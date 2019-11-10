Micronaut Kafka ML
------------------

Run Locally:

```
BOOTSTRAP_SERVER=localhost:9092 \
KAFKA_TOPIC_IN=bikeweather \
KAFKA_TOPIC_OUT=bikeweather-ml \
ML_URL=http://YOUR_TFSERVING_IP:8500/v1/models/bikesw:predict \
./mvnw compile exec:exec
```
