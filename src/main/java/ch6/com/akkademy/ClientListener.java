package ch6.com.akkademy;

import akka.actor.AbstractActor;
import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.cluster.client.ContactPointAdded;
import akka.cluster.client.ContactPointRemoved;
import akka.cluster.client.ContactPoints;
import akka.cluster.client.SubscribeContactPoints;

import java.util.HashSet;
import java.util.Set;

public class ClientListener extends AbstractActor {
    private final ActorRef targetClient;

    private final Set<ActorPath> contactPoints = new HashSet<>();

    public ClientListener(ActorRef targetClient) {
        this.targetClient = targetClient;
    }

    @Override
    public void preStart() {
        targetClient.tell(SubscribeContactPoints.getInstance(), sender());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ContactPoints.class, msg -> {
            contactPoints.addAll(msg.getContactPoints());
            // Now do something with an up-to-date "contactPoints"
            System.out.println("addAll");
        }).match(ContactPointAdded.class, msg -> {
            contactPoints.add(msg.contactPoint());
            // Now do something with an up-to-date "contactPoints"
            System.out.println("add");
        }).match(ContactPointRemoved.class, msg -> {
            contactPoints.remove(msg.contactPoint());
            // Now do something with an up-to-date "contactPoints"
            System.out.println("remove");
        }).build();
    }
}
