package morozov.ru.controller;

import morozov.ru.service.serviceinterface.GifService;
import morozov.ru.service.serviceinterface.ExchangeRatesService;
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

    private ExchangeRatesService exchangeRatesService;
    private GifService gifService;
    @Value("${giphy.rich}")
    private String richTag;
    @Value("${giphy.broke}")
    private String brokeTag;
    @Value("${giphy.zero}")
    private String whatTag;
    @Value("${giphy.error}")
    private String errorTag;

    @Autowired
    public MainController(
            ExchangeRatesService exchangeRatesService,
            GifService gifService
    ) {
        this.exchangeRatesService = exchangeRatesService;
        this.gifService = gifService;
    }

    /**
     * Возвращает список доступных кодов валют.
     *
     * @return
     */
    @GetMapping("/getcodes")
    public List<String> getCharCodes() {
        return exchangeRatesService.getCharCodes();
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
        int gifKey = -101;
        String gifTag = this.errorTag;
        if (code != null) {
            gifKey = exchangeRatesService.getKeyForTag(code);
        }
        switch (gifKey) {
            case 1:
                gifTag = this.richTag;
                break;
            case -1:
                gifTag = this.brokeTag;
                break;
            case 0:
                gifTag = this.whatTag;
                break;
        }
        result = gifService.getGif(gifTag);
        return result;
    }

}