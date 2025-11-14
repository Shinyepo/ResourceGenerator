package dev.shinyepo.resourcegenerator.configs;

public enum ProducerConfig {
    SOLAR_PANEL(6L),
    WATER_ABSORBER(4L);

    private final Long produces;

    ProducerConfig(long produces) {
        this.produces = produces;
    }

    public Long getProduces() {
        return produces;
    }
}
