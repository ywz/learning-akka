package ch7.com.akka;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.CircuitBreaker;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static scala.compat.java8.FutureConverters.toJava;

/**
 * Modified version of the new project from typesafe demonstrating mailbox config
 * and circuitbreaker
 */

public class ApplicationMain {

    public static void main(String[] args) {

        Config config = ConfigFactory.load("ch7_mailbox_application.conf");
        ActorSystem system = ActorSystem.create("MyActorSystem", config);
        ActorRef pingActorWithMailbox = system.actorOf(PingActor.props().withMailbox("akka.actor.boundedmailbox"), "pingActor");

        pingActorWithMailbox.tell(new PingActor.Initialize(), null);

        ActorRef pongActor = system.actorOf(PingActor.props().withMailbox("akka.actor.boundedmailbox"), "pingActor2");

        CircuitBreaker breaker =
                new CircuitBreaker(system.scheduler(),
                        1,
                        FiniteDuration.create(1, "second"),
                        FiniteDuration.create(1, "second"),
                        system.dispatcher()).
                        onOpen(() -> {
                            System.out.println("circuit breaker opened!");
                        }).
                        onClose(() -> {
                            System.out.println("circuit breaker closed!");
                        }).
                        onHalfOpen(() -> {
                            System.out.println("circuit breaker half opened!");
                        });

        // Timeout timeout = Timeout.longToTimeout(2000L);
        Timeout timeout = Timeout.apply(2000L, TimeUnit.MILLISECONDS);


        Future future1 = breaker.callWithCircuitBreaker(()
                -> Patterns.ask(pongActor, new PingActor.PingMessage("ping"), timeout));

        Future future2 = breaker.callWithCircuitBreaker(()
                -> Patterns.ask(pongActor, new PingActor.PingMessage("ping"), timeout));


        toJava(future1).handle((x, t) -> {
            if (t != null) {
                System.out.println("got it: " + x);
            } else {
                System.out.println("error: " + t.toString());
            }
            return null;
        });

        toJava(future2).handle((x, t) -> {
            if (t != null) {
                System.out.println("got it: " + x);
            } else {
                System.out.println("error: " + t.toString());
            }
            return null;
        });

        //play around with sending futures and see how the breaker responds

        // system.awaitTermination();
        system.terminate();
    }

}
