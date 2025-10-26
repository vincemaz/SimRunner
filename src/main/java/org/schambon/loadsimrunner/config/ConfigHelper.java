package org.schambon.loadsimrunner.config;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);

    private final Document configDocument;

    public ConfigHelper(Document configDocument){
        this.configDocument = configDocument;
    }

    public <T> T getValue(String key, Class<T> type) throws ConfigException {
        T result;
        try {
            result = type.cast(this.configDocument.get(key));
        } catch (ClassCastException exception){
            LOGGER.error("Invalid configuration file.");
            throw new ConfigException(String.format("Expected %s value for config parameter '%s'.", type.getSimpleName()  ,key));
        }
        return result;
    }
}
