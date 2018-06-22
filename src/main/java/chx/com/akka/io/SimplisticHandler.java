package chx.com.akka.io;

import akka.actor.AbstractActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;

public class SimplisticHandler extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Tcp.Received.class, msg -> {
                    final ByteString data = msg.data();
                    System.out.println(data);
                    getSender().tell(TcpMessage.write(data), getSelf());
                })
                .match(Tcp.ConnectionClosed.class, msg -> {
                    getContext().stop(getSelf());
                })
                .build();
    }
}
