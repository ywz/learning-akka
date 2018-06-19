package ch1.com.akkademy;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ch1.com.akkademy.messages.SetRequest;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);

    protected final Map<String, Object> map = new HashMap<>();

    @Override
    public Receive createReceive() {
        // 匹配模式：如果消息的类型是SetRequest.class，那么接受
        // 该消息，打印日志，并且将该Set 消息的键和值作为一条新纪录插入到map中(match)。
        // 其次，我们捕捉其他所有未知类型的消息，直接输出到日志(matchAny)。
        Receive receive = receiveBuilder().match(SetRequest.class, message -> {
            log.info("Received Set request: {}", message);
            map.put(message.getKey(), message.getValue());
        }).matchAny(o -> log.info("received unknown message: {}", o)).build();
        return receive;
    }
}
