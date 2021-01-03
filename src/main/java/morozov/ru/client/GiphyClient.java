package morozov.ru.client;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface GiphyClient {

    ResponseEntity<Map> getRandomGif(String apiKey, String tag);

}
