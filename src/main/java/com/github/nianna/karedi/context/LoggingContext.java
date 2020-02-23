package main.java.com.github.nianna.karedi.context;

import main.java.com.github.nianna.karedi.KarediApp;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.logging.Logger;

@Component
public class LoggingContext {
    public static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());

    @PostConstruct
    public void init() {
        LOGGER.setUseParentHandlers(false);
    }

    public Logger getMainLogger() {
        return LOGGER;
    }
}
