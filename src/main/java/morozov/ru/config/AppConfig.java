package morozov.ru.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AppConfig {

    /**
     * Для преобразования к строке с точностью до дня.
     *
     * @return
     */
    @Bean("date_bean")
    public SimpleDateFormat simpleDateFormatForDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * Для преобразования к строке с точностью до часа.
     *
     * @return
     */
    @Bean("time_bean")
    public SimpleDateFormat simpleDateFormatForTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH");
    }

}
