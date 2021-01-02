package morozov.ru.service.serviceinterface;

import java.util.List;

public interface OpenExchangeRatesService {

    List<String> getCharCodes();
    int getKeyForTag(String charCode);
}