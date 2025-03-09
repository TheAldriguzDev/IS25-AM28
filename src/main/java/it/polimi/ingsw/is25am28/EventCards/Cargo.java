package it.polimi.ingsw.is25am28.EventCards;

public enum Cargo {
        RED(4), YELLOW(3), GREEN(2), BLUE(1);

        private final int cargoValue;

        Cargo(int cargoValue) {
            this.cargoValue = cargoValue;
        }

        public int getCargoValue() {
            return cargoValue;
        }
}

// delete
