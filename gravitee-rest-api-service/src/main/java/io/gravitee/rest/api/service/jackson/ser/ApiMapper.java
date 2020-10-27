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

import io.gravitee.definition.model.Path;
import io.gravitee.definition.model.Proxy;
import io.gravitee.definition.model.Rule;
import io.gravitee.definition.model.VirtualHost;
import io.gravitee.repository.management.model.Api;
import io.gravitee.rest.api.model.ApiMetadataEntity;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.api.UpdateApiEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ApiMapper {

    UpdateApiEntity map(JApiEntity source);

    default Map<String, Path> map(Map<String, List<Rule>> source) {
        Map<String, Path> result = new LinkedHashMap<>();
        source.forEach((path, rules) -> {
            Path pathEntry = new Path();
            pathEntry.setPath(path);
            pathEntry.setRules(rules);
            result.put(path, pathEntry);
        });
        return result;
    }

    @Mapping(target = "entrypoints", ignore = true)
    void update(JApiEntity api, @MappingTarget ApiEntity apiEntity);

    @Mapping(target = "workflowState", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "responseTemplates", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "proxy", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "primaryOwner", ignore = true)
    @Mapping(target = "pictureUrl", ignore = true)
    @Mapping(target = "pathRules", ignore = true)
    @Mapping(target = "pathMappings", ignore = true)
    @Mapping(target = "entrypoints", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "contextPath", ignore = true) // mapped by adjustApi
    @Mapping(source = "apiLifecycleState", target = "lifecycleState")
    @Mapping(source = "lifecycleState", target = "state")
    void update(Api api, @MappingTarget ApiEntity apiEntity);

    @AfterMapping
    default void adjustApi(@MappingTarget ApiEntity apiEntity) {
        // Issue https://github.com/gravitee-io/issues/issues/3356
        Optional.ofNullable(apiEntity.getProxy())
                .map(Proxy::getVirtualHosts)
                .flatMap(virtualHosts -> virtualHosts.stream().findFirst())
                .map(VirtualHost::getPath)
                .ifPresent(apiEntity::setContextPath);
    }

    default Map<String, Object> map(List<ApiMetadataEntity> value) {
        return null;
    }
}
