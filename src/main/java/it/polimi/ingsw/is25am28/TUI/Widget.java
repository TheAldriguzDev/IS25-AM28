package it.polimi.ingsw.is25am28.TUI;

import java.util.ArrayList;
import java.util.List;

public class Widget {
    private List<String> screen;
    private int height;
    private int width;

    public Widget(int height, int width) {
        this.height = height;
        this.width = width;
        screen = new ArrayList<>();
    }

    public void setScreen(List<String> screen) {
        this.screen = screen;
    }

}
