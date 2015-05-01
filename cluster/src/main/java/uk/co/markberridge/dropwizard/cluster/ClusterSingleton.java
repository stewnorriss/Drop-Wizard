package uk.co.markberridge.dropwizard.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;

import com.google.common.base.Supplier;

public class ClusterSingleton extends UntypedActor {

    private static final Logger log = LoggerFactory.getLogger(ClusterSingleton.class);

    private final Cluster cluster;
    private final SingletonHealthCheck healthCheck;

    public ClusterSingleton(SingletonHealthCheck healthCheck, Supplier<Runnable> action) {

        // notify the health check that I am now the master
        this.healthCheck = healthCheck;
        this.healthCheck.setMaster(true);

        this.cluster = Cluster.get(getContext().system());

        if (action.get() == null) {
            throw new IllegalStateException("No action has been specified to run as singleton!");
        }
        action.get().run();
    }

    @Override
    public void preStart() {
        // subscribe to cluster changes
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
    }

    @Override
    public void postStop() {
        // un-subscribe on stop
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof MemberUp) {
            MemberUp mUp = (MemberUp) message;
            log.debug("########## Member is Up: {}", mUp.member());

        } else if (message instanceof UnreachableMember) {
            UnreachableMember mUnreachable = (UnreachableMember) message;
            log.debug("########## Member detected as unreachable: {}", mUnreachable.member());

        } else if (message instanceof MemberRemoved) {
            MemberRemoved mRemoved = (MemberRemoved) message;
            log.debug("########## Member is Removed: {}", mRemoved.member());

        } else if (message instanceof MemberEvent) {
            // ignore
        } else {
            unhandled(message);
        }
    }
}
