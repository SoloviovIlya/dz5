import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import weather.DailyForecast;
import weather.Headline;
import weather.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class WeatherTest extends AbstractTest{
    private static final Logger logger
            = LoggerFactory.getLogger(LocTest.class);
    @Test
    void get_shouldReturn200() throws IOException {
        logger.info("Тест код ответ 200 запущен");
        ObjectMapper mapper = new ObjectMapper();
        Weather weather = new Weather();
        Headline headline = new Headline();
        headline.setCategory("Категория");
        headline.setText("Текст");
        weather.setHeadline(headline);
        DailyForecast dailyForecast = new DailyForecast();
        List<DailyForecast> dailyForecasts = new ArrayList<>();
        dailyForecasts.add(dailyForecast);
        weather.setDailyForecasts(dailyForecasts);
        logger.debug("Формирование мока для GET /forecasts/v1/daily/10day/170087");
        stubFor(get(urlPathEqualTo("/forecasts/v1/daily/1day/294021"))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(weather))));
        stubFor(get(urlPathEqualTo("/forecasts/v1/daily/2day/294021"))
                .willReturn(aResponse()
                        .withStatus(500).withBody("ERROR")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl() + "/forecasts/v1/daily/1day/294021");
        logger.debug("http клиент создан");
        HttpResponse responseOK = httpClient.execute(request);
        verify(getRequestedFor(urlPathEqualTo("/forecasts/v1/daily/1day/294021")));
        assertEquals(200, responseOK.getStatusLine().getStatusCode());
        Weather responseBody = mapper.readValue(responseOK.getEntity().getContent(), Weather.class);
        assertEquals("Категория", responseBody.getHeadline().getCategory());
        assertEquals("Текст", responseBody.getHeadline().getText());
        assertEquals(10, responseBody.getDailyForecasts().size());
        HttpResponse responseEr = httpClient.execute(request);
        verify(getRequestedFor(urlPathEqualTo("/forecasts/v1/daily/1day/294021")));
        assertEquals(500, responseEr.getStatusLine().getStatusCode());
        assertEquals("ERROR", convertResponseToString(responseEr));
    }
}
