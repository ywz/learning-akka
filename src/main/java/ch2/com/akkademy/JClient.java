package ch2.com.akkademy;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import ch2.com.akkademy.messages.DeleteRequest;
import ch2.com.akkademy.messages.GetRequest;
import ch2.com.akkademy.messages.SetIfNotExistsRequest;
import ch2.com.akkademy.messages.SetRequest;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;
import static scala.compat.java8.FutureConverters.toJava;

public class JClient {
    private final ActorSystem system = ActorSystem.create("LocalSystem", ConfigFactory.load("ch2_jclient_application.conf"));

    private final ActorSelection remoteDb;

    public JClient(String remoteAddress) {
        remoteDb = system.actorSelection("akka.tcp://akkademy@" + remoteAddress + "/user/akkademy-db");
    }

    public CompletionStage set(String key, Object value) {
        return toJava(ask(remoteDb, new SetRequest(key, value), 2000));
    }

    public CompletionStage<Object> get(String key) {
        return toJava(ask(remoteDb, new GetRequest(key), 2000));
    }

    public CompletionStage setIfExists(String key, Object value) {
        return toJava(ask(remoteDb, new SetIfNotExistsRequest(key, value), 2000));
    }

    public CompletionStage delete(String key) {
        return toJava(ask(remoteDb, new DeleteRequest(key), 2000));
    }
}

