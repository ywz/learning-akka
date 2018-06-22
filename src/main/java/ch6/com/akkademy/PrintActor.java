package ch6.com.akkademy;

import akka.actor.AbstractActor;

public class PrintActor extends AbstractActor {

    @Override
    public void preStart() throws Exception {
        System.out.println("start: " + getSelf().path());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                matchAny(x -> System.out.println("a new msg: " + x)).build();
    }
}
