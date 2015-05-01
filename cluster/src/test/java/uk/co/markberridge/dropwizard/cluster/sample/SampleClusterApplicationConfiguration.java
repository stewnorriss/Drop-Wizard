package uk.co.markberridge.dropwizard.cluster.sample;

import io.dropwizard.Configuration;
import uk.co.markberridge.dropwizard.cluster.ClusterConfiguration;

public class SampleClusterApplicationConfiguration extends Configuration implements ClusterConfiguration {

    private AkkaConfiguration akkaConfiguration = AkkaConfiguration.clusteringDisabledByDefault();

    @Override
    public AkkaConfiguration getAkkaConfiguration() {
        return akkaConfiguration;
    }
}
