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
package io.gravitee.rest.api.service.jackson.ser.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.gravitee.definition.model.Property;
import io.gravitee.definition.model.plugins.resources.Resource;
import io.gravitee.definition.model.services.Services;
import io.gravitee.rest.api.model.ApiMetadataEntity;
import io.gravitee.rest.api.model.MediaEntity;
import io.gravitee.rest.api.model.PageEntity;
import io.gravitee.rest.api.model.Visibility;

import java.util.*;

/**
 * common exported properties
 */
public class BaseApiExport {
    public String name;
    public String version;
    public String description;
    public Visibility visibility;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Set<String> tags;
    public String picture;
    public Map<String, List<Rule>> paths;
    public Services services;
    public List<Resource> resources;
    public List<Property> properties = Collections.emptyList();
    public Set<String> categories;
    public List<String> labels;
    public Collection<String> groups;
    public Collection<PageEntity> pages;
    public Collection<MediaEntity> apiMedia;
    public Collection<ApiMetadataEntity> metadata;
}
