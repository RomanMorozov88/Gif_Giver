package morozov.ru.service.serviceimplementation;

import morozov.ru.client.GiphyClient;
import morozov.ru.service.serviceinterface.GiphyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GiphyServiceImpl implements GiphyService {

    private GiphyClient client;
    @Value("${giphy.api.key}")
    private String apiKey;

    @Autowired
    public GiphyServiceImpl(GiphyClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Map> getGif(String tag) {
        return client.getRandomGif(this.apiKey, tag);
    }
}
