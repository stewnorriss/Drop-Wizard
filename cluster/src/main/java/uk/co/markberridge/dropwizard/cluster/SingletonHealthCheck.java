package uk.co.markberridge.dropwizard.cluster;

import com.codahale.metrics.health.HealthCheck;

public class SingletonHealthCheck extends HealthCheck {

    private boolean master = false;
    private boolean enabled;

    public SingletonHealthCheck(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected Result check() throws Exception {
        if (!enabled) {
            return Result.healthy("DISABLED");
        }
        return Result.healthy(master ? "ACTIVE" : "PASSIVE");
    }

    public void setMaster(boolean master) {
        this.master = master;
    }
}
