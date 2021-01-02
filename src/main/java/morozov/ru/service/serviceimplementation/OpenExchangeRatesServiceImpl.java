package morozov.ru.service.serviceimplementation;

import morozov.ru.service.serviceinterface.OpenExchangeRatesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenExchangeRatesServiceImpl implements OpenExchangeRatesService {

    @Value("${openexchangerates.app.id}")
    private String apiId;
    @Value("${openexchangerates.base}")
    private String base;

    @Override
    public List<String> getCharCodes() {
        return null;
    }

    @Override
    public int getKeyForTag(String charCode) {
        return 0;
    }
}
