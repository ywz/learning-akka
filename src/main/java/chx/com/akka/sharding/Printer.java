package chx.com.akka.sharding;

import akka.actor.AbstractActor;
import akka.cluster.sharding.ShardRegion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Printer extends AbstractActor {

    static final String SHARD_ID = "printer";

    private List<String> msgList = new ArrayList<>();

    public static class Get implements Serializable {
        public final String printerId;

        public Get(String printerId) {
            this.printerId = printerId;
        }
    }

    public static class Message implements Serializable {
        public final String printerId;
        public final String msg;

        public Message(String printerId, String msg) {
            this.printerId = printerId;
            this.msg = msg;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, x -> {
                    this.msgList.add(x);
                    System.out.println(getSelf().path() + ", new msg: " + x + ", list size: " + this.msgList.size());
                })
                .build();
    }

    public static ShardRegion.MessageExtractor getMessageExtractor() {
        return new ShardRegion.MessageExtractor() {

            @Override
            public String entityId(Object message) {
                if (message instanceof Printer.Get)
                    return ((Printer.Get) message).printerId;
                else if (message instanceof Printer.Message)
                    return ((Message) message).printerId;
                else
                    return null;
            }

            @Override
            public Object entityMessage(Object message) {
                if (message instanceof Printer.Message)
                    return ((Message) message).msg;
                else
                    return message;
            }

            @Override
            public String shardId(Object message) {
                return Printer.SHARD_ID;
            }
        };
    }
}
