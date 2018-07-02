package chx.com.akka.ask;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

public class ActorA extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(a -> sender().tell("ok", ActorRef.noSender()))
                .build();
    }
}
