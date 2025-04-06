package it.polimi.ingsw.is25am28.TimeObserver;
import java.util.List;
public interface TimeEndedNotifier {
      void sendTimeEndedNotification( List<String> players );
}
