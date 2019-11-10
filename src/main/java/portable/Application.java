package portable;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller()
public class Application implements ApplicationEventListener<ServerStartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final PortableClientConfig portableClientConfig;

    public Application(PortableClientConfig portableClientConfig) {
        this.portableClientConfig = portableClientConfig;
    }


    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }

    @Get()
    public String index() {
        return "hello, world";
    }

    @Override
    public void onApplicationEvent(ServerStartupEvent event) {

    }

    @EventListener
    public void onStartup(ServerStartupEvent event) {
        LOG.info("Connecting with ws client config = " + portableClientConfig);
        LOG.info("topics in = " + portableClientConfig.getTopic_in());
        LOG.info("topics out = " + portableClientConfig.getTopic_out());

    }
}


