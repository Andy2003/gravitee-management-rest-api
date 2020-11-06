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

import io.gravitee.definition.model.Proxy;
import io.gravitee.rest.api.service.jackson.ser.api.v1_15.ProxyBaseV1_15;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@MapperConfig
public interface ApiMappingConfig {

    @Mapping(target = "properties", source = "properties.properties")
    BaseApiExport mapBase1_15(ApiExport source);

    @Mapping(target = "contextPath", ignore = true)
    ProxyBaseV1_15 mapBaseProxy(Proxy proxy);

}
