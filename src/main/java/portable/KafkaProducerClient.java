package portable;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface KafkaProducerClient {

    @Topic("${portable.topic_out}")
    void sendProduct(@KafkaKey String key, String message);

}
