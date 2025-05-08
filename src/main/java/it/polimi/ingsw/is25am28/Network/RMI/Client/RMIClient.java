package it.polimi.ingsw.is25am28.Network.RMI.Client;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientModel;
import it.polimi.ingsw.is25am28.Client.UI.ClientUI;
import it.polimi.ingsw.is25am28.Client.UI.TUI.TUIHandler;
import it.polimi.ingsw.is25am28.Client.ViewUpdater;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.*;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.DisconnectedPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.ShipConstruction.*;
import it.polimi.ingsw.is25am28.Network.Answer.Answer;
import it.polimi.ingsw.is25am28.Network.Answer.ErrorAnswer;
import it.polimi.ingsw.is25am28.Network.Messages.Message;
import it.polimi.ingsw.is25am28.Network.Messages.Ping;
import it.polimi.ingsw.is25am28.Network.Queue.Queue;
import it.polimi.ingsw.is25am28.Network.RMI.Server.VirtualViewRMI;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class RMIClient extends UnicastRemoteObject implements VirtualViewRMI {
    private VirtualServerRMI server;
    private final ViewUpdater viewUpdater;
    private final Queue queueHandler;

    private final ExecutorService inputThread;
    private final ExecutorService updateThread;
    private final ExecutorService forceThread;
    private final ScheduledExecutorService pingScheduler;

    private final UUID uuid;


    /**
     * Constructor used to create the RMIClient and starts it
     * */
    public RMIClient(String ipAddress, int port, UUID uuid, ClientUI ui, ClientModel model) throws Exception, RemoteException {
        super();

        // Args validation
        if (ipAddress == null || ipAddress.isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port OutOfBound [0, 65535]: " + port);
        }
        if (uuid == null) {
            throw new IllegalArgumentException("UUI cannot be null");
        }

        this.uuid = uuid;

        final String serverName = "GameRMIServer";
        this.server = this.lookUpForServer(ipAddress, port, serverName);
        if (this.server == null) {
            throw new RemoteException("Server unavailable: " + serverName);
        }

        // Init the viewUpdater
        this.viewUpdater = new ViewUpdater(ui, model);

        // Create the queue handler to process in a thread the communication with the server
        this.queueHandler = new Queue();
        new Thread(queueHandler).start();

        this.inputThread = Executors.newSingleThreadExecutor();
        this.updateThread = Executors.newSingleThreadExecutor();
        this.forceThread = Executors.newSingleThreadExecutor();
        this.pingScheduler = Executors.newSingleThreadScheduledExecutor();

        this.run();

        // Method used by the client to ping the server
        this.pingServer();
    }

    private VirtualServerRMI lookUpForServer(String ipAddress, int port, String serverName) {
        try {
            Registry registry = LocateRegistry.getRegistry(ipAddress, port);
            return (VirtualServerRMI) registry.lookup(serverName);
        } catch (RemoteException e) {
            System.err.println("Server unavailable: " + e.getMessage());
        } catch (NotBoundException e) {
            System.err.println("Server not bound: " + e.getMessage());
        }
        return null;
    }

    /**
     * Method used to connect the client to the server
     * */
    private void run() throws Exception, RemoteException {
        queueHandler.enqueue(() -> {
            try {
                this.server.connectClient(this, this.uuid);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * This method will ping every 5 seconds the server
     * */
    private void pingServer() {
        this.pingScheduler.scheduleAtFixedRate(() -> {
            queueHandler.enqueue(() -> {
                try {
                    this.sendMessage(new Ping());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    /**
     * Method used to send Messages to the client
     * */
    public void sendMessage(Message message) throws Exception {
        queueHandler.enqueue(() -> {
            try {
                server.sendMessage(message, this.uuid);
            } catch (RemoteException e) {
                System.out.println("\n[Server offline] The connection with the server has been lost");
                System.exit(1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Method used to update the client view (display the new content)
     * */
    @Override
    public void updateView(StateDTO state) throws RemoteException {
//        this.updateThread.submit(() -> {
//            try {
//                state.accept(viewUpdater);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
    }

    // TODO: Maybe is better to put the interrupting events on a separate thread, so in every moment that they arrive, we can switch without any issue

    @Override
    public void updateState(Answer answer) {
        StateDTO state = answer.getState();
        StateDTO nextState = answer.getNextState();
        String nickname = answer.getPlayerNickname();

        CompletableFuture<Void> future = new CompletableFuture<>();

//        if (state != null) {
//            System.out.println("STATE: " + state.getStateName() + " (" + state + ")");
//        }
//        if (nextState != null) {
//            System.out.println("NEXT_STATE: " + nextState.getStateName() + " (" + nextState + ")");
//        }

        switch (state) {
            // Update the current state of the game
            case ConstructionComponentDTO _, PlacedComponentDTO _, TimerDTO _, PopulateShipComponentDTO _,
                 ConstructionDeckDTO _ -> { // TODO: Timer should be removed from here
                future = CompletableFuture.runAsync(() -> {
                    try {
                        state.accept(viewUpdater);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, updateThread);

                // Then commit the command
                if (nickname != null && viewUpdater.isCTXAvailable()) { // Try to commit the message only if it's present --> otherwise is not smart to potentially lock the program
                    future = future.thenRunAsync(() -> {
                        try {
                            viewUpdater.commitCommand(nickname);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while commiting the command: ", e);
                        }
                    }, inputThread);
                }
            }
            case PlayerEndedShipDTO _ -> {
                if (nextState == null) {
                    future = CompletableFuture.runAsync(() -> {
                        try {
                            state.accept(viewUpdater);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, updateThread);

                    // Then commit the command
                    if (nickname != null && viewUpdater.isCTXAvailable()) { // Try to commit the message only if it's present --> otherwise is not smart to potentially lock the program
                        future = future.thenRunAsync(() -> {
                            try {
                                viewUpdater.commitCommand(nickname);
                            } catch (Exception e) {
                                throw new RuntimeException("Error while commiting the command: ", e);
                            }
                        }, inputThread);
                    }
                }
            }
            case DisconnectedPlayerDTO _ -> {
                future = CompletableFuture.runAsync(() -> {
                    try {
                        state.accept(viewUpdater);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, updateThread);
            }
            case CardRoundDTO data -> {
                if (nextState != null) {
                    future = CompletableFuture.runAsync(() -> {
                        try {
                            viewUpdater.updateCardResult(data);
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                    future = future.thenRunAsync(() -> {
                        try {
                            viewUpdater.interruptCurrScreen();
                        }
                        catch (Exception e) {
                            throw e;
                        }
                    });
                }
                else {
                    future = CompletableFuture.runAsync(() -> {
                        try {
                            viewUpdater.interruptCurrScreen();
                        }
                        catch (Exception e) {
                            throw e;
                        }
                    });
                }

                // If the nickname is present, commit the command first
                if (nickname != null && viewUpdater.isCTXAvailable()) {
                    future = future.thenRunAsync(() -> {
                        try {
                            viewUpdater.commitCommand(nickname);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while commiting the command: ", e);
                        }
                    }, inputThread);
                } else {
                    future = CompletableFuture.completedFuture(null);
                }

                future = future.thenRunAsync(() -> {
                    try {
                        Objects.requireNonNullElse(nextState, state).accept(viewUpdater);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while executing the state: ", e);
                    }
                }, inputThread);
            }
            case null -> {
                // If the nickname is present, commit the command first
                if (nickname != null && viewUpdater.isCTXAvailable()) {
                    future = CompletableFuture.runAsync(() -> {
                        try {
                            viewUpdater.commitCommand(nickname);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while commiting the command: ", e);
                        }
                    }, inputThread);
                } else {
                    future = CompletableFuture.completedFuture(null);
                }
            }
            default -> {
                // If the nickname is present, commit the command first
                if (nickname != null && viewUpdater.isCTXAvailable()) {
                    future = CompletableFuture.runAsync(() -> {
                        try {
                            viewUpdater.commitCommand(nickname);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while commiting the command: ", e);
                        }
                    }, inputThread);
                } else {
                    future = CompletableFuture.completedFuture(null);
                }

                future = future.thenRunAsync(() -> {
                    try {
                        state.accept(viewUpdater);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while executing the state: ", e);
                    }
                }, inputThread);
            }
        }

        switch (nextState) {
            case InsufficientPlayerDTO _-> {
                forceThread.submit(() -> {
                    try {
                        nextState.accept(viewUpdater);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while executing the next state: ", e);
                    }
                });
            }
            case FixShipDTO _, PopulateShipDTO _, CardRoundDTO _, EndGameDTO _ -> {
                CompletableFuture<Void> completableFuture;

                completableFuture = CompletableFuture.runAsync(
                    this.viewUpdater::interruptCurrScreen,
                    forceThread
                );

                completableFuture = completableFuture.thenRunAsync(
                    () -> {
                        try {
                            nextState.accept(viewUpdater);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while executing the next state: ", e);
                        }
                    },
                    inputThread
                );
            }
            case null -> {}
            default -> {
                future = future.thenRunAsync(() -> {
                    try {
                        nextState.accept(viewUpdater);
                    } catch (Exception e) {
                        throw new RuntimeException("Error while executing the next state: ", e);
                    }
                }, inputThread);
            }
        }
    }

    @Override
    public void reportError(ErrorAnswer error) throws RemoteException {
        inputThread.submit(() -> {
            viewUpdater.reportError(error.getError());
        });
    }
}
