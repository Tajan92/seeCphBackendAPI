package app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

    private static final String CONFIG_FILE = "config.properties";

    /** Required value: from env vars when DEPLOYED, otherwise from config.properties. Throws if missing. */
    public static String getConfig(String key) {
        String value = getOptionalConfig(key);
        if (value == null) {
            throw new IllegalStateException(key + " is not configured");
        }
        return value;
    }

    /** Optional value: same lookup, but returns null if missing or blank. */
    public static String getOptionalConfig(String key) {
        String value = System.getenv("DEPLOYED") != null
                ? System.getenv(key)
                : readProperty(key, CONFIG_FILE);
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    /** Reads one key from a properties file on the classpath, or null if the file or key is missing. */
    private static String readProperty(String propName, String resourceName) {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                return null;
            }
            Properties prop = new Properties();
            prop.load(is);
            return prop.getProperty(propName);
        } catch (IOException ex) {
            throw new RuntimeException("Could not read " + resourceName, ex);
        }
    }
}