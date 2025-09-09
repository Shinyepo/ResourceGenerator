package dev.shinyepo.resourcegenerator.configs;

public enum ProducerConfig {
    SOLAR_PANEL(20L);
    
    private final Long produces;

    ProducerConfig(long produces) {
        this.produces = produces;
    }

    public Long getProduces() {
        return produces;
    }
}
