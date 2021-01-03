package morozov.ru.service.serviceimplementation;

import morozov.ru.client.GiphyClient;
import morozov.ru.service.serviceinterface.GifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Сервис для работы с Giphy.com
 */
@Service
public class GiphyGifServiceImpl implements GifService {

    private GiphyClient client;
    @Value("${giphy.api.key}")
    private String apiKey;

    @Autowired
    public GiphyGifServiceImpl(GiphyClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Map> getGif(String tag) {
        return client.getRandomGif(this.apiKey, tag);
    }
}
