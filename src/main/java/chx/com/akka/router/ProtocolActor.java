package chx.com.akka.router;

import akka.actor.AbstractActor;

public class ProtocolActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(x -> System.out.println(getSelf().path() + ": " + x)).build();
    }
}
