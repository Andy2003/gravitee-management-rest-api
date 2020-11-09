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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.definition.model.ResponseTemplate;
import io.gravitee.definition.model.Rule;
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

    public static class PlanEntity extends io.gravitee.rest.api.model.PlanEntity{

        @JsonProperty("paths")
        private Map<String, List<io.gravitee.rest.api.service.jackson.ser.api.common.Rule>> fixedPaths = new HashMap<>();

        @JsonIgnore
        @Override
        public void setPaths(Map<String, List<Rule>> paths) {
            super.setPaths(paths);
        }

        @JsonIgnore
        @Override
        public Map<String, List<Rule>> getPaths() {
            return super.getPaths();
        }

        @JsonProperty("paths")
        public Map<String, List<io.gravitee.rest.api.service.jackson.ser.api.common.Rule>> getFixedPaths() {
            return fixedPaths;
        }

        @JsonProperty("paths")
        public PlanEntity setFixedPaths(Map<String, List<io.gravitee.rest.api.service.jackson.ser.api.common.Rule>> fixedPaths) {
            this.fixedPaths = fixedPaths;
            return this;
        }
    }
}
