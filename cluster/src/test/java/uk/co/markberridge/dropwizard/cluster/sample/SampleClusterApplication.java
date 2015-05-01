package uk.co.markberridge.dropwizard.cluster.sample;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.concurrent.ScheduledExecutorService;

import uk.co.markberridge.dropwizard.cluster.ClusterSingletonBundle;

/**
 * Main dropwizard service
 */
public class SampleClusterApplication extends Application<SampleClusterApplicationConfiguration> {

    private static final ClusterSingletonBundle singletonBundle = new ClusterSingletonBundle();

    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            String name = new OverrideConfig("cluster.yml").getName();
            new SampleClusterApplication().run(new String[] { "server", name });
        } else {
            new SampleClusterApplication().run(args);
        }
    }

    @Override
    public String getName() {
        return "sample-cluster-app";
    }

    @Override
    public void initialize(Bootstrap<SampleClusterApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(singletonBundle);
    }

    @Override
    public void run(SampleClusterApplicationConfiguration config, Environment environment) throws Exception {

        environment.jersey().register(PingResource.class);

        ScheduledExecutorService executorService = environment.lifecycle()
                                                              .scheduledExecutorService("singletonThread")
                                                              .threads(1)
                                                              .build();

        singletonBundle.onBecomingActiveSingleton(new ScheduleLoggingThatIAmMaster(executorService));

    }
}