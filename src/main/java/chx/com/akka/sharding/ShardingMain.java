package chx.com.akka.sharding;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import ch6.com.akkademy.ClusterController;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.TimeUnit;

public class ShardingMain {

    public static void main(String[] args) {
        Config config = ConfigFactory.load("chx_sharding_application.conf");
        ActorSystem system = ActorSystem.create("ShadingCluster", config);

        system.actorOf(Props.create(ClusterController.class), "clusterController");

        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ClusterSharding.get(system).start("printers", Props.create(Printer.class), settings, Printer.getMessageExtractor());

        ActorRef shardRegion = ClusterSharding.get(system).shardRegion("printers");
        for (int i = 0; i < 10; i++) {
            shardRegion.tell(new Printer.Get("printer" + i), ActorRef.noSender());
            shardRegion.tell(new Printer.Message("printer-" + i, "blah " + i), ActorRef.noSender());
            shardRegion.tell(new Printer.Message("printer-" + i, "blah " + i), ActorRef.noSender());
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shardRegion.tell(new Printer.Message("printer-" + 3, "blah " + 3), ActorRef.noSender());
        system.actorSelection("akka://ShadingCluster/system/sharding/printers/printer/printer-3").tell("selection", ActorRef.noSender());
    }
}
