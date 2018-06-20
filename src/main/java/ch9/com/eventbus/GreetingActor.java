package ch9.com.eventbus;

import akka.actor.AbstractActor;

public class GreetingActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().
                matchAny(x -> System.out.println("guess we received a greeting! msg: " + x)).build();
    }
}
