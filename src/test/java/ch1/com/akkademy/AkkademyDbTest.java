package ch1.com.akkademy;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import ch1.com.akkademy.messages.SetRequest;
import org.junit.Assert;
import org.junit.Test;

public class AkkademyDbTest {
    ActorSystem system = ActorSystem.create();

    @Test
    public void itShouldPlaceKeyValueFromSetMessageIntoMap() {
        TestActorRef<AkkademyDb> actorRef = TestActorRef.create(system, Props.create(AkkademyDb.class));
        actorRef.tell(new SetRequest("key1", "value1"), ActorRef.noSender()); // TestActorRef的tell方法非异步
        actorRef.tell(new SetRequest("key2", "value2"), ActorRef.noSender());

        AkkademyDb akkademyDb = actorRef.underlyingActor();
        Assert.assertEquals(akkademyDb.map.get("key1"), "value1");
    }
}
