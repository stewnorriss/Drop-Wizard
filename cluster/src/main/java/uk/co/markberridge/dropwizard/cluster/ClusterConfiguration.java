package uk.co.markberridge.dropwizard.cluster;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

/*
 * TODO:
 * * cluster singleton "auto-down-unreachable-after" 
 * */
public interface ClusterConfiguration {

    @Valid
    public AkkaConfiguration getAkkaConfiguration();

    public static class AkkaConfiguration {

        @JsonProperty
        private String configuration;
        @JsonProperty
        private boolean enabled = true;

        /**
         * Clustering will be enabled if there is any akka configuration specified.
         * 
         * @return
         */
        public static AkkaConfiguration clusteringDisabledByDefault() {
            AkkaConfiguration result = new AkkaConfiguration();
            result.enabled = false;
            return result;
        }

        private AkkaConfiguration() {

        }

        public Optional<String> getConfiguration() {
            return Optional.of(configuration);
        }

        public boolean isClusterAware() {
            return enabled;
        }
    }
}
