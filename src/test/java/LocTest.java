import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class LocTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(LocTest.class);
    @Test
    void GetLoc() throws IOException {
        logger.info("Тест код ответ 200 запущен");
        ObjectMapper mapper = new ObjectMapper();
        Location locationOk = new Location();
        locationOk.setKey("OK");
        Location locationEr = new Location();
        locationEr.setKey("Error");
        logger.debug("Формирование мока для GET dataservice.accuweather.com/locations/v1/170087");
        stubFor(get(urlPathEqualTo("/locations/v1/170087"))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(locationOk))));
        stubFor(get(urlPathEqualTo("/locations/v1/170087"))
                .willReturn(aResponse()
                        .withStatus(500).withBody(mapper.writeValueAsString(locationEr))));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http клиент создан");
        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/170087");
        HttpResponse responseOk = httpClient.execute(request);
        HttpResponse responseEr = httpClient.execute(request);
        verify(2,getRequestedFor(urlPathEqualTo("/locations/v1/170087")));
        assertEquals(200, responseOk.getStatusLine().getStatusCode());
        assertEquals(500, responseEr.getStatusLine().getStatusCode());
        assertEquals("OK", mapper.readValue(responseOk.getEntity().getContent(), Location.class).getKey());
        assertEquals("Error", mapper.readValue(responseEr.getEntity().getContent(), Location.class).getKey());
    }



    }
