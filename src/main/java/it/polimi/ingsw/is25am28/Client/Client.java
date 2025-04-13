package it.polimi.ingsw.is25am28.Client;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.StateDTO;
import it.polimi.ingsw.is25am28.RMI.Client.RMIClient;
import it.polimi.ingsw.is25am28.VirtualView;

import java.util.Scanner;
import java.util.UUID;

public class Client {

    private StateDTO currentState;




    public static void main(String[] args) throws Exception {
        // Will store the specific network implementation (RMI / Socket)
        VirtualView client;

        Scanner scanner = new Scanner(System.in);
        int connectionType = -1;
        int uiType = -1;

        System.out.flush();

        do {
            System.out.println("Choose a connection type: ");
            System.out.println("1 --> RMI");
            System.out.println("2 --> Socket (Coming soon)");
            connectionType = scanner.nextInt();

            if (connectionType == -1) {
                System.err.println("Invalid connection type, the value must be 1 or 2!");
            }
        } while (connectionType == -1);


        do {
            System.out.println("Choose an ui type: ");
            System.out.println("1 --> TUI");
            System.out.println("2 --> GUI (Coming soon)");
            uiType = scanner.nextInt();

            if (uiType == -1) {
                System.err.println("Invalid ui type, the value must be 1 or 2!");
            }
        } while (uiType == -1);

        System.out.print("\033[H\033[2J");
        System.out.flush();

        if (connectionType == 1) {
            // Create the client with an unique UUID as identifier. This will be used server side for sendTo communication
            client = new RMIClient(args[0], 7777, UUID.randomUUID());
        }
    }
}
