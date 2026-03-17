package dev.shinyepo.resourcegenerator.configs;

public enum ConsumerConfig {
    BASIC_CONSUMER(2L);

    private final Long produces;

    ConsumerConfig(long produces) {
        this.produces = produces;
    }

    public Long getProduces() {
        return produces;
    }
}
