package it.polimi.ingsw.is25am28.TimeObserver;


import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class TimerObserver {
      private final HashSet<TimeSubscriber> subscribed;
      private Thread thd;

      private long lastTimestamp = 0;

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
            long newTimestamp = ZonedDateTime.now().toInstant().toEpochMilli();

            // request sent in less than a second. 
            // refused for race condition between players
            if( newTimestamp - lastTimestamp < 1000 )
                  return this;
            
            lastTimestamp = newTimestamp;

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