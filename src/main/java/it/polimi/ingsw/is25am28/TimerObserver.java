package it.polimi.ingsw.is25am28;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class TimerObserver {
      private final HashSet<TimeSubscriber> subscribed; 
      private Thread thd;

      public TimerObserver(){
            this.subscribed = new HashSet<>();
      }

      private Thread createSleepThread(){

            return new Thread(() -> {
                  try {
                        TimeUnit.MINUTES.sleep(2);
                        if( subscribed.size() > 0 )
                              subscribed.forEach( sub -> sub.onTimerEnd() );
                  }catch(InterruptedException e){

                  }
            });
      }

      public TimerObserver observe( TimeSubscriber sub ){
            subscribed.add( sub );
            return this;
      }

      public TimerObserver stopObserve( TimeSubscriber sub ){
            subscribed.remove(sub);
            return this;
      }

      public TimerObserver flip(){
            if( thd.isAlive() ){
                  try{
                        thd.join( (long)0.0001 );
                  }catch(InterruptedException e){

                  }
            }

            try {
                  thd = createSleepThread();
                  thd.start();
            }finally{

            }

            return this;
      }
      
}
