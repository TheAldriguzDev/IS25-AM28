package it.polimi.ingsw.is25am28.TUI;

import java.util.List;

public class WidgetTUI {
    private List<String> screen;
    private int height;
    private int width;

    // Empty constructor
    public WidgetTUI() {
        this.height = 0;
        this.width = 0;
        this.screen = null;
    }

    // WidgetTUI constructor without the screen content set
    public WidgetTUI(int height, int width) {
        this.height = height;
        this.width = width;
        this.screen = null;
    }

    // WidgetTUI constructor with the screen content set
    public WidgetTUI(int height, int width, List<String> screen) {
        this.height = height;
        this.width = width;
        this.screen = screen;
    }

    /**
     * @param height The height to set this widget to
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return This widget's height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @param width The width to set this widget to
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return this widget's width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Sets the screen stored by this widget, which is a list of strings
     * that describe the text to print to terminal
     *
     * @param screen The list of string that make up this widget's output screen
     */
    public void setScreen(List<String> screen) {
        this.screen = screen;
    }

    /**
     * @return This widget's stored screen
     */
    public List<String> getScreen() {
        return this.screen;
    }
}
