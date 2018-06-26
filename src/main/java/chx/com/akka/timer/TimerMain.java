package chx.com.akka.timer;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class TimerMain {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("timer");
        system.actorOf(Props.create(TimerActor.class), "timerActor");
    }
}
