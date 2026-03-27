package dev.shinyepo.resourcegenerator.configs;

public enum ConsumerConfig {
    BASIC_CONSUMER(2);

    private final int produces;

    ConsumerConfig(int produces) {
        this.produces = produces;
    }

    public int getProduces() {
        return produces;
    }
}
