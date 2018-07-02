package chx.com.akka.ask;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.concurrent.TimeUnit;

public class ActorB extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(a -> {
                    TimeUnit.SECONDS.sleep(6);
                    sender().tell("ng", ActorRef.noSender());
                })
                .build();
    }
}
