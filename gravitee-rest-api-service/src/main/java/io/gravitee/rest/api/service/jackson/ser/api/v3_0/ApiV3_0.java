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
package io.gravitee.rest.api.service.jackson.ser.api.v3_0;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.definition.model.ResponseTemplate;
import io.gravitee.rest.api.model.PlanEntity;
import io.gravitee.rest.api.service.jackson.ser.api.common.BaseApiExport;

import java.util.*;

public class ApiV3_0 extends BaseApiExport {
    @JsonProperty("path_mappings")
    public Set<String> pathMappings = new HashSet<>();
    public ProxyBaseV3_0 proxy;
    public Collection<Member> members;
    public Collection<PlanEntity> plans;
    @JsonProperty(value = "response_templates")
    public Map<String, Map<String, ResponseTemplate>> responseTemplates;

    public static class Member {
        public String source;
        public String sourceId;
        public List<String> roles;
    }

}
