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
package io.gravitee.rest.api.service.jackson.ser.api.v1_20;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.definition.model.Endpoint;
import io.gravitee.definition.model.HttpProxy;
import io.gravitee.definition.model.LoadBalancer;
import io.gravitee.definition.model.services.Services;
import io.gravitee.rest.api.service.jackson.ser.api.common.BaseApiExport;
import io.gravitee.rest.api.service.jackson.ser.api.v1_15.MemberV1_15;
import io.gravitee.rest.api.service.jackson.ser.api.v1_15.PlanEntityBefore_3_00;

import java.util.*;


public class ApiV1_20 extends BaseApiExport {
    @JsonProperty("path_mappings")
    public Set<String> pathMappings = new HashSet<>();
    public Proxy proxy;
    public Collection<MemberV1_15> members;
    public Collection<PlanEntityBefore_3_00> plans;

    public static class Proxy extends ProxyBaseV1_20 {
        public List<Group> groups;
    }

    public static class Group {
        public String name = "default";
        public Set<Endpoint> endpoints;
        @JsonProperty("load_balancing")
        public LoadBalancer loadBalancer;
        public Services services;
        @JsonProperty("proxy")
        public HttpProxy httpProxy;
        public Map<String, String> headers;
    }

}
