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
package io.gravitee.rest.api.management.rest.resource.organization;

import io.gravitee.common.http.MediaType;
import io.gravitee.repository.management.model.RoleScope;
import io.gravitee.rest.api.management.rest.resource.AbstractResource;
import io.gravitee.rest.api.model.permissions.ApiPermission;
import io.gravitee.rest.api.model.permissions.ApplicationPermission;
import io.gravitee.rest.api.model.permissions.EnvironmentPermission;
import io.gravitee.rest.api.model.permissions.OrganizationPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@Tag(name = "Roles")
public class RoleScopesResource extends AbstractResource {

    @Context
    private ResourceContext resourceContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List availables role scopes")
    @ApiResponse(responseCode = "200", description = "List of role scopes",
            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = RoleScopes.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public RoleScopes getRoleScopes() {
        final RoleScopes roles = new RoleScopes();
        roles.put(RoleScope.ORGANIZATION.name(), stream(OrganizationPermission.values()).map(OrganizationPermission::getName).sorted().collect(toList()));
        roles.put(RoleScope.ENVIRONMENT.name(), stream(EnvironmentPermission.values()).map(EnvironmentPermission::getName).sorted().collect(toList()));
        roles.put(RoleScope.API.name(), stream(ApiPermission.values()).map(ApiPermission::getName).sorted().collect(toList()));
        roles.put(RoleScope.APPLICATION.name(), stream(ApplicationPermission.values()).map(ApplicationPermission::getName).sorted().collect(toList()));
        return roles;
    }

    @Path("{scope}/roles")
    public RoleScopeResource getRoleScopeResource() {
        return resourceContext.getResource(RoleScopeResource.class);
    }

    public static class RoleScopes extends LinkedHashMap<String, List<String>> {
    }
}
