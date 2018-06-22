package chx.com.akka.io;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;

import java.net.InetSocketAddress;

public class Client extends AbstractActor {
    final InetSocketAddress remote;
    final ActorRef listener;

    public static Props props(InetSocketAddress remote, ActorRef listener) {
        return Props.create(Client.class, remote, listener);
    }

    public Client(InetSocketAddress remote, ActorRef listener) {
        this.remote = remote;
        this.listener = listener;

        final ActorRef tcp = Tcp.get(getContext().getSystem()).manager();
        tcp.tell(TcpMessage.connect(remote), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Tcp.CommandFailed.class, msg -> {
                    listener.tell("failed", getSelf());
                    getContext().stop(getSelf());

                })
                .match(Tcp.Connected.class, msg -> {
                    listener.tell(msg, getSelf());
                    getSender().tell(TcpMessage.register(getSelf()), getSelf());
                    getContext().become(connected(getSender()));
                })
                .build();
    }

    private Receive connected(final ActorRef connection) {
        return receiveBuilder()
                .match(ByteString.class, msg -> {
                    connection.tell(TcpMessage.write(msg), getSelf());
                })
                .match(Tcp.CommandFailed.class, msg -> {
                    // OS kernel socket buffer was full
                })
                .match(Tcp.Received.class, msg -> {
                    listener.tell(msg.data(), getSelf());
                })
                .matchEquals("close", msg -> {
                    connection.tell(TcpMessage.close(), getSelf());
                })
                .match(Tcp.ConnectionClosed.class, msg -> {
                    getContext().stop(getSelf());
                })
                .build();
    }
}
