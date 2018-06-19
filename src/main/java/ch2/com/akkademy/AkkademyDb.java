package ch2.com.akkademy;

import akka.actor.AbstractActor;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ch2.com.akkademy.messages.*;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);

    protected final Map<String, Object> map = new HashMap<String, Object>();

    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(SetRequest.class, message -> {
                    log.info("Received Set request: {}", message);
                    map.put(message.key, message.value);
                    sender().tell(new Status.Success(message.key), self());
                }).
                match(GetRequest.class, message -> {
                    log.info("Received Get request: {}", message);
                    Object value = map.get(message.key);
                    Object response = (value != null) ? value : new Status.Failure(new KeyNotFoundException(message.key));
                    sender().tell(response, self());
                }).
                match(SetIfNotExistsRequest.class, message -> {
                    log.info("Received SetIfNotExists request: {}", message);
                    if (!this.map.containsKey(message.key)) {
                        this.map.put(message.key, message.value);
                        sender().tell(new Status.Success(message.key), self());
                    } else {
                        sender().tell(new Status.Failure(new KeyExistsException(message.key)), self());
                    }
                }).
                match(DeleteRequest.class, message -> {
                    log.info("Received Delete request: ", message);
                    if (this.map.containsKey(message.key))
                        this.map.remove(message.key);
                    sender().tell(new Status.Success(message.key), self());
                }).
                matchAny(o -> sender().tell(new Status.Failure(new ClassNotFoundException()), self())).build();
    }
}
