package chx.com.akka.sharding;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.japi.Option;

public class ShardingMain {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("ShardingCluster");
        Option<String> roleOption = Option.none();
        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
        ActorRef startedCounterRegion = ClusterSharding.get(system).start("Counter",
                Props.create(Counter.class), settings, Counter.getMessageExtractor());
    }
}
