package hr.algebra.javatwo.model;

public enum ConfigurationKey {

    HOST("host"), SERVER_PORT("server.port"), CLIENT_PORT("client.port"), RMI_PORT("rmi.port"), RANDOM_PORT_HINT("random.port.hint");

    public String getKeyName() {
        return keyName;
    }

    ConfigurationKey(String keyName) {
        this.keyName = keyName;
    }

    private final String keyName;
}
