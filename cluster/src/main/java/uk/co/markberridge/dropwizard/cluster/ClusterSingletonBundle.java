package uk.co.markberridge.dropwizard.cluster;

import uk.co.markberridge.dropwizard.cluster.ClusterConfiguration.AkkaConfiguration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import static com.google.common.base.Preconditions.*;

public class ClusterSingletonBundle implements ConfiguredBundle<ClusterConfiguration> {

    private String name;
    private ManagedAkkaCluster managedAkkaCluster;

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.name = bootstrap.getApplication().getName();
    }

    @Override
    public void run(ClusterConfiguration config, Environment environment) throws Exception {

        AkkaConfiguration akka = config.getAkkaConfiguration();
        boolean isEnabled = akka.isClusterAware();

        SingletonHealthCheck singletonHealthCheck = new SingletonHealthCheck(isEnabled);
        environment.healthChecks().register(name + "-clustering", singletonHealthCheck);

        if (isEnabled) {
            checkState(akka.getConfiguration().isPresent(), "Cannot be cluster aware with no akka config");

            this.managedAkkaCluster = new ManagedAkkaCluster(name, akka.getConfiguration().get(), singletonHealthCheck);
            environment.lifecycle().manage(managedAkkaCluster);
        }
    }

    public void onBecomingActiveSingleton(Runnable action) {
        if (managedAkkaCluster != null) {
            this.managedAkkaCluster.intialiseSingleton(action);
        }
    }
}
