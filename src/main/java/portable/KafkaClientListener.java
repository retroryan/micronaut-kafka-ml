package portable;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@KafkaListener(groupId = "portable_weather")
public class KafkaClientListener {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaClientListener.class);

    @Inject
    private KafkaProducerClient kafkaProducerClient;

    @Topic("${portable.topic_in}")
    public void receive(String message) throws IOException {
        LOG.debug("kafka msg => " + message);
        postWeatherML(message);
    }

    @Client("${portable.ml_url}")
    interface MlService {
        @Post
        Single<WeatherDataPrediction> predict(@Body WeatherDataList weatherDataList);
    }

    @Inject MlService mlService;

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

        WeatherDataPrediction weatherDataPrediction = mlService.predict(weatherDataList).blockingGet();
        weatherData.prediction = weatherDataPrediction.predictions.get(0).get(0);

        String outJson = mapper.writeValueAsString(weatherData);
        LOG.info("Storing json -> " + outJson);
        kafkaProducerClient.sendProduct(UUID.randomUUID().toString(), outJson);
    }
}
