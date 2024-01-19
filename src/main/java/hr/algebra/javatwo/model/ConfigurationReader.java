package hr.algebra.javatwo.model;

import hr.algebra.javatwo.expection.InvalidConfigurationKeyException;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class ConfigurationReader {

    private static ConfigurationReader reader;
    private Hashtable<String, String> environment;

    public ConfigurationReader() {
        System.out.println("sss");
        environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        String currentDirectory = System.getProperty("user.dir");
        environment.put(Context.PROVIDER_URL, "file:D:/configuration");
    }


    public static ConfigurationReader getInstance() {
        if (reader == null) {
            reader = new ConfigurationReader();
        }
        return reader;
    }

    public String readStringValueForKey(ConfigurationKey configurationKey) {

        String valueForKey = "";

        try (InitialDirContextCloseable contextCloseable = new InitialDirContextCloseable(environment)) {
            searchForKey(contextCloseable, configurationKey);
            return valueForKey;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public int readIntegerValueForKey(ConfigurationKey configurationKey) {
        String valueForKey = readStringValueForKey(configurationKey);
        return Integer.parseInt(valueForKey);
    }


    private static String searchForKey(Context context, ConfigurationKey key) {

        String fileName = "conf.properties";
        try {
            Object object = context.lookup(fileName);
            Properties props = new Properties();
            props.load(new FileReader(object.toString()));
            String value = props.getProperty(key.getKeyName());
            if (value == null) {
                throw new InvalidConfigurationKeyException("The key " + key.getKeyName() + " does not exist in configuration file");
            }
            return value;
        } catch (NamingException | IOException ex) {
            throw new RuntimeException("Error while reading configuration.");
        }
    }


}
