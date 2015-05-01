package uk.co.markberridge.dropwizard.cluster;

import io.dropwizard.lifecycle.Managed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSingletonManager;

import com.google.common.base.Supplier;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ManagedAkkaCluster implements Managed, Supplier<Runnable> {
    private static Logger log = LoggerFactory.getLogger(ManagedAkkaCluster.class);
    private final SingletonHealthCheck healthcheck;
    private final String configStr;
    private final String name;
    private ActorSystem system;
    private Runnable action;

    public ManagedAkkaCluster(String name, String configStr, SingletonHealthCheck healthcheck) {
        this.name = name;
        this.configStr = configStr;
        this.healthcheck = healthcheck;
    }

    @Override
    public void start() throws Exception {
        log.info("Starting {}", name);

        Config config = ConfigFactory.parseString(configStr);
        system = ActorSystem.create("ClusterSystem", config);
        // Create an Akka system
        Props defaultProps = ClusterSingletonManager.defaultProps(
                Props.create(ClusterSingleton.class, healthcheck, this), "ClusterSingleton", PoisonPill.getInstance(),
                null);
        system.actorOf(defaultProps);
    }

    public void intialiseSingleton(@SuppressWarnings("hiding") Runnable action) {
        this.action = action;
    }

    @Override
    public void stop() throws Exception {
        log.debug("Shutting Down Akka Cluster");
        system.shutdown();
    }

    @Override
    public Runnable get() {
        return action;
    }

}
