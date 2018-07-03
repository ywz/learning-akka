package chx.com.akka.ask;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.PatternsCS.ask;

public class AskMain {
    public static void main(String[] args) {
        Timeout t = Timeout.create(Duration.ofSeconds(5));
        ActorSystem system = ActorSystem.create();
        ActorRef actorA = system.actorOf(Props.create(ActorA.class), "A");
        ActorRef actorB = system.actorOf(Props.create(ActorB.class), "B");

        CompletableFuture<Object> future1 = ask(actorA, "hello", t).toCompletableFuture();
        System.out.println((String) future1.join());

        CompletableFuture<Object> future2 = ask(actorB, "hi", t).handle((result, ex) -> {
            if (null != ex) {
                System.out.println("error: " + ex.getMessage());
                return null;
            }
            return result;
        }).toCompletableFuture();
        System.out.println(future2.join());
        system.terminate();
    }
}
