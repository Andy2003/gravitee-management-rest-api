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

import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.rest.model.Subscription;
import io.gravitee.rest.api.management.rest.security.Permission;
import io.gravitee.rest.api.management.rest.security.Permissions;
import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.model.parameters.Key;
import io.gravitee.rest.api.model.parameters.ParameterReferenceType;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.service.*;
import io.gravitee.rest.api.validator.CustomApiKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static io.gravitee.rest.api.model.SubscriptionStatus.*;
import static io.gravitee.rest.api.model.permissions.RolePermissionAction.UPDATE;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@Tag(name = "API Subscriptions")
public class ApiSubscriptionResource extends AbstractResource {

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ApiKeyService apiKeyService;

    @Inject
    private PlanService planService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private UserService userService;

    @Inject
    private ParameterService parameterService;

    @SuppressWarnings("UnresolvedRestParam")
    @PathParam("api")
    @Parameter(name = "api", hidden = true)
    private String api;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a subscription", description = "User must have the MANAGE_PLANS permission to use this service")
    @ApiResponse(responseCode = "200", description = "Get a subscription",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Subscription.class)))
    @ApiResponse(responseCode = "404", description = "Subscription does not exist")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = RolePermissionAction.READ)
    })
    public Subscription getApiSubscription(
            @PathParam("subscription") String subscription) {
        return convert(subscriptionService.findById(subscription));
    }

    @POST
    @Path("/_process")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a subscription", description = "User must have the MANAGE_PLANS permission to use this service")
    @ApiResponse(responseCode = "200", description = "Update a subscription",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Subscription.class)))
    @ApiResponse(responseCode = "400", description = "Bad subscription format")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = UPDATE)
    })
    public Response processApiSubscription(
            @PathParam("subscription") String subscription,
            @Parameter(name = "subscription", required = true) @Valid @NotNull ProcessSubscriptionEntity processSubscriptionEntity) {

        if (processSubscriptionEntity.getId() != null && !subscription.equals(processSubscriptionEntity.getId())) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("'subscription' parameter does not correspond to the subscription to process")
                    .build();
        }

        // Force subscription ID
        processSubscriptionEntity.setId(subscription);

        SubscriptionEntity subscriptionEntity = subscriptionService.process(processSubscriptionEntity, getAuthenticatedUser());
        return Response.ok(convert(subscriptionEntity)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a subscription", description = "User must have the MANAGE_PLANS permission to use this service")
    @ApiResponse(responseCode = "200", description = "Update a subscription",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Subscription.class)))
    @ApiResponse(responseCode = "400", description = "Bad subscription format")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = UPDATE)
    })
    public Response updateApiSubscription(
            @PathParam("subscription") String subscription,
            @Parameter(name = "subscription", required = true) @Valid @NotNull UpdateSubscriptionEntity updateSubscriptionEntity) {

        if (updateSubscriptionEntity.getId() != null && !subscription.equals(updateSubscriptionEntity.getId())) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("'subscription' parameter does not correspond to the subscription to update")
                    .build();
        }

        // Force ID
        updateSubscriptionEntity.setId(subscription);

        SubscriptionEntity subscriptionEntity = subscriptionService.update(updateSubscriptionEntity);
        return Response.ok(convert(subscriptionEntity)).build();
    }

    @POST
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Change the status of a subscription", description = "User must have the MANAGE_PLANS permission to use this service")
    @ApiResponse(responseCode = "200", description = "Subscription status successfully updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Subscription.class)))
    @ApiResponse(responseCode = "400", description = "Status changes not authorized")
    @ApiResponse(responseCode = "404", description = "API subscription does not exist")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = UPDATE)
    })
    public Response changeApiSubscriptionStatus(
            @PathParam("subscription") String subscription,
            @Parameter(required = true, schema = @Schema(allowableValues = {"CLOSED", "PAUSED", "RESUMED"}))
            @QueryParam("status") SubscriptionStatus subscriptionStatus) {
        if (CLOSED.equals(subscriptionStatus)) {
            SubscriptionEntity updatedSubscriptionEntity = subscriptionService.close(subscription);
            return Response.ok(convert(updatedSubscriptionEntity)).build();
        } else if (PAUSED.equals(subscriptionStatus)) {
            SubscriptionEntity updatedSubscriptionEntity = subscriptionService.pause(subscription);
            return Response.ok(convert(updatedSubscriptionEntity)).build();
        } else if (RESUMED.equals(subscriptionStatus)) {
            SubscriptionEntity updatedSubscriptionEntity = subscriptionService.resume(subscription);
            return Response.ok(convert(updatedSubscriptionEntity)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/keys")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List all API Keys for a subscription", description = "User must have the MANAGE_API_KEYS permission to use this service")
    @ApiResponse(responseCode = "200", description = "List of API Keys for a subscription",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema(implementation = ApiKeyEntity.class))))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = RolePermissionAction.READ)
    })
    public List<ApiKeyEntity> getApiKeysForSubscription(
            @PathParam("subscription") String subscription) {
        return apiKeyService.findBySubscription(subscription);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Renew an API key", description = "User must have the MANAGE_API_KEYS permission to use this service")
    @ApiResponse(responseCode = "201", description = "A new API Key",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiKeyEntity.class)))
    @ApiResponse(responseCode = "400", description = "Bad custom API Key format or custom API Key definition disabled")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = UPDATE)
    })
    public Response renewApiKey(
            @PathParam("subscription") String subscription,
            @Parameter(name = "customApiKey")
            @CustomApiKey @QueryParam("customApiKey") String customApiKey) {

        if (StringUtils.isNotEmpty(customApiKey)
                && !parameterService.findAsBoolean(Key.PLAN_SECURITY_APIKEY_CUSTOM_ALLOWED, ParameterReferenceType.ENVIRONMENT)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You are not allowed to provide a custom API Key")
                    .build();
        }

        ApiKeyEntity apiKeyEntity = apiKeyService.renew(subscription, customApiKey);
        return Response
                .created(this.getLocationHeader("keys", apiKeyEntity.getKey()))
                .entity(apiKeyEntity)
                .build();
    }

    @DELETE
    @Path("/keys/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Revoke an API key", description = "User must have the API_SUBSCRIPTION permission to use this service")
    @ApiResponse(responseCode = "204", description = "API key successfully revoked")
    @ApiResponse(responseCode = "400", description = "API Key does not correspond to the subscription")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = RolePermissionAction.DELETE)
    })
    public Response revokeSubscriptionApiKey(
            @PathParam("subscription") String subscription,
            @PathParam("key") String apiKey) {
        ApiKeyEntity apiKeyEntity = apiKeyService.findByKey(apiKey);
        if (apiKeyEntity.getSubscription() != null && !subscription.equals(apiKeyEntity.getSubscription())) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("'key' parameter does not correspond to the subscription")
                    .build();
        }

        apiKeyService.revoke(apiKey, true);

        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

    @POST
    @Path("/keys/{key}/_reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Reactivate an API key", description = "User must have the API_SUBSCRIPTION permission to use this service")
    @ApiResponse(responseCode = "204", description = "API key successfully reactivated")
    @ApiResponse(responseCode = "400", description = "API Key does not correspond to the subscription")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = RolePermissionAction.DELETE)
    })
    public Response reactivateApiKey(
            @PathParam("subscription") String subscription,
            @PathParam("key") String apiKey) {
        ApiKeyEntity apiKeyEntity = apiKeyService.findByKey(apiKey);
        if (apiKeyEntity.getSubscription() != null && !subscription.equals(apiKeyEntity.getSubscription())) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("'key' parameter does not correspond to the subscription")
                    .build();
        }

        ApiKeyEntity reactivated = apiKeyService.reactivate(apiKey);

        return Response.ok()
                .entity(reactivated)
                .build();
    }

    @POST
    @Path("/_transfer")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Transfer a subscription", description = "User must have the API_SUBSCRIPTION update permission to use this service")
    @ApiResponse(responseCode = "200", description = "Update a subscription",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Subscription.class)))
    @ApiResponse(responseCode = "400", description = "Bad subscription format")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({
            @Permission(value = RolePermission.API_SUBSCRIPTION, acls = UPDATE)
    })
    public Response transferApiSubscription(
            @PathParam("subscription") String subscription,
            @Parameter(name = "subscription", required = true) @Valid @NotNull TransferSubscriptionEntity transferSubscriptionEntity) {

        if (transferSubscriptionEntity.getId() != null && !subscription.equals(transferSubscriptionEntity.getId())) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("'subscription' parameter does not correspond to the subscription to process")
                    .build();
        }

        // Force subscription ID
        transferSubscriptionEntity.setId(subscription);

        SubscriptionEntity subscriptionEntity = subscriptionService.transfer(transferSubscriptionEntity, getAuthenticatedUser());
        return Response.ok(convert(subscriptionEntity)).build();
    }

    private Subscription convert(SubscriptionEntity subscriptionEntity) {
        Subscription subscription = new Subscription();

        subscription.setId(subscriptionEntity.getId());
        subscription.setCreatedAt(subscriptionEntity.getCreatedAt());
        subscription.setUpdatedAt(subscriptionEntity.getUpdatedAt());
        subscription.setStartingAt(subscriptionEntity.getStartingAt());
        subscription.setEndingAt(subscriptionEntity.getEndingAt());
        subscription.setProcessedAt(subscriptionEntity.getProcessedAt());
        subscription.setProcessedBy(subscriptionEntity.getProcessedBy());
        subscription.setRequest(subscriptionEntity.getRequest());
        subscription.setReason(subscriptionEntity.getReason());
        subscription.setRequest(subscriptionEntity.getRequest());
        subscription.setStatus(subscriptionEntity.getStatus());
        subscription.setSubscribedBy(
                new Subscription.User(
                        subscriptionEntity.getSubscribedBy(),
                        userService.findById(subscriptionEntity.getSubscribedBy()).getDisplayName()));
        subscription.setClientId(subscriptionEntity.getClientId());

        PlanEntity plan = planService.findById(subscriptionEntity.getPlan());
        subscription.setPlan(new Subscription.Plan(plan.getId(), plan.getName()));
        subscription.getPlan().setSecurity(plan.getSecurity());

        ApplicationEntity application = applicationService.findById(subscriptionEntity.getApplication());
        subscription.setApplication(
                new Subscription.Application(
                        application.getId(),
                        application.getName(),
                        application.getType(),
                        application.getDescription(),
                        new Subscription.User(
                                application.getPrimaryOwner().getId(),
                                application.getPrimaryOwner().getDisplayName()
                        )
                ));

        subscription.setClosedAt(subscriptionEntity.getClosedAt());
        subscription.setPausedAt(subscriptionEntity.getPausedAt());

        return subscription;
    }
}
