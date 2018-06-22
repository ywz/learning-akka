package ch5.com.akkademy;

import akka.actor.AbstractActor;
import akka.actor.Status;

public class ArticleParseActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder().
                match(ParseArticle.class, x -> this.doPaerse(x.htmlBody)).
                matchAny(x -> System.out.println(x)).
                // match(String.class, x -> this.doPaerse(x)).
                build();
    }

    private void doPaerse(String input) {
        ArticleParser.apply(input).
                onSuccess(body -> {
                    sender().tell(body, self());
                    System.out.println(body);
                }).
                onFailure(t -> sender().tell(new Status.Failure(t), self()));
    }
}
