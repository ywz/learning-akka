package ch6.com.akkademy.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.japi.Option;
import ch5.com.akkademy.ArticleParseActor;
import chx.com.akka.sharding.Counter;
import chx.com.akka.sharding.Printer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main2551 {
    public static void main(String... args) {
        Config config = ConfigFactory.load("ch6_demaid_application_2551.conf");
        ActorSystem system = ActorSystem.create("Akkademy", config);
        ActorRef clusterController = system.actorOf(Props.create(ClusterController.class), "clusterController");
        ActorRef worker = system.actorOf(Props.create(ArticleParseActor.class), "worker");
        ClusterClientReceptionist.get(system).registerService(worker);

        ActorRef printer = system.actorOf(Props.create(PrintActor.class), "printer");
        ClusterClientReceptionist.get(system).registerService(printer);

        // distributed publish subscribe in cluster
        system.actorOf(Props.create(Subscriber.class), "subscriber1");
        //        ActorRef publisher = system.actorOf(Props.create(Publisher.class), "publisher");
        //        publisher.tell("hello", null);
        system.actorOf(Props.create(Destination.class), "destination");

        // sharding
        Option<String> roleOption = Option.none();
//        ClusterShardingSettings settings = ClusterShardingSettings.create(system).withRole("sharding");
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef startedCounterRegion = ClusterSharding.get(system).start("Counter", Props.create(Counter.class), settings.withRole("sharding"), Counter.getMessageExtractor());
        ActorRef counterRegion = ClusterSharding.get(system).shardRegion("Counter");
        System.out.println("get sharding ok.");
        counterRegion.tell(new Counter.Get(123), ActorRef.noSender());
        counterRegion.tell(new Counter.EntityEnvelope(123,
                Counter.CounterOp.INCREMENT), ActorRef.noSender());
        counterRegion.tell(new Counter.Get(123), ActorRef.noSender());

        // printer sharding
        ActorRef startedPrinterRegion = ClusterSharding.get(system).start("printer", Props.create(Printer.class, "printer1"), settings.withRole("printer"), Printer.getMessageExtractor());
        ActorRef printerRegion = ClusterSharding.get(system).shardRegion("printer");
//        printerRegion.tell(new Printer.Init("111"), ActorRef.noSender());
        printerRegion.tell(new Printer.Message("111", "2551"), ActorRef.noSender());

        // ClusterSharding.get(system).startProxy("Counter", java.util.Optional.of("sharding"),Counter.getMessageExtractor());

        // router
        //		ActorRef workers = system.actorOf(new BalancingPool(5).props(Props.create(ArticleParseActor.class)), "workers");
        //		ClusterClientReceptionist.get(system).registerService(workers);
    }
}
