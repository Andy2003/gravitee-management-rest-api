/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.management.rest.resource;

import io.gravitee.common.data.domain.Page;
import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.rest.model.Pageable;
import io.gravitee.rest.api.management.rest.resource.param.TicketsParam;
import io.gravitee.rest.api.model.NewTicketEntity;
import io.gravitee.rest.api.model.TicketEntity;
import io.gravitee.rest.api.model.api.TicketQuery;
import io.gravitee.rest.api.model.common.Sortable;
import io.gravitee.rest.api.model.common.SortableImpl;
import io.gravitee.rest.api.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@Tag(name = "Platform Tickets")
public class PlatformTicketsResource extends AbstractResource  {

    @Inject
    private TicketService ticketService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a platform ticket")
    @ApiResponse(responseCode = "201", description = "Ticket succesfully created")
    @ApiResponse(responseCode = "500", description = "Internal server error")

    public Response createPlatformTicket(@Valid @NotNull final NewTicketEntity ticketEntity) {
        ticketService.create(getAuthenticatedUser(), ticketEntity);
        return Response.created(URI.create("")).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Search for platform tickets written by current user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "List platform tickets written by current user", response = TicketEntity.class, responseContainer = "Page"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Page<TicketEntity> getTickets(
            @Valid @BeanParam Pageable pageable,
            @Valid @BeanParam TicketsParam ticketsParam) {

        TicketQuery query = new TicketQuery();
        query.setApi(ticketsParam.getApi());
        query.setApplication(ticketsParam.getApplication());
        query.setApi(ticketsParam.getApi());
        query.setFromUser(getAuthenticatedUser());

        Sortable sortable = null;
        if (ticketsParam.getOrder() != null) {
            sortable = new SortableImpl(ticketsParam.getOrder().getField(), ticketsParam.getOrder().isOrder());
        }

        return ticketService.search(
                query,
                sortable,
                pageable.toPageable());
    }

    @GET
    @Path("/{ticket}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get a specific ticket")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Get a platform ticket", response = TicketEntity.class),
            @ApiResponse(code = 404, message = "Ticket not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    public Response getTicket(@PathParam("ticket") String ticketId) {

        TicketEntity ticketEntity = ticketService.findById(ticketId);

        return Response.ok(ticketEntity).build();
    }
}
