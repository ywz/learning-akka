package ch2.com.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main {
    public static void main(String... args) {
        Config config = ConfigFactory.load("ch2_server_application.conf");
        ActorSystem system = ActorSystem.create("akkademy", config);
        ActorRef actor = system.actorOf(Props.create(AkkademyDb.class), "akkademy-db");
    }
}
