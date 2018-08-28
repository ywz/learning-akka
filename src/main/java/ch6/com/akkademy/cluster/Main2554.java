//package ch6.com.akkademy.cluster;
//
//import akka.actor.ActorRef;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//import akka.cluster.sharding.ClusterSharding;
//import akka.cluster.sharding.ClusterShardingSettings;
//import chx.com.akka.sharding.Printer;
//import com.typesafe.config.Config;
//import com.typesafe.config.ConfigFactory;
//
//public class Main2554 {
//
//    public static void main(String[] args) {
//        Config config = ConfigFactory.load("ch6_demaid_application_2554.conf");
//        ActorSystem system = ActorSystem.create("Akkademy", config);
//
//        // printer sharding
//        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
//        ClusterSharding.get(system).start("printer", Props.create(Printer.class, "printer4"), settings.withRole("printer"), Printer.getMessageExtractor());
//        ActorRef printerRegion = ClusterSharding.get(system).shardRegion("printer");
//        printerRegion.tell(new Printer.Message("111", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("222", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("333", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("444", "2553"), ActorRef.noSender());
//        printerRegion.tell(new Printer.Message("555", "2553"), ActorRef.noSender());
//    }
//}
