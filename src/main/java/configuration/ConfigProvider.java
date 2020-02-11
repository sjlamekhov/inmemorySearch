package configuration;

import utils.FileUtils;

import java.util.Properties;

public class ConfigProvider {

    public static final String CONFIG_FILE_PROPERTY = "configFile";
    private static final String DEFAULT_CONFIG_FILE = "properties.properties";

    public static Properties getProperties() {
        Properties properties = new Properties();
        String configFile = System.getProperty(CONFIG_FILE_PROPERTY);
        try {
            if (configFile != null) {
                properties = FileUtils.propertiesFromFile(configFile);
            } else {
                properties = FileUtils.propertiesFromClasspath(DEFAULT_CONFIG_FILE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

}