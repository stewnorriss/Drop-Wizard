package uk.co.markberridge.dropwizard.cluster.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/ping")
@Produces("text/plain")
public class PingResource {

    @GET
    public String ping() {
        return "pong";
    }
}
