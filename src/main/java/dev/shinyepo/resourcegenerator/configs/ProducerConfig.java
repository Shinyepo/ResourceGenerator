package dev.shinyepo.resourcegenerator.configs;

public enum ProducerConfig {
    SOLAR_PANEL(6L),
    WATER_ABSORBER(4L),
    SCULK_ABSORBER(40L),
    SPAWNER_ABSORBER(1200L),
    CONDUIT_ABSORBER(800L);

    private final Long produces;

    ProducerConfig(long produces) {
        this.produces = produces;
    }

    public Long getProduces() {
        return produces;
    }
}
