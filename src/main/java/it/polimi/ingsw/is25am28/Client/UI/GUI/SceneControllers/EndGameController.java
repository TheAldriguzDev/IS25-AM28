package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.EndGameDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.*;


/**
 * The EndGameController class is responsible for managing the end-game screen in a GUI application.
 */
public class EndGameController extends GUIController {
    @FXML private VBox leaderBoard;
    @FXML private Button quitButton;

    /**
     * Creates the leaderBoard showing each player's credits
     * @param state Containing the necessary info to create the leaderBoard
     */
    public void setLeaderBoard(EndGameDTO state) {
        if (state.getWinner() != null) {
            Label label = new Label(state.getWinner() + " won the game!");
            label.getStyleClass().add("winner-label");

            leaderBoard.getChildren().add(label);
        }

        List<String> orderedPlayers = state.getPlayersPositionResult().entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList().reversed();

        List<String> placements = Arrays.asList("1st", "2nd", "3rd", "4th");

        for (int i = 0; i < orderedPlayers.size(); i++) {
            int credits = state.getPlayersCredits().get(orderedPlayers.get(i));
            String text = placements.get(i) + " - " + orderedPlayers.get(i) + " (Final credits: " + credits + ")";

            Label label = new Label(text);
            label.getStyleClass().add("leaderBoard-label");

            this.leaderBoard.getChildren().add(label);

        }
    }

    /**
     * Handles the event triggered by clicking the "Quit" button.
     * This method closes the application window and terminates the program.
     *
     * @param actionEvent The action event triggered by clicking the "Quit" button.
     */
    public void onQuitButtonClick(ActionEvent actionEvent) {
        GUIHandler.getInstance().getStage().close();
        System.exit(0);
    }

}
