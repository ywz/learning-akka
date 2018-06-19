//package ch5.com.akkademy;
//
//import akka.actor.ActorRef;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//import akka.testkit.TestActorRef;
//import org.junit.Test;
//
//import java.util.concurrent.CompletableFuture;
//
//public class TestCameoActorCaller {
//
//    ActorSystem system = ActorSystem.create();
//
//    CompletableFuture<String> completableFuture = new CompletableFuture();
//
//    TestActorRef<TestCameoActor> actorRef = TestActorRef.create(system, Props.create(TestCameoActor.class, completableFuture));
//
//    @Test
//    public void shouldComplete() {
//        for (int i = 0; i < 2000; i++) {
//            actorRef.tell("blah", ActorRef.noSender());
//        }
//        TestCameoActor testCameoActor = actorRef.underlyingActor();
//        assert (testCameoActor.futureToComplete.isDone());
//    }
//
//    @Test
//    public void doNotCompleted() {
//        for (int i = 0; i < 1999; i++) {
//            actorRef.tell("blah", ActorRef.noSender());
//        }
//        TestCameoActor testCameoActor = actorRef.underlyingActor();
//        assert (!testCameoActor.futureToComplete.isDone());
//    }
//}
