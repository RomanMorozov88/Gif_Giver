package morozov.ru.service.serviceinterface;

import java.util.List;

public interface ExchangeRatesService {

    List<String> getCharCodes();

    int getKeyForTag(String charCode);

}