package portable;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("portable")
public class PortableClientConfig {

    private String bootstrap_servers;
    private String topic_in;
    private String topic_out;
    private String ml_url;

    public String getBootstrap_servers() {
        return bootstrap_servers;
    }

    public void setBootstrap_servers(String bootstrap_servers) {
        this.bootstrap_servers = bootstrap_servers;
    }

    public String getTopic_in() {
        return topic_in;
    }

    public void setTopic_in(String topic_in) {
        this.topic_in = topic_in;
    }

    public String getTopic_out() {
        return topic_out;
    }

    public void setTopic_out(String topic_out) {
        this.topic_out = topic_out;
    }

    public String getMl_url() {
        return ml_url;
    }

    public void setMl_url(String ml_url) {
        this.ml_url = ml_url;
    }
}
