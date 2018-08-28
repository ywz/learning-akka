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
        ClusterSharding.get(system).startProxy("Counter", Optional.empty(), Counter.getMessageExtractor());
        ActorRef counterRegion = ClusterSharding.get(system).shardRegion("Counter");
        for (int i = 0; i < 10; i++) {
            counterRegion.tell(new Counter.EntityEnvelope(123,
                    Counter.CounterOp.INCREMENT), ActorRef.noSender());
        }

        ActorRef printer = system.actorOf(Props.create(PrintActor.class), "printer");
        ClusterClientReceptionist.get(system).registerService(printer);

        // printer sharding
//        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
//        ClusterSharding.get(system).start("printer", Props.create(Printer.class, "printer3"), settings.withRole("printer"), Printer.getMessageExtractor());
//        ActorRef printerRegion = ClusterSharding.get(system).shardRegion("printer");
//        printerRegion.tell(new Printer.Message("111", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("222", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("333", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("444", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("555", "2553"), ActorRef.noSender());
    }
}