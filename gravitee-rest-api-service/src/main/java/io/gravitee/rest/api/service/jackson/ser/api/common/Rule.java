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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.gravitee.common.http.HttpMethod;
import io.gravitee.definition.model.Policy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Rule {
    private Set<HttpMethod> methods;

    @JsonIgnore
    private Policy policy;

    private String description;

    private boolean enabled = true;

    public Set<HttpMethod> getMethods() {
        return methods;
    }

    public void setMethods(Set<HttpMethod> methods) {
        this.methods = methods;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonAnySetter
    private void setPolicyJson(String name, JsonNode jsonNode) {
        policy = new Policy();
        policy.setName(name);
        policy.setConfiguration(jsonNode.toString());
    }

    @JsonSerialize(contentUsing = RawSerializer.class)
    @JsonAnyGetter
    public Map<String, Object> getPolicyJson() {
        if (policy == null) {
            return null;
        }
        return Collections.singletonMap(policy.getName(), policy.getConfiguration());
    }
}
