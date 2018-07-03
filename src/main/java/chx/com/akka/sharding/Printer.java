package chx.com.akka.sharding;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Printer extends AbstractActor {

    private static final String SHARD_ID = "printer";

    private final String shardId;

    private List<String> msgList = new ArrayList<>();

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private static Logger logger = LoggerFactory.getLogger(Printer.class);

    static Props props(String shardId) {
        return Props.create(Printer.class, () -> new Printer(shardId));
    }

    public Printer(String shardId) {
        logger.debug("a new Printer, shard id: {}", shardId);
        this.shardId = shardId;
    }

    public static class Init implements Serializable {
        public final String printerId;

        public Init(String printerId) {
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
                    // System.out.println("param: " + shardId);
                    //System.out.println(getSelf().path() + ", new msg: " + x + ", list size: " + this.msgList.size());
                    log.debug("path: {}, new msg: {}, list size: {}", getSelf().path(), x, this.msgList.size());
                })
                .build();
    }

    public static ShardRegion.MessageExtractor getMessageExtractor() {
        return new ShardRegion.MessageExtractor() {

            @Override
            public String entityId(Object message) {
                if (message instanceof Printer.Init)
                    return ((Printer.Init) message).printerId;
                else if (message instanceof Printer.Message)
                    return ((Printer.Message) message).printerId;
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
