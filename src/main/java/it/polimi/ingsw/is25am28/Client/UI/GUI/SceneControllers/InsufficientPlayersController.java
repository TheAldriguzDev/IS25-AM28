package it.polimi.ingsw.is25am28.Client.UI.GUI.SceneControllers;

import it.polimi.ingsw.is25am28.Model.ActionJSON.State.InsufficientPlayer.InsufficientPlayerDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class InsufficientPlayersController extends GUIController {
    @FXML
    public Label countDownToWin;

    private int countDown;
    private Timeline timer;

    public void setCountDown(InsufficientPlayerDTO dto) {
        this.countDown = dto.getCountdown() / 1000;

        // Check if there is already an active timer
        if (timer != null) {
            timer.stop();
        }

        // Update the text every second
        timer = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            // The server will send the EndGameDTO to inform of the victory
            if (countDown <= 0) {
                timer.stop();
                return;
            }

            int minutes = countDown / 60;
            int seconds = countDown % 60;

            String timeFormatted = String.format("%02d:%02d", minutes, seconds);
            countDownToWin.setText("Waiting for others to reconnect [" + timeFormatted + "] — you'll win if time runs out!");

            countDown--;
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
}
