package morozov.ru.service.serviceimplementation;

import morozov.ru.client.OpenExchangeRatesClient;
import morozov.ru.model.ExchangeRates;
import morozov.ru.service.serviceinterface.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
     * Перед выполнением проводит проверку текущих курсов-
     * т.к. тут актуальность курсов не важна- то обновляет курсы только при отсутствии
     * текущих курсов.
     * Возвращает список доступных для проверки валют.
     *
     * @return
     */
    @Override
    public List<String> getCharCodes() {
        if (this.currentRates == null) {
            this.refreshRates();
        }
        return new ArrayList<>(this.currentRates.getRates().keySet());
    }

    /**
     * Возвращает результат сравнения коэфициентов.
     * Если коэфициентов нет- возвращает 0.
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
    private void refreshRates() {
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
     * Приходят данные, отличные на день- потому при настройке календаря стоит -2
     *
     * @param time
     */
    private void refreshPrevRates(long time) {
        Calendar prevCalendar = Calendar.getInstance();
        prevCalendar.setTimeInMillis(time);
        prevCalendar.add(Calendar.DAY_OF_YEAR, -2);
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
        Map<String, Double> map = rates.getRates();
        Double targetRate = map.get(charCode);
        return targetRate != null
                ? (map.get(this.prevRates.getBase()) / map.get(this.base)) * targetRate
                : null;
    }

}
