package com.benevolo.client;

import com.benevolo.entity.TicketType;
import io.quarkus.oidc.token.propagation.AccessToken;
import io.quarkus.oidc.token.propagation.reactive.AccessTokenRequestReactiveFilter;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterClientHeaders
@RegisterRestClient(configKey = "event-service")
public interface TicketTypeClient {

    @GET
    @Path("/ticket-types/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    TicketType findById(@PathParam("id") String id);

}