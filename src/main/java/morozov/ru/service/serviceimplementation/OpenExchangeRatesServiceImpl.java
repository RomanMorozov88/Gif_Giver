package morozov.ru.service.serviceimplementation;

import morozov.ru.client.OpenExchangeRatesClient;
import morozov.ru.model.ExchangeRates;
import morozov.ru.service.serviceinterface.OpenExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenExchangeRatesServiceImpl implements OpenExchangeRatesService {

    private OpenExchangeRatesClient openExchangeRatesClient;
    @Value("${openexchangerates.app.id}")
    private String appId;
    @Value("${openexchangerates.base}")
    private String base;

    @Autowired
    public OpenExchangeRatesServiceImpl(OpenExchangeRatesClient openExchangeRatesClient) {
        this.openExchangeRatesClient = openExchangeRatesClient;
    }

    @Override
    public List<String> getCharCodes() {
        ExchangeRates response = openExchangeRatesClient.getLatestRates(this.appId);
        List<String> result = new ArrayList<>(response.getRates().keySet());
        return result;
    }

    @Override
    public int getKeyForTag(String charCode) {
        return 0;
    }
}
