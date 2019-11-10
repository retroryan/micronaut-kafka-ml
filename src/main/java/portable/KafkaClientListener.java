package portable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static io.micronaut.http.HttpRequest.POST;

@KafkaListener(groupId = "portable_weather")
public class KafkaClientListener {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaClientListener.class);

    @Inject
    private KafkaProducerClient kafkaProducerClient;

    @Client("${portable.ml_url}")
    @Inject
    RxHttpClient httpClient;

    @Topic("${portable.topic_in}")
    public void receive(String message) throws IOException {
        LOG.debug("kafka msg => " + message);
        postWeatherML(message);
    }

    /*
         ML Service must receive:
         {
           "instances": [
             {
               "end_station_id": "333",
               "ts": 1435774380.0,
               "day_of_week": "4",
               "start_station_id": "160",
               "euclidean": 4295.88,
               "loc_cross": "POINT(-0.13 51.51)POINT(-0.19 51.51)",
               "prcp": 0.0,
               "max": 94.5,
               "min": 58.9,
               "temp": 81.8,
               "dewp": 59.5
             }
           ]
         }
          */
    public void postWeatherML(String message) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        WeatherData weatherData = mapper.readValue(message, WeatherData.class);
        WeatherDataML weatherDataML = new WeatherDataML(weatherData);
        WeatherDataList weatherDataList = new WeatherDataList(Collections.singletonList(weatherDataML));


        MutableHttpRequest<WeatherDataList> accept = POST("/models/bikesw:predict", weatherDataList).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);
        Flowable<HttpResponse<String>> call = httpClient.exchange(accept, String.class);

        /*
            {
                "predictions": [[1501.77026]]
            }
        */

        call.blockingForEach(stringHttpResponse -> {
            if (stringHttpResponse.status() == HttpStatus.OK) {
                Map<String, List<List<Double>>> readValueMap = mapper.readValue(stringHttpResponse.body(), Map.class);
                readValueMap.forEach((s, lists) -> {
                    weatherData.prediction = lists.get(0).get(0);
                    String outJson = null;
                    try {
                        outJson = mapper.writeValueAsString(weatherData);
                        LOG.info("Storing json -> " + outJson);
                        kafkaProducerClient.sendProduct(UUID.randomUUID().toString(), outJson);

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
