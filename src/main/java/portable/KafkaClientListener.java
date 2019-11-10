package portable;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.functions.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

import static io.micronaut.http.HttpRequest.POST;

import static io.micronaut.http.HttpRequest.GET;

@KafkaListener(groupId = "portable_weather")
public class KafkaClientListener {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaClientListener.class);

    @Inject
    private KafkaProducerClient kafkaProducerClient;

    @Client("${portable.ml_url}") @Inject
    RxHttpClient httpClient;

    @Topic("${portable.topic_in}")
    public void receive(String message) {
        LOG.info("kafka msg => " + message);
        kafkaProducerClient.sendProduct(UUID.randomUUID().toString(), message);
        //postWeatherML(message);
    }

    public void postWeatherML(String message) {
        MutableHttpRequest<String> http_request = POST("/models/bikesw:predict", message).contentType(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN_TYPE);
        Flowable<HttpResponse<String>> call = httpClient.exchange(http_request, String.class);

        call.blockingForEach(new Consumer<HttpResponse<String>>() {
            @Override
            public void accept(HttpResponse<String> stringHttpResponse) throws Exception {
                LOG.info("string http response => " + stringHttpResponse);
                LOG.info("string http body => " + stringHttpResponse.body());
                LOG.info("string http status => " + stringHttpResponse.status());
            }
        });
    }


}
