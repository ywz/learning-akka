package chx.com.akka.timer;

import akka.actor.AbstractActorWithTimers;

import java.time.Duration;

public class TimerActor extends AbstractActorWithTimers {

    private static Object TICK_KEY = "TickKey";

    private static final class Tick {
    }

    private static final class FirstTick {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(FirstTick.class, tick -> {
                    System.out.println("first tick");
                    getTimers().startPeriodicTimer(TICK_KEY, new Tick(), Duration.ofSeconds(1));
                })
                .match(Tick.class, tick -> System.out.println(System.currentTimeMillis()))
                .build();
    }

    public TimerActor() {
        getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofMillis(500));
    }
}
