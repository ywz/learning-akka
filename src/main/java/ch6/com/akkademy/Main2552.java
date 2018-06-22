package ch6.com.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import ch5.com.akkademy.ArticleParseActor;
import chx.com.akka.sharding.Counter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main2552 {
    public static void main(String... args) {
        Config config = ConfigFactory.load("ch6_demaid_application_2552.conf");
        ActorSystem system = ActorSystem.create("Akkademy", config);
        ActorRef clusterController = system.actorOf(Props.create(ClusterController.class), "clusterController");
        ActorRef worker = system.actorOf(Props.create(ArticleParseActor.class), "worker");
        ClusterClientReceptionist.get(system).registerService(worker);

        try {
            TimeUnit.SECONDS.sleep(10L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        system.actorOf(Props.create(Destination.class), "destination");

        ActorRef sender = system.actorOf(Props.create(Publisher.class), "sender");
        sender.tell("hello", null);

        System.out.println("###");
        //  system.actorSelection("akka.tcp://Akkademy@127.0.0.1:2551/user/printer").tell("bbb", ActorRef.noSender());

        // sharding
        ClusterShardingSettings settings = ClusterShardingSettings.create(system).withRole("sharding");
        ActorRef startedCounterRegion = ClusterSharding.get(system).start("Counter", Props.create(Counter.class), settings, Counter.getMessageExtractor());

        // ClusterSharding.get(system).startProxy("Counter", java.util.Optional.of("sharding"), Counter.getMessageExtractor());
        // startedCounterRegion.tell(new Counter.Get(123), ActorRef.noSender());

        ActorRef counterRegion = ClusterSharding.get(system).shardRegion("Counter");
        IntStream.range(0, 1000).forEach(x -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counterRegion.tell(new Counter.Get(123), ActorRef.noSender());
            counterRegion.tell(new Counter.EntityEnvelope(123,
                    Counter.CounterOp.INCREMENT), ActorRef.noSender());
            counterRegion.tell(new Counter.Get(123), ActorRef.noSender());
        });

//        ActorRef printer = system.actorOf(FromConfig.getInstance().props(), "printer");
//        printer.tell("blah ", ActorRef.noSender());

        //		ActorRef workers = system.actorOf(new BalancingPool(5).props(Props.create(ArticleParseActor.class)), "workers");
        //		ClusterClientReceptionist.get(system).registerService(workers);
    }
}

