package ch5.com.akkademy;

import com.jasongoodwin.monads.Try;

public class ArticleParser {
    public static Try<String> apply(String html) {
        return Try.ofFailable(() -> de.l3s.boilerpipe.extractors.ArticleExtractor.INSTANCE.getText(html));
    }
}

