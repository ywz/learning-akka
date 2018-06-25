package ch6.com.akkademy.cluster;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;

public class Publisher extends AbstractActor {

    // activate the extension
    ActorRef mediator =
            DistributedPubSub.get(getContext().system()).mediator();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, in -> {
                    String out = in.toUpperCase();
                    mediator.tell(new DistributedPubSubMediator.Publish("content", out),
                            getSelf());
                })
                .build();
    }

}
