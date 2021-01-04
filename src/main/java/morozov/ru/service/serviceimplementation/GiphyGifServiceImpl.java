package morozov.ru.service.serviceimplementation;

import morozov.ru.client.GifClient;
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

    private GifClient gifClient;
    @Value("${giphy.api.key}")
    private String apiKey;

    @Autowired
    public GiphyGifServiceImpl(GifClient gifClient) {
        this.gifClient = gifClient;
    }

    /**
     * Ответ от Giphy.com просто перекидывается клиенту
     * в виде ResponseEntity
     * лишь с небольшой модификацией- добавляется compareResult
     * для удобства визуальной проверки результата.
     *
     * @param tag
     * @return
     */
    @Override
    public ResponseEntity<Map> getGif(String tag) {
        ResponseEntity<Map> result = gifClient.getRandomGif(this.apiKey, tag);
        result.getBody().put("compareResult", tag);
        return result;
    }
}
