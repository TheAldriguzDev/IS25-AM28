package it.polimi.ingsw.is25am28.TimeObserver;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class TimerObserver {
      private final HashSet<TimeSubscriber> subscribed;
      private int timeout;
      private Thread thd;

      private boolean finished = true;

      public TimerObserver( int timeoutMillis ){
            this.subscribed = new HashSet<>();
            this.timeout = timeoutMillis;
      }

      private Thread createSleepThread(){
            
            return new Thread(() -> {
                  try {
                        TimeUnit.MILLISECONDS.sleep(timeout);
                        finished = true;
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

      public Boolean hasFinished(){
            return finished;
      }

      public TimerObserver flip(){
            if( !finished )
                  return this;
            
            finished = false;

            if( thd != null && thd.isAlive() ){
                  thd.interrupt();
            }

            try {
                  thd = createSleepThread();
                  thd.start();
            }finally{

            }

            return this;
      }

      public TimerObserver setTimeout( int timeout ){
            this.timeout = timeout;
            return this;
      }
}