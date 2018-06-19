package ch6.com.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;
import ch5.com.akkademy.ArticleParseActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main2552 {
    public static void main(String... args) {
        Config config = ConfigFactory.load("ch6_demaid_application_2552.conf");
        ActorSystem system = ActorSystem.create("Akkademy", config);
        ActorRef clusterController = system.actorOf(Props.create(ClusterController.class), "clusterController");
        ActorRef worker = system.actorOf(Props.create(ArticleParseActor.class), "woker");
        ClusterClientReceptionist.get(system).registerService(worker);

        //		ActorRef workers = system.actorOf(new BalancingPool(5).props(Props.create(ArticleParseActor.class)), "workers");
        //		ClusterClientReceptionist.get(system).registerService(workers);
    }
}

