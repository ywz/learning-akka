package chx.com.akka.io;

import akka.actor.AbstractActor;

public class ClientListener extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().
                matchAny(x -> System.out.println("new msg: " + x)).build();
    }
}
