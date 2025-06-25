package it.polimi.ingsw.is25am28.Client.ClientModel.ClientPlayer;

import it.polimi.ingsw.is25am28.Client.ClientModel.ClientShip.ClientShip;
import it.polimi.ingsw.is25am28.Model.Player.PlayerColor;

import java.util.List;
import java.util.Map;

public class ClientPlayer {
    private  String nickname;
    private  PlayerColor color;
    private int credits;
    private int lostComponents;
    private int cursor;
    private ClientShip ship;

    // Constructor #1
    public ClientPlayer(String nickname, PlayerColor color, int level) {
        this.nickname = nickname;
        this.color = color;
        this.credits = 0;
        this.lostComponents = 0;
        this.cursor = 0;

        this.ship = new ClientShip(level, color);
    }

    // Constructor #2
    public ClientPlayer(
            String nickname,
            PlayerColor color,
            int level,
            int credits,
            int lostPieces,
            List<Map<String, Object>> ship
    ) {
        this.nickname = nickname;
        this.color = color;
        this.credits = credits;
        this.lostComponents = lostPieces;
        this.cursor = 0;
        this.ship = new ClientShip(level, ship, color);
    }

    /**
     * @return a String that represent the Player nickname
     * */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * @return the PlayerColor
     * */
    public PlayerColor getColor() {
        return this.color;
    }

    /**
     * @return the credits amount of the Player
     * */
    public int getCredits() {
        return this.credits;
    }

    /**
     * @param credits is the number of credits that needs to be set
     * */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * @param numCredits is the amount of credits that needs to be added or removed
     * */
    public void modifyCredits(int numCredits) {
        this.credits += numCredits;
    }

    /**
     * @return the number of lost components
     * */
    public int getLostComponents() {
        return this.lostComponents;
    }

    /**
     * @param lostComponents is the amount of components that needs to be added to the total
     * */
    public void addLostComponent(int lostComponents) {
        this.lostComponents += lostComponents;
    }

    /**
     * @return the current cursors of the Player, this value represent his position
     * */
    public int getCursor() {
        return this.cursor;
    }

    /**
     * @param cursor is the current cursors of the Player
     * */
    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    /**
     * @param lostPieces is the current amount the player's lost components
     */
    public void setLostComponents(int lostPieces) {
        this.lostComponents = lostPieces;
    }

    /**
     * @return the ClientShip
     * */
    public ClientShip getShip() {
        return this.ship;
    }
}
