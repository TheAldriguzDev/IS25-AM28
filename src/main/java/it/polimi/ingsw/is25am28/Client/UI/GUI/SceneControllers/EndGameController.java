package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Client.UI.GUI.GUIHandler;
import it.polimi.ingsw.is25am28.Model.ActionJSON.State.EndGameDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.*;

public class EndGameController extends GUIController {
    @FXML private VBox leaderBoard;
    @FXML private Button quitButton;

    public void setLeaderBoard(EndGameDTO state) {
        if (state.getWinner() != null) {
            Label label = new Label(state.getWinner() + " won the game!");
            label.getStyleClass().add("winner-label");

            leaderBoard.getChildren().add(label);
        }

//TODO: color through model

        List<String> orderedPlayers = new ArrayList<>();
        orderedPlayers = state.getPlayersPositionResult().entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        List<String> placements = Arrays.asList("1st", "2nd", "3rd", "4th");

        for (int i = 0; i < orderedPlayers.size(); i++) {
            int credits = state.getPlayersCredits().get(orderedPlayers.get(i));
            String text = placements.get(i) + " - " + orderedPlayers.get(i) + " (Final credits: " + credits + ")";

            Label label = new Label(text);
            label.getStyleClass().add("leaderBoard-label");

            this.leaderBoard.getChildren().add(label);

        }
    }

    public void onQuitButtonClick(ActionEvent actionEvent) {
        GUIHandler.getInstance().getStage().close();
        System.exit(0);
    }

}
