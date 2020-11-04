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
package io.gravitee.rest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.rest.api.model.application.ApplicationSettings;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ApplicationEntity {

    @Schema(description = "Application's uuid.", example = "00f8c9e7-78fc-4907-b8c9-e778fc790750")
    private String id;

    @Schema(description = "Application's name. Duplicate names can exists.", example = "My App")
    private String name;

    @Schema(description = "Application's description. A short description of your App.", example = "I can use a hundred characters to describe this App.")
    private String description;

    @Schema(description = "Application's groups. Used to add team in your App.", example = "['MY_GROUP1', 'MY_GROUP2']")
    private Set<String> groups;

    @Schema(description = "if the app is ACTIVE or ARCHIVED.", example = "ACTIVE")
    private String status;

    @Schema(description = "a string to describe the type of your app.", example = "iOS")
    private String type;
    private String picture;

    @JsonProperty("created_at")
    @Schema(description = "The date (as a timestamp) when the application was created.", example = "1581256457163")
    private Date createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "The last date (as a timestamp) when the application was updated.", example = "1581256457163")
    private Date updatedAt;

    @JsonProperty("owner")
    @Schema(description = "The user with role PRIMARY_OWNER on this API.")
    private PrimaryOwnerEntity primaryOwner;

    @JsonProperty("settings")
    private ApplicationSettings settings;

    @JsonProperty("disable_membership_notifications")
    private boolean disableMembershipNotifications;

    private String background;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PrimaryOwnerEntity getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(PrimaryOwnerEntity primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApplicationSettings getSettings() {
        return settings;
    }

    public void setSettings(ApplicationSettings settings) {
        this.settings = settings;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isDisableMembershipNotifications() {
        return disableMembershipNotifications;
    }

    public void setDisableMembershipNotifications(boolean disableMembershipNotifications) {
        this.disableMembershipNotifications = disableMembershipNotifications;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationEntity that = (ApplicationEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Application{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", createdAt=").append(createdAt).append('\'');
        sb.append(", disableMembershipNotifications='").append(disableMembershipNotifications);
        sb.append('}');
        return sb.toString();
    }
}
