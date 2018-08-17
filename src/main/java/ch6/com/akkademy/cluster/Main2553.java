package ch6.com.akkademy.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.sharding.ClusterSharding;
import chx.com.akka.sharding.Counter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Optional;

public class Main2553 {
    public static void main(String[] args) {
        Config config = ConfigFactory.load("ch6_demaid_application_2553.conf");
        ActorSystem system = ActorSystem.create("Akkademy", config);
        ActorRef startedCounterRegion = ClusterSharding.get(system).startProxy("Counter", Optional.empty(), Counter.getMessageExtractor());

        ActorRef printer = system.actorOf(Props.create(PrintActor.class), "printer");
        ClusterClientReceptionist.get(system).registerService(printer);

        ActorRef counterRegion = ClusterSharding.get(system).shardRegion("Counter");
        for (int i = 0; i < 10; i++) {
            counterRegion.tell(new Counter.EntityEnvelope(123,
                    Counter.CounterOp.INCREMENT), ActorRef.noSender());
        }
    }
}