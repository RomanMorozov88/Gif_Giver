package morozov.ru.client;

import morozov.ru.model.ExchangeRates;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OpenExchangeRatesClient {

    ExchangeRates getLatestRates(String appId);

    ResponseEntity<Map> getHistoricalRates(String date, String appId);
}
