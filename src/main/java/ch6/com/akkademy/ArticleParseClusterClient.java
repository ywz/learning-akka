package ch6.com.akkademy;

import akka.actor.*;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;
import akka.util.Timeout;
import ch5.com.akkademy.ParseArticle;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArticleParseClusterClient extends AbstractActor {
    public static void main(String[] args) {
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));

        final Config config = ConfigFactory.load("ch6_demaind_client_application.conf");

        ActorSystem system = ActorSystem.create("clientSystem", config);

/*		Set<ActorSelection> initialContacts = new HashSet<ActorSelection>();
		initialContacts.add(system.actorSelection("akka.tcp://Akkademy@127.0.0.1:2552/user/receptionist"));
		initialContacts.add(system.actorSelection("akka.tcp://Akkademy@127.0.0.1:2551/user/receptionist"));*/
        // ActorRef receptionist = system.actorOf(ClusterClient.defaultProps(initialContacts));
        ActorRef receptionist = system.actorOf(ClusterClient.props(ClusterClientSettings.create(system).withInitialContacts(initialContacts(config))), "client");

        // receptionist.tell(new ClusterClient.Send("/user/wokers", "<html></html>", true), ActorRef.noSender());
        ActorRef clientActor = system.actorOf(Props.create(ArticleParseClusterClient.class), "clientActor");
        receptionist.tell(new ClusterClient.Send("/user/woker", new ParseArticle(ArticleToParse.article), true), clientActor);

        //		ClusterClient.Send msg = new ClusterClient.Send("/user/worker", ArticleToParse.article, false);
        //		Future f = Patterns.ask(receptionist, msg, timeout);
        //		String result = (String) Await.result(f, timeout.duration());
        //		System.out.println("result: " + result);
    }

    static Set<ActorPath> initialContacts(Config config) {
        List<String> actorPaths = config.getStringList("akka.cluster.client.initial-contacts");
        List<ActorPath> ActorPathList = new ArrayList<>();
        if (null != actorPaths && actorPaths.size() > 0) {
            for (String path : actorPaths) {
                ActorPathList.add(ActorPaths.fromString(path));
            }
        }
        return new HashSet<>(ActorPathList);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(String.class, x -> System.out.println(x)
                ).build();
    }
}
