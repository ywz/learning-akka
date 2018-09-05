package ch2.pong;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.junit.Test;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static org.junit.Assert.assertEquals;

public class PongActorTest {
    ActorSystem system = ActorSystem.create();

    ActorRef actorRef = system.actorOf(Props.create(JavaPongActor.class), "BruceWillis");

    @Test
    public void shouldReplyToPingWithPong() throws Exception {
        Future sFuture = ask(actorRef, "Ping", 1000);
        final CompletionStage<String> cs = FutureConverters.<String>toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        assertEquals("Pong", jFuture.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test(expected = ExecutionException.class)
    public void shouldReplyToUnknownMessageWithFailure() throws Exception {
        Future sFuture = ask(actorRef, "unknown", 1000);
        final CompletionStage<String> cs = FutureConverters.<String>toJava(sFuture);
        final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
        jFuture.get(1000, TimeUnit.MILLISECONDS);
    }

    //Future Examples

    /**
     * 可以使用thenAccept 来操作返回结果。
     *
     * @throws Exception
     */
    @Test
    public void shouldPrintToConsole() throws Exception {
        askPong("Ping").thenAccept(x -> System.out.println("replied with: " + x));
        Thread.sleep(100);
        //no assertion - just prints to console. Try to complete a CompletableFuture instead.
    }

    /**
     * 类型转换可以通过map 来完成，例如Scala 的Future。在Java 8 中，我们调用thenApply
     *
     * @throws Exception
     */
    @Test
    public void shouldTransform() throws Exception {
        char result = (char) get(askPong("Ping").thenApply(x -> x.charAt(0)));
        assertEquals('P', result);
    }

    /**
     * There is was a bug with the scala-java8-compat library 0.3.0 - thenCompose throws exception
     * https://github.com/scala/scala-java8-compat/issues/26
     * <p>
     * I confirmed fixed in 0.6.0-SNAPSHOT (10 months later). Just in time for publishing!
     * 链式异步操作。在Java 中使用thenCompose
     */
    @Test
    public void shouldTransformAsync() throws Exception {
        CompletionStage cs = askPong("Ping").
                thenCompose(x -> askPong("Ping"));
        assertEquals(get(cs), "Pong");
    }

    /**
     * 在Java 8 中，没有面向用户的用于失败处理的方法，因此我们在这里引入handle()来处理这种情况
     *
     * @throws Exception
     */
    @Test
    public void shouldEffectOnError() throws Exception {
        askPong("cause error").handle((x, t) -> {
            if (t != null) {
                System.out.println("Error: " + t);
            }
            return null;
        });

        askPong("Ping").handle((x, t) -> {
            if (null != t) {
                System.out.println("Error: " + t);
            }
            return x;
        }).thenAccept(y -> System.out.println(y));
    }

    /**
     * 使用exceptionally 将Throwable 转换为一个可用的值
     *
     * @throws Exception
     */
    @Test
    public void shouldRecoverOnError() throws Exception {
        CompletionStage<String> cs = askPong("cause error").exceptionally(t -> {
            return "default";
        });

        String result = (String) get(cs);
    }

    /**
     * 从异常恢复
     *
     * @throws Exception
     */
    @Test
    public void shouldRecoverOnErrorAsync() throws Exception {
        CompletionStage<String> cf = askPong("cause error").handle((pong, ex) -> ex == null ? CompletableFuture.completedFuture(pong) : askPong("Ping")).thenCompose(x -> x);
        assertEquals("Pong", get(cf));

        CompletionStage<String> cs = askPong("Ping").handle((pong, ex) -> ex == null ? CompletableFuture.completedFuture(pong) : askPong("Ping")).thenCompose(x -> x);
        assertEquals("Pong", get(cs));
    }

    /**
     * 链式调用
     *
     * @throws Exception
     */
    @Test
    public void shouldChainTogetherMultipleOperations() throws Exception {
        CompletionStage<String> cs = askPong("Ping").thenCompose(x -> askPong("Ping" + x)).handle((x, t) -> t != null ? "default" : x);
        assertEquals("default", get(cs));
    }

    @Test
    public void shouldPrintErrorToConsole() throws Exception {
        askPong("cause error").handle((x, t) -> {
            if (t != null) {
                System.out.println("Error: " + t);
            }
            return null;
        });
        Thread.sleep(100);
    }

    /**
     * 我们经常需要访问执行的多个Future。同样有很多方法可以用来处理这些情况。
     * 在Java 中，可以使用CompletableFuture 的thenCompose 方法，在Future 的值可用时访问到这些值
     *
     * @throws Exception
     */
    @Test
    public void shouldCombineFutures() throws Exception {
        CompletionStage<String> cs = askPong("Ping").thenCombine(askPong("Ping"), (a, b) -> {
            return a + b;
        });
        assertEquals("PongPong", get(cs));
    }

    //Helpers
    public Object get(CompletionStage cs) throws Exception {
        return ((CompletableFuture<String>) cs).get(1000, TimeUnit.MILLISECONDS);
    }

    public CompletionStage<String> askPong(String message) {
        Future sFuture = ask(actorRef, message, 1000);
        final CompletionStage<String> cs = FutureConverters.<String>toJava(sFuture);
        return cs;
    }
}
