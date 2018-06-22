package chx.com.akka.io;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.io.Tcp;

public class IOServer {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MyActorSystem");
        final ActorRef tcpManager = Tcp.get(system).manager();
        ActorRef server = system.actorOf(Props.create(Server.class, tcpManager));
    }
}
