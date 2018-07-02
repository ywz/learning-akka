package chx.com.akka.router;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.BalancingPool;
import akka.routing.RoundRobinPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RouterMain {
    public static void main(String[] args) {
        Config config = ConfigFactory.load("chx_router_application.conf");
        ActorSystem system = ActorSystem.create("routerSystem", config);
        ActorRef firstUnpackRouter = system.actorOf(Props.create(FirstUnpackActor.class).withRouter(new RoundRobinPool(2)), "firstUnpackRouter");
        ActorRef protocolRouter = system.actorOf(Props.create(ProtocolActor.class).withRouter(new BalancingPool(5)), "protocolRouter");

        firstUnpackRouter.tell("test", ActorRef.noSender());
        protocolRouter.tell("test test", ActorRef.noSender());

        ActorSelection actor = system.actorSelection("akka://routerSystem/user/firstUnpackRouter/");
        actor.tell("blah", ActorRef.noSender());
        actor.tell("blah", ActorRef.noSender());

    }
}
