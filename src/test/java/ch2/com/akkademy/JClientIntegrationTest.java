package ch2.com.akkademy;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JClientIntegrationTest {
    JClient client = new JClient("127.0.0.1:2552");

    @Test(expected = ExecutionException.class)
    public void itShouldSetRecord() throws Exception {
        client.set("123", 123);
        Integer result = (Integer) ((CompletableFuture) client.get("123")).get();
        assert (result == 123);

        client.setIfExists("123", 456).handle((x, ex) -> ex == null ? CompletableFuture.completedFuture(x) : client.setIfExists("456", 456));
        result = (Integer) ((CompletableFuture) client.get("456")).get();
        assert (456 == result);

        client.delete("123");
        ((CompletableFuture) client.get("123")).get();
    }
}
