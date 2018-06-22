package chx.com.akka.sharding;


import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ShardRegion;
import akka.persistence.AbstractPersistentActor;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Counter extends AbstractPersistentActor {

    public static enum CounterOp {
        INCREMENT, DECREMENT
    }

    public static class Get implements Serializable {
        final public long counterId;

        public Get(long counterId) {
            this.counterId = counterId;
        }
    }

    public static class EntityEnvelope implements Serializable {
        final public long id;
        final public Object payload;

        public EntityEnvelope(long id, Object payload) {
            this.id = id;
            this.payload = payload;
        }
    }

    public static class CounterChanged implements Serializable {
        final public int delta;

        public CounterChanged(int delta) {
            this.delta = delta;
        }
    }


    int count = 0;

    // getSelf().path().name() is the entity identifier (utf-8 URL-encoded)
    @Override
    public String persistenceId() {
        return "Counter-" + getSelf().path().name();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        getContext().setReceiveTimeout(Duration.create(120L, TimeUnit.SECONDS));
    }

    void updateState(CounterChanged event) {
        count += event.delta;
        System.out.println("new count: " + count);
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(CounterChanged.class, this::updateState)
                .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Get.class, this::receiveGet)
                .matchEquals(CounterOp.INCREMENT, msg -> receiveIncrement())
                .matchEquals(CounterOp.DECREMENT, msg -> receiveDecrement())
                .matchEquals(ReceiveTimeout.getInstance(), msg -> passivate())
                .build();
    }

    private void receiveGet(Get msg) {
        getSender().tell(count, getSelf());
    }

    private void receiveIncrement() {
        persist(new CounterChanged(+1), this::updateState);
    }

    private void receiveDecrement() {
        persist(new CounterChanged(-1), this::updateState);
    }

    private void passivate() {
        getContext().getParent().tell(
                new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
    }

    public static ShardRegion.MessageExtractor getMessageExtractor() {
        return new ShardRegion.MessageExtractor() {

            @Override
            public String entityId(Object message) {
                if (message instanceof Counter.EntityEnvelope)
                    return String.valueOf(((Counter.EntityEnvelope) message).id);
                else if (message instanceof Counter.Get)
                    return String.valueOf(((Counter.Get) message).counterId);
                else
                    return null;
            }

            @Override
            public Object entityMessage(Object message) {
                if (message instanceof Counter.EntityEnvelope)
                    return ((Counter.EntityEnvelope) message).payload;
                else
                    return message;
            }

            @Override
            public String shardId(Object message) {
                int numberOfShards = 100;
                if (message instanceof Counter.EntityEnvelope) {
                    long id = ((Counter.EntityEnvelope) message).id;
                    return String.valueOf(id % numberOfShards);
                } else if (message instanceof Counter.Get) {
                    long id = ((Counter.Get) message).counterId;
                    return String.valueOf(id % numberOfShards);
                } else {
                    return null;
                }
            }
        };
    }
}
