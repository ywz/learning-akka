package ch9.com.eventbus;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class EventBusMain {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MyActorSystem");
        ActorRef greetingActor = system.actorOf(Props.create(GreetingActor.class));
        JavaLookupClassifier lookupClassifier = new JavaLookupClassifier();
        lookupClassifier.subscribe(greetingActor, "java-greetings");

        // error topic
        lookupClassifier.publish(new EventBusMessage("time", String.valueOf(System.currentTimeMillis())));

        // correct topic
        lookupClassifier.publish(new EventBusMessage("java-greetings", "java event bus greeting"));

        // map size
        System.out.println("map size: " + lookupClassifier.mapSize());
    }
}
