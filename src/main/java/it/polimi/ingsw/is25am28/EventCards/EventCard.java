package it.polimi.ingsw.is25am28.EventCards;

abstract class EventCard {
    private final String name;
    private final int cardLevel;

    public EventCard(String name, int cardLevel) {
        this.name = name;
        this.cardLevel = cardLevel;
    }

    public String getCardName() {
        return name;
    }

    public int getCardLevel() {
        return cardLevel;
    }

    abstract void useCard(Player[] players);

    abstract void bonusEffect();

    abstract void malusEffect();
}
