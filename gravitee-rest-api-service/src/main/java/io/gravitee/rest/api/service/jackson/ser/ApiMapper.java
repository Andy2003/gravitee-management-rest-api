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

import io.gravitee.definition.model.*;
import io.gravitee.definition.model.endpoint.HttpEndpoint;
import io.gravitee.rest.api.model.PlanEntity;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.service.jackson.ser.api.common.ApiExport;
import io.gravitee.rest.api.service.jackson.ser.api.common.ApiMappingConfig;
import io.gravitee.rest.api.service.jackson.ser.api.common.BaseApiExport;
import io.gravitee.rest.api.service.jackson.ser.api.common.Rule;
import io.gravitee.rest.api.service.jackson.ser.api.v1_15.ApiV1_15;
import io.gravitee.rest.api.service.jackson.ser.api.v1_15.ProxyBaseV1_15;
import io.gravitee.rest.api.service.jackson.ser.api.v1_20.ApiV1_20;
import io.gravitee.rest.api.service.jackson.ser.api.v1_25.ApiV1_25;
import io.gravitee.rest.api.service.jackson.ser.api.v3_0.ApiV3_0;
import io.gravitee.rest.api.service.jackson.ser.api.v3_x.ApiV3_x;
import org.mapstruct.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(config = ApiMappingConfig.class, componentModel = "spring", mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG)
public interface ApiMapper {

    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "plans", ignore = true)
    @Mapping(target = "pages", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "apiMedia", ignore = true)
    ApiExport update(@MappingTarget ApiExport apiExport, ApiEntity apiEntity);

    ApiV1_15 mapV1_15(ApiExport apiEntity);

    ApiV1_20 mapV1_20(ApiExport apiEntity);

    ApiV1_25 mapV1_25(ApiExport apiEntity);

    ApiV3_0 mapV3_0(ApiExport apiEntity);

    ApiV3_x mapV3_x(ApiExport apiEntity);

    @Mapping(target = "contextPath", ignore = true)
    @Mapping(target = "loadBalancer", ignore = true)
    @Mapping(target = "loggingMode", source = "logging.mode", defaultValue = "NONE")
    @Mapping(target = "endpoints", source = "groups")
    ApiV1_15.Proxy mapV1_15(Proxy proxy);

    List<Rule> map(List<io.gravitee.definition.model.Rule> value);

    @Mapping(target = "fixedPaths", source = "paths")
    io.gravitee.rest.api.service.jackson.ser.api.v3_0.ApiV3_0.PlanEntity map(PlanEntity planEntity);

    default List<Endpoint> mapEndpointGroup(Collection<EndpointGroup> group) {
        return group.stream()
                .map(EndpointGroup::getEndpoints)
                .flatMap(this::mapHttpEndpoint)
                .collect(Collectors.toList());
    }

    default Stream<Endpoint> mapHttpEndpoint(Collection<Endpoint> endpoints) {
        if (endpoints == null) {
            return Stream.empty();
        }
        return endpoints.stream().peek(endpoint -> {
            if (endpoint instanceof HttpEndpoint) {
                fix(((HttpEndpoint) endpoint).getHttpClientOptions());
            }
        });
    }

    default void fix(HttpClientOptions http) {
        if (http != null && http.getVersion() == ProtocolVersion.HTTP_1_1) {
            http.setClearTextUpgrade(null);
            http.setVersion(null);
        }
    }

    Set<EndpointGroup> mapGroups(Collection<EndpointGroup> group);

    EndpointGroup mapGroup(EndpointGroup group);

    default Set<Endpoint> mapHttpEndpoint2(Collection<Endpoint> endpoints) {
        return mapHttpEndpoint(endpoints).collect(Collectors.toSet());
    }

    @BeforeMapping
    default void prepare(ApiExport source, @TargetType Class<?> targetType) {
        if ((ApiV1_15.class.isAssignableFrom(targetType) ||
                ApiV1_20.class.isAssignableFrom(targetType) ||
                ApiV1_25.class.isAssignableFrom(targetType) ||
                ApiV3_0.class.isAssignableFrom(targetType)
        ) && !ApiV3_x.class.isAssignableFrom(targetType)) {
            Set<EndpointGroup> groups = source.getProxy().getGroups();
            if (groups != null) {
                groups.forEach(grp -> {
                    if (grp.getEndpoints() != null) {
                        grp.setEndpoints(grp.getEndpoints()
                                .stream()
                                .filter(endpoint -> endpoint.getType() == EndpointType.HTTP)
                                .collect(Collectors.toSet()));
                    }
                });
            }
        }
        source.getProxy().getGroups().forEach(endpointGroup -> {
            if (endpointGroup.getHttpClientOptions() == null) {
                HttpClientOptions httpClientOptions = new HttpClientOptions();
                fix(httpClientOptions);
                endpointGroup.setHttpClientOptions(httpClientOptions);
            }
            for (Endpoint endpoint : endpointGroup.getEndpoints()) {
                if (endpoint instanceof HttpEndpoint) {
                    if (((HttpEndpoint) endpoint).getHttpClientOptions() == null) {
                        HttpClientOptions httpClientOptions = new HttpClientOptions();
                        fix(httpClientOptions);
                        ((HttpEndpoint) endpoint).setHttpClientOptions(httpClientOptions);
                    }
                }
            }
        });
    }

    @AfterMapping
    default void fix(@MappingTarget ApiV1_15.Proxy target, Proxy source) {
        target.loadBalancer = source.getGroups().iterator().next().getLoadBalancer();
    }

    @AfterMapping
    default void fix(@MappingTarget BaseApiExport target) {
        if (target.services != null && target.services.isEmpty()) {
            target.services = null;
        }
    }

    @AfterMapping
    default void fix(@MappingTarget EndpointGroup target) {
        if (target.getServices() != null && target.getServices().isEmpty()) {
            target.setServices(null);
        }
    }

    @AfterMapping
    default void fix(@MappingTarget ApiV1_20.Group target) {
        if (target.services != null && target.services.isEmpty()) {
            target.services = null;
        }
    }

    @AfterMapping
    default void getContextPath(@MappingTarget ProxyBaseV1_15 target, Proxy source) {
        target.contextPath = Optional.ofNullable(source)
                .map(Proxy::getVirtualHosts)
                .flatMap(vh -> vh.stream().findFirst())
                .map(VirtualHost::getPath)
                .orElse(null);
    }
}

