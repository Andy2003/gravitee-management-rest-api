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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.definition.model.*;
import io.gravitee.definition.model.plugins.resources.Resource;
import io.gravitee.definition.model.services.Services;
import io.gravitee.rest.api.model.ApiMetadataEntity;
import io.gravitee.rest.api.model.Visibility;
import io.swagger.annotations.ApiModelProperty;

import static java.util.stream.Collectors.toMap;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class UpdateApiEntity extends Api {

	private String description;

	private Visibility visibility;

	private String picture;

	private String pictureUrl;

	private Set<String> categories;

	private List<String> labels;

	private Set<String> groups;

	private List<ApiMetadataEntity> metadata;

	private ApiLifecycleState lifecycleState;

	private boolean disableMembershipNotifications;

	private String background;

	@NotNull
	@NotEmpty(message = "Api's name must not be empty")
	@ApiModelProperty(
			value = "Api's name. Duplicate names can exists.",
			example = "My Api")
	public String getName() {
		return super.getName();
	}

	@NotNull
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

	@NotNull
	@ApiModelProperty(
			value = "Api's version. It's a simple string only used in the portal.",
			example = "v1.0")
	public String getVersion() {
		return super.getVersion();
	}

	@NotNull
	@ApiModelProperty(
			value = "API's description. A short description of your API.",
			example = "I can use a hundred characters to describe this API.")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@ApiModelProperty(
			value = "API's definition.",
			required = true)
	public Proxy getProxy() {
		return super.getProxy();
	}

	@ApiModelProperty(
			required = true,
			value = "a map where you can associate a path to a configuration (the policies configuration)")
	public Map<String, Path> getPaths() {
		return super.getPaths();
	}

	@ApiModelProperty(
			value = "The configuration of API services like the dynamic properties, the endpoint discovery or the healthcheck.")
	public Services getServices() {
		return super.getServices();
	}

	@ApiModelProperty(
			value = "A dictionary (could be dynamic) of properties available in the API context.")
	public Properties getProperties() {
		return super.getProperties();
	}

	@ApiModelProperty(
			value = "the list of sharding tags associated with this API.",
			example = "public, private")
	public Set<String> getTags() {
		return super.getTags();
	}

	@ApiModelProperty(
			value = "the API logo encoded in base64")
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@ApiModelProperty(
			value = "The list of API resources used by policies like cache resources or oauth2")
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
			value = "the free list of labels associated with this API",
			example = "json, read_only, awesome")
	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
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

	@Override
	@ApiModelProperty(
			value = "A list of paths used to aggregate data in analytics",
			example = "/products/:productId, /products/:productId/media")
	public Map<String, Pattern> getPathMappings() {
		return super.getPathMappings();
	}

	@JsonIgnore
	@Deprecated // TODO remove
	public void setPathMappings2(Set<String> pathMappings2) {
		setPathMappings(pathMappings2.stream()
				.collect(toMap(pathMapping -> pathMapping, pathMapping -> Pattern.compile(""))));
	}

	@ApiModelProperty(
			value = "A map that allows you to configure the output of a request based on the event throws by the gateway. Example : Quota exceeded, api-ky is missing, ...")
	public Map<String, ResponseTemplates> getResponseTemplates() {
		return super.getResponseTemplates();
	}

	public List<ApiMetadataEntity> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<ApiMetadataEntity> metadata) {
		this.metadata = metadata;
	}

	@JsonProperty(value = "lifecycle_state")
	public ApiLifecycleState getLifecycleState() {
		return lifecycleState;
	}

	public void setLifecycleState(ApiLifecycleState lifecycleState) {
		this.lifecycleState = lifecycleState;
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

	@JsonProperty("picture_url")
	@ApiModelProperty(
			value = "the API logo encoded in base64")
	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
}
