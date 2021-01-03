package morozov.ru.client;

import morozov.ru.model.ExchangeRates;

public interface OpenExchangeRatesClient {

    ExchangeRates getLatestRates(String appId);

    ExchangeRates getHistoricalRates(String date, String appId);
}
