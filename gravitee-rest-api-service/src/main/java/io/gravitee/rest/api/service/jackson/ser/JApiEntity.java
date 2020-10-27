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
package io.gravitee.rest.api.service.jackson.ser;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.common.component.Lifecycle;
import io.gravitee.definition.model.Path;
import io.gravitee.definition.model.Properties;
import io.gravitee.definition.model.Proxy;
import io.gravitee.definition.model.ResponseTemplates;
import io.gravitee.definition.model.Rule;
import io.gravitee.definition.model.plugins.resources.Resource;
import io.gravitee.definition.model.services.Services;
import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.model.api.ApiLifecycleState;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.*;

@JsonFilter("apiMembershipTypeFilter")
public class JApiEntity {

    @ApiModelProperty(
            value = "API's uuid.",
            example = "00f8c9e7-78fc-4907-b8c9-e778fc790750")
    private String id;

    @ApiModelProperty(
            value = "API's name. Duplicate names can exists.",
            example = "My Api")
    private String name;

    @ApiModelProperty(
            value = "Api's version. It's a simple string only used in the portal.",
            example = "v1.0")
    private String version;

    @ApiModelProperty(
            value = "API's description. A short description of your API.",
            example = "I can use a hundred characters to describe this API.")
    private String description;

    @ApiModelProperty(
            value = "API's groups. Used to add team in your API.",
            dataType = "java.util.List",
            example = "['MY_GROUP1', 'MY_GROUP2']")
    private Set<String> groups;

    @JsonProperty(value = "context_path")
    @ApiModelProperty(
            value = "API's context path.",
            example = "/my-awesome-api")
    private String contextPath;

    @NotNull
    @DeploymentRequired
    @JsonProperty(value = "proxy", required = true)
    @ApiModelProperty(
            value = "API's definition.")
    private JProxy proxy;

    @DeploymentRequired
    @JsonProperty(value = "paths", required = true)
    @ApiModelProperty(
            value = "a map where you can associate a path to a configuration (the policies configuration)")
    private Map<String, List<Rule>> paths = new HashMap<>();

    @JsonProperty("deployed_at")
    @ApiModelProperty(
            value = "The last date (as timestamp) when the API was deployed.",
            example = "1581256457163")
    private Date deployedAt;

    @JsonProperty("created_at")
    @ApiModelProperty(
            value = "The date (as a timestamp) when the API was created.",
            example = "1581256457163")
    private Date createdAt;

    @JsonProperty("updated_at")
    @ApiModelProperty(
            value = "The last date (as a timestamp) when the API was updated.",
            example = "1581256457163")
    private Date updatedAt;

    @ApiModelProperty(
            value = "The visibility of the API regarding the portal.",
            example = "PUBLIC",
            allowableValues = "PUBLIC, PRIVATE")
    private Visibility visibility;

    @ApiModelProperty(
            value = "The status of the API regarding the gateway.",
            example = "STARTED",
            allowableValues = "INITIALIZED, STOPPED, STARTED, CLOSED")
    private Lifecycle.State state;

    @JsonProperty("owner")
    @ApiModelProperty(
            value = "The user with role PRIMARY_OWNER on this API.")
    private PrimaryOwnerEntity primaryOwner;

    @DeploymentRequired
    @JsonProperty(value = "properties")
    @ApiModelProperty(
            value = "A dictionary (could be dynamic) of properties available in the API context.")
    private io.gravitee.definition.model.Properties properties;

    @DeploymentRequired
    @JsonProperty(value = "services")
    @ApiModelProperty(
            value = "The configuration of API services like the dynamic properties, the endpoint discovery or the healthcheck.")
    private Services services;

    @DeploymentRequired
    @ApiModelProperty(
            value = "the list of sharding tags associated with this API.",
            dataType = "java.util.List",
            example = "public, private")
    private Set<String> tags;

    @ApiModelProperty(
            value = "the API logo encoded in base64")
    private String picture;

    @JsonProperty(value = "picture_url")
    @ApiModelProperty(
            value = "the API logo url.",
            example = "https://gravitee.mycompany.com/management/apis/6c530064-0b2c-4004-9300-640b2ce0047b/picture")
    private String pictureUrl;

    @DeploymentRequired
    @JsonProperty(value = "resources")
    @ApiModelProperty(
            value = "The list of API resources used by policies like cache resources or oauth2")
    private List<JResource> resources = new ArrayList<>();

    @ApiModelProperty(
            value = "the list of categories associated with this API",
            dataType = "java.util.List",
            example = "Product, Customer, Misc")
    private Set<String> categories;

    @ApiModelProperty(
            value = "the free list of labels associated with this API",
            dataType = "java.util.List",
            example = "json, read_only, awesome")
    private List<String> labels;

    @DeploymentRequired
    @JsonProperty(value = "path_mappings")
    @ApiModelProperty(
            value = "A list of paths used to aggregate data in analytics",
            dataType = "java.util.List",
            example = "/products/:productId, /products/:productId/media")
    private Set<String> pathMappings = new HashSet<>();

    private List<ApiMetadataEntity> metadata = new ArrayList<>();

    @DeploymentRequired
    @JsonProperty(value = "response_templates")
    @ApiModelProperty(
            value = "A map that allows you to configure the output of a request based on the event throws by the gateway. Example : Quota exceeded, api-ky is missing, ...")
    private Map<String, ResponseTemplates> responseTemplates;

    @JsonProperty(value = "lifecycle_state")
    private ApiLifecycleState lifecycleState;

    @JsonProperty(value = "workflow_state")
    private WorkflowState workflowState;

    @JsonProperty("disable_membership_notifications")
    private boolean disableMembershipNotifications;

    public String getId() {
        return id;
    }

    public JApiEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public JApiEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public JApiEntity setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public JApiEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public JApiEntity setGroups(Set<String> groups) {
        this.groups = groups;
        return this;
    }

    public String getContextPath() {
        return contextPath;
    }

    public JApiEntity setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public JProxy getProxy() {
        return proxy;
    }

    public JApiEntity setProxy(JProxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public Map<String, List<Rule>> getPaths() {
        return paths;
    }

    public JApiEntity setPaths(Map<String, List<Rule>> paths) {
        this.paths = paths;
        return this;
    }

    public Date getDeployedAt() {
        return deployedAt;
    }

    public JApiEntity setDeployedAt(Date deployedAt) {
        this.deployedAt = deployedAt;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public JApiEntity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public JApiEntity setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public JApiEntity setVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    public Lifecycle.State getState() {
        return state;
    }

    public JApiEntity setState(Lifecycle.State state) {
        this.state = state;
        return this;
    }

    public PrimaryOwnerEntity getPrimaryOwner() {
        return primaryOwner;
    }

    public JApiEntity setPrimaryOwner(PrimaryOwnerEntity primaryOwner) {
        this.primaryOwner = primaryOwner;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    public JApiEntity setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public Services getServices() {
        return services;
    }

    public JApiEntity setServices(Services services) {
        this.services = services;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public JApiEntity setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public String getPicture() {
        return picture;
    }

    public JApiEntity setPicture(String picture) {
        this.picture = picture;
        return this;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public JApiEntity setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
        return this;
    }

    public List<JResource> getResources() {
        return resources;
    }

    public JApiEntity setResources(List<JResource> resources) {
        this.resources = resources;
        return this;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public JApiEntity setCategories(Set<String> categories) {
        this.categories = categories;
        return this;
    }

    public List<String> getLabels() {
        return labels;
    }

    public JApiEntity setLabels(List<String> labels) {
        this.labels = labels;
        return this;
    }

    public Set<String> getPathMappings() {
        return pathMappings;
    }

    public JApiEntity setPathMappings(Set<String> pathMappings) {
        this.pathMappings = pathMappings;
        return this;
    }

    public List<ApiMetadataEntity> getMetadata() {
        return metadata;
    }

    public JApiEntity setMetadata(List<ApiMetadataEntity> metadata) {
        this.metadata = metadata;
        return this;
    }

    public Map<String, ResponseTemplates> getResponseTemplates() {
        return responseTemplates;
    }

    public JApiEntity setResponseTemplates(Map<String, ResponseTemplates> responseTemplates) {
        this.responseTemplates = responseTemplates;
        return this;
    }

    public ApiLifecycleState getLifecycleState() {
        return lifecycleState;
    }

    public JApiEntity setLifecycleState(ApiLifecycleState lifecycleState) {
        this.lifecycleState = lifecycleState;
        return this;
    }

    public WorkflowState getWorkflowState() {
        return workflowState;
    }

    public JApiEntity setWorkflowState(WorkflowState workflowState) {
        this.workflowState = workflowState;
        return this;
    }

    public boolean isDisableMembershipNotifications() {
        return disableMembershipNotifications;
    }

    public JApiEntity setDisableMembershipNotifications(boolean disableMembershipNotifications) {
        this.disableMembershipNotifications = disableMembershipNotifications;
        return this;
    }
}
