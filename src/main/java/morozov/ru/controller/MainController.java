package morozov.ru.controller;

import morozov.ru.service.serviceinterface.GiphyService;
import morozov.ru.service.serviceinterface.OpenExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gg")
public class MainController {

    private OpenExchangeRatesService openExchangeRatesService;
    private GiphyService giphyService;
    @Value("${giphy.api.key}")
    private String apiKey;
    @Value("${giphy.rich}")
    private String richTag;
    @Value("${giphy.broke}")
    private String brokeTag;
    @Value("${giphy.zero}")
    private String whatTag;

    @Autowired
    public MainController(
            OpenExchangeRatesService openExchangeRatesService,
            GiphyService giphyService
    ) {
        this.openExchangeRatesService = openExchangeRatesService;
        this.giphyService = giphyService;
    }

    /**
     * Возвращает список доступных кодов валют.
     *
     * @return
     */
    @GetMapping("/getcodes")
    public List<String> getCharCodes() {
        return openExchangeRatesService.getCharCodes();
    }

    /**
     * Получает гифку для отправки клиенту
     * исходя из резултата сравнения курса в openExchangeRatesService
     *
     * @param code
     * @return
     */
    @GetMapping("/getgif/{code}")
    public ResponseEntity<Map> getGif(@PathVariable String code) {
        ResponseEntity<Map> result = null;
        switch (openExchangeRatesService.getKeyForTag(code)) {
            case 1:
                result = giphyService.getGif(this.richTag);
                break;
            case -1:
                result = giphyService.getGif(this.brokeTag);
                break;
            case 0:
                result = giphyService.getGif(this.whatTag);
                break;
        }
        return result;
    }

}