package it.polimi.ingsw.is25am28.Model.Exceptions;

public class SelectedConcurrencyException extends TargetException {

      public SelectedConcurrencyException( String player ){
            super( player, "error in selection of component");
      }
}