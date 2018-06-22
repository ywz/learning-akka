package chx.com.akka.io;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.net.InetSocketAddress;

public class IOClient {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MyActorSystem");

        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 0);
        final ActorRef clientListener = system.actorOf(Props.create(ClientListener.class));
        ActorRef client = system.actorOf(Props.create(Client.class, inetSocketAddress, clientListener));
    }
}
