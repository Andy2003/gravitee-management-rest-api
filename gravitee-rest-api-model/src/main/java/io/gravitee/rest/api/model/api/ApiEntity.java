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
package io.gravitee.rest.api.model.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.common.component.Lifecycle;
import io.gravitee.definition.model.*;
import io.gravitee.definition.model.Properties;
import io.gravitee.definition.model.plugins.resources.Resource;
import io.gravitee.definition.model.services.Services;
import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.model.filtering.FilterableItem;
import io.gravitee.rest.api.model.search.Indexable;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.regex.Pattern;

/**
 * --------------------------------------------------------------------------------------------------------------
 * --------------------------------------------------------------------------------------------------------------
 * /!\ Do not forget to update {@see io.gravitee.rest.api.service.jackson.ser.api.ApiDefaultSerializer}
 * for each modification of the ApiEntity class to apply export API changes /!\
 * --------------------------------------------------------------------------------------------------------------
 * --------------------------------------------------------------------------------------------------------------
 *
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@JsonFilter("apiMembershipTypeFilter")
public class ApiEntity extends Api implements Indexable, FilterableItem {

    private String description;

    private Set<String> groups;

    private String contextPath;

    private Date deployedAt;

    private Date createdAt;

    private Date updatedAt;

    private Visibility visibility;

    private Lifecycle.State state;

    private PrimaryOwnerEntity primaryOwner;

    private String picture;

    private String pictureUrl;

    private Set<String> categories;

    private List<String> labels;

    private Map<String, Object> metadata = new HashMap<>();

    private ApiLifecycleState lifecycleState;

    private WorkflowState workflowState;

    private boolean disableMembershipNotifications;

    private List<ApiEntrypointEntity> entrypoints;

    private String background;

   private String backgroundUrl;

	public ApiEntity() {
		super(null);
	}

	@JsonCreator
	public ApiEntity(@JsonProperty(value = "proxy", required = true) Proxy proxy) {
		super(proxy);
	}

	@ApiModelProperty(
			value = "API's uuid.",
			example = "00f8c9e7-78fc-4907-b8c9-e778fc790750")
	@Override
    public String getId() {
        return super.getId();
    }

	@JsonProperty("created_at")
	@ApiModelProperty(
			value = "The date (as a timestamp) when the API was created.",
			example = "1581256457163")
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

	@ApiModelProperty(
			value = "API's name. Duplicate names can exists.",
			example = "My Api")
	@Override
	public String getName() {
		return super.getName();
	}

	@ApiModelProperty(
			value = "The visibility of the API regarding the portal.",
			example = "PUBLIC",
			allowableValues = "PUBLIC, PRIVATE")
	public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

	@JsonProperty("updated_at")
	@ApiModelProperty(
			value = "The last date (as a timestamp) when the API was updated.",
			example = "1581256457163")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

	@Override
	@ApiModelProperty(
			value = "Api's version. It's a simple string only used in the portal.",
			example = "v1.0")
    public String getVersion() {
        return super.getVersion();
    }

	@ApiModelProperty(
			value = "API's description. A short description of your API.",
			example = "I can use a hundred characters to describe this API.")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	@ApiModelProperty(
			value = "The status of the API regarding the gateway.",
			example = "STARTED",
			allowableValues = "INITIALIZED, STOPPED, STARTED, CLOSED")
    public Lifecycle.State getState() {
        return state;
    }

    public void setState(Lifecycle.State state) {
        this.state = state;
    }

	@NotNull
	@DeploymentRequired
	@ApiModelProperty(
			value = "API's definition.")
    @Override
    public Proxy getProxy() {
        return super.getProxy();
    }

	@DeploymentRequired
	@JsonProperty(value = "paths", required = true)
	@ApiModelProperty(
			// specify a type here because jackson der/ser for Path handle only array of rules
			dataType = "io.gravitee.rest.api.model.api.PathsSwaggerDef",
			value = "a map where you can associate a path to a configuration (the policies configuration)")
    @Override
    public Map<String, Path> getPaths() {
        return super.getPaths();
    }

	@JsonProperty("owner")
	@ApiModelProperty(
			value = "The user with role PRIMARY_OWNER on this API.")
    public PrimaryOwnerEntity getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(PrimaryOwnerEntity primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

	@DeploymentRequired
	@JsonProperty(value = "services")
	@ApiModelProperty(
			value = "The configuration of API services like the dynamic properties, the endpoint discovery or the healthcheck.")
	@Override
    public Services getServices() {
        return super.getServices();
    }

	@DeploymentRequired
	@JsonProperty(value = "properties")
	@ApiModelProperty(
			value = "A dictionary (could be dynamic) of properties available in the API context.")
    @Override
    public Properties getProperties() {
        return super.getProperties();
    }

	@DeploymentRequired
	@ApiModelProperty(
			value = "the list of sharding tags associated with this API.",
			example = "public, private")
    @Override
    public Set<String> getTags() {
        return super.getTags();
    }

	@JsonProperty("deployed_at")
	@ApiModelProperty(
			value = "The last date (as timestamp) when the API was deployed.",
			example = "1581256457163")
    public Date getDeployedAt() {
        return deployedAt;
    }

    public void setDeployedAt(Date deployedAt) {
        this.deployedAt = deployedAt;
    }

	@ApiModelProperty(
			value = "the API logo encoded in base64")
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

	@JsonProperty(value = "picture_url")
	@ApiModelProperty(
			value = "the API logo url.",
			example = "https://gravitee.mycompany.com/management/apis/6c530064-0b2c-4004-9300-640b2ce0047b/picture")
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

	@DeploymentRequired
	@ApiModelProperty(
			value = "The list of API resources used by policies like cache resources or oauth2")
    @Override
    public List<Resource> getResources() {
        return super.getResources();
    }


	@ApiModelProperty(
			value = "the list of categories associated with this API",
			example = "Product, Customer, Misc")
    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

	@ApiModelProperty(
			value = "API's groups. Used to add team in your API.",
			example = "['MY_GROUP1', 'MY_GROUP2']")
    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

	@ApiModelProperty(
			value = "the free list of labels associated with this API",
			example = "json, read_only, awesome")
    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

	@DeploymentRequired
	@ApiModelProperty(
			value = "A list of paths used to aggregate data in analytics",
			example = "/products/:productId, /products/:productId/media")
	@Override
	public Map<String, Pattern> getPathMappings() {
        return super.getPathMappings();
    }

	@JsonIgnore
	public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

	@DeploymentRequired
	@ApiModelProperty(
			value = "A map that allows you to configure the output of a request based on the event throws by the gateway. Example : Quota exceeded, api-ky is missing, ...")
	@Override
    public Map<String, ResponseTemplates> getResponseTemplates() {
        return super.getResponseTemplates();
    }

	@JsonProperty(value = "lifecycle_state")
    public ApiLifecycleState getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(ApiLifecycleState lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

	@JsonProperty(value = "workflow_state")
	public WorkflowState getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(WorkflowState workflowState) {
        this.workflowState = workflowState;
    }

    public List<ApiEntrypointEntity> getEntrypoints() {
        return entrypoints;
    }

    public void setEntrypoints(List<ApiEntrypointEntity> entrypoints) {
        this.entrypoints = entrypoints;
    }

	@JsonProperty(value = "context_path")
	@ApiModelProperty(
			value = "API's context path.",
			example = "/my-awesome-api")
    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

	@JsonProperty("disable_membership_notifications")
	public boolean isDisableMembershipNotifications() {
        return disableMembershipNotifications;
    }

    public void setDisableMembershipNotifications(boolean disableMembershipNotifications) {
        this.disableMembershipNotifications = disableMembershipNotifications;
    }

	@ApiModelProperty(
			value = "the API background encoded in base64")
    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

	@JsonProperty(value = "background_url")
	@ApiModelProperty(
			value = "the API background url.",
			example = "https://gravitee.mycompany.com/management/apis/6c530064-0b2c-4004-9300-640b2ce0047b/background")

	public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiEntity api = (ApiEntity) o;
        return Objects.equals(getId(), api.getId()) &&
                Objects.equals(getVersion(), api.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVersion());
    }

    public String toString() {
	    return "ApiEntity{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", version='" + getVersion() + '\'' +
                ", description='" + description + '\'' +
                ", proxy=" + getProxy() +
                ", paths=" + getPaths() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", visibility=" + visibility +
                ", state=" + state +
                ", primaryOwner=" + primaryOwner +
                ", tags=" + getTags() +
                ", category=" + categories +
                ", groups=" + groups +
                ", pathMappings=" + getPathMappings().keySet() +
                ", lifecycleState=" + lifecycleState +
                ", workflowState=" + workflowState +
                ", disableMembershipNotifications=" + disableMembershipNotifications +
                '}';
    }
}
