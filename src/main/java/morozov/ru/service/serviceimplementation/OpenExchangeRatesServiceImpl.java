package morozov.ru.service.serviceimplementation;

import morozov.ru.client.OpenExchangeRatesClient;
import morozov.ru.model.ExchangeRates;
import morozov.ru.service.serviceinterface.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с openexchangerates.org
 */
@Service
public class OpenExchangeRatesServiceImpl implements ExchangeRatesService {

    private ExchangeRates prevRates;
    private ExchangeRates currentRates;

    private OpenExchangeRatesClient openExchangeRatesClient;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    @Value("${openexchangerates.app.id}")
    private String appId;
    @Value("${openexchangerates.base}")
    private String base;
    private final Integer period = -2;

    @Autowired
    public OpenExchangeRatesServiceImpl(
            OpenExchangeRatesClient openExchangeRatesClient,
            @Qualifier("date_bean") SimpleDateFormat dateFormat,
            @Qualifier("time_bean") SimpleDateFormat timeFormat
    ) {
        this.openExchangeRatesClient = openExchangeRatesClient;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
    }

    /**
     * Возвращает список доступных для проверки валют.
     *
     * @return
     */
    @Override
    public List<String> getCharCodes() {
        List<String> result = null;
        if (this.currentRates.getRates() != null) {
            result = new ArrayList<>(this.currentRates.getRates().keySet());
        }
        return result;
    }

    /**
     * Проверяет\обновляет курсы,
     * Возвращает результат сравнения коэфициентов.
     * Если курсов или коэфициентов нет- возвращает -101.
     *
     * @param charCode
     * @return
     */
    @Override
    public int getKeyForTag(String charCode) {
        this.refreshRates();
        Double prevCoefficient = this.getCoefficient(this.prevRates, charCode);
        Double currentCoefficient = this.getCoefficient(this.currentRates, charCode);
        return prevCoefficient != null && currentCoefficient != null
                ? Double.compare(currentCoefficient, prevCoefficient)
                : -101;
    }

    /**
     * Проверка\обновление курсов.
     */
    @Override
    public void refreshRates() {
        long currentTime = System.currentTimeMillis();
        this.refreshCurrentRates(currentTime);
        this.refreshPrevRates(currentTime);
    }

    /**
     * Обновление текущих курсов.
     * Проверяется время с точностью до часа, т.к.
     * Обновление на openexchangerates.org происходит каждый час.
     *
     * @param time
     */
    private void refreshCurrentRates(long time) {
        if (
                this.currentRates == null ||
                        !timeFormat.format(Long.valueOf(this.currentRates.getTimestamp()) * 1000)
                                .equals(timeFormat.format(time))
        ) {
            this.currentRates = openExchangeRatesClient.getLatestRates(this.appId);
        }
    }

    /**
     * Обновление вчерашних курсов.
     * Проверяется время с точностью до дня.
     * При запросе к openexchangerates.org//historical/*
     * Приходят данные, отличные на день- потому значение period должно быть
     * установлено как -2 для получения курсов на день раньше от текущей даты.
     *
     * @param time
     */
    private void refreshPrevRates(long time) {
        Calendar prevCalendar = Calendar.getInstance();
        prevCalendar.setTimeInMillis(time);
        prevCalendar.add(Calendar.DAY_OF_YEAR, this.period);
        String newPrevDate = dateFormat.format(prevCalendar.getTime());
        if (
                this.prevRates == null ||
                        !dateFormat.format(Long.valueOf(this.prevRates.getTimestamp()) * 1000)
                                .equals(newPrevDate)
        ) {
            this.prevRates = openExchangeRatesClient.getHistoricalRates(newPrevDate, appId);
        }
    }

    /**
     * Формула для подсчётка коэфициента по отношению к установленной в этом приложении валютной базе.
     * (Default_Base / Our_Base) * Target
     * Если на входе оказался несуществующий charCode- то вернёт null
     *
     * @param rates
     * @param charCode
     */
    private Double getCoefficient(ExchangeRates rates, String charCode) {
        Double result = null;
        Double targetRate = null;
        Double appBaseRate = null;
        Double defaultBaseRate = null;
        Map<String, Double> map = null;
        if (rates != null && rates.getRates() != null) {
            map = rates.getRates();
            targetRate = map.get(charCode);
            appBaseRate = map.get(this.base);
            defaultBaseRate = map.get(rates.getBase());
        }
        if (targetRate != null && appBaseRate != null && defaultBaseRate != null) {
            targetRate = this.roundRate(targetRate);
            appBaseRate = this.roundRate(appBaseRate);
            defaultBaseRate = this.roundRate(defaultBaseRate);
            result = (defaultBaseRate / appBaseRate) * targetRate;
        }
        return result;
    }

    /**
     * Округление курса до двух знаков после запятой.
     *
     * @param rate
     * @return
     */
    private double roundRate(double rate) {
        return new BigDecimal(rate).setScale(2, RoundingMode.UP).doubleValue();
    }

}
