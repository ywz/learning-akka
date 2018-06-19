package ch5.com.akkademy;

import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Complete future after 2000 messages. Used for testing.
 */
public class TestCameoActor extends AbstractActor {
    final CompletableFuture futureToComplete;
    private List<String> articles = new ArrayList<>();

    private TestCameoActor(CompletableFuture futureToComplete) {
        this.futureToComplete = futureToComplete;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(String.class, x -> {
                            articles.add(x);
                            if (articles.size() == 2000) {
                                futureToComplete.complete("OK!");
                                System.out.println("ok");
                            }
                        }
                ).build();
    }
}

