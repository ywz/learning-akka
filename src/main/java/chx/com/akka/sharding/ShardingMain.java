package chx.com.akka.sharding;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import ch6.com.akkademy.cluster.ClusterController;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.TimeUnit;

public class ShardingMain {

    private static final String TYPE_NAME = "printers";

    private static final String PRINTER_ID_PREFIX = "printer-";

    public static void main(String[] args) {
        Config config = ConfigFactory.load("chx_sharding_application.conf");
        ActorSystem system = ActorSystem.create("ShardingCluster", config);

        system.actorOf(Props.create(ClusterController.class), "clusterController");

        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ClusterSharding.get(system).start(TYPE_NAME, Props.create(Printer.class, "param"), settings, Printer.getMessageExtractor());

        ActorRef shardRegion = ClusterSharding.get(system).shardRegion(TYPE_NAME);
        for (int i = 0; i < 10; i++) {
            // shardRegion.tell(new Printer.Init(PRINTER_ID_PREFIX + i), ActorRef.noSender());
            shardRegion.tell(new Printer.Message(PRINTER_ID_PREFIX + i, "hello " + i), ActorRef.noSender());
            shardRegion.tell(new Printer.Message(PRINTER_ID_PREFIX + i, "blah " + i), ActorRef.noSender());
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shardRegion.tell(new Printer.Message(PRINTER_ID_PREFIX + 3, "hello again " + 3), ActorRef.noSender());
        system.actorSelection("akka://ShardingCluster/system/sharding/printers/printer/printer-3").tell("selection", ActorRef.noSender());
        system.actorSelection("akka://ShardingCluster/system/sharding/printers/printer/*").tell("boardcast", ActorRef.noSender());
    }
}
