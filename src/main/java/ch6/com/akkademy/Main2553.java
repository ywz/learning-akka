package ch6.com.akkademy;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main2553 {
    public static void main(String[] args) {
        Config config = ConfigFactory.load("ch6_demaid_application_2553.conf");
        ActorSystem system = ActorSystem.create("Akkademy", config);
    }
}
