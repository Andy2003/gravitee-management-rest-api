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

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

public class JResource {

    private boolean enabled = true;

    private String name;

    private String type;

    private Object configuration;

    public boolean isEnabled() {
        return enabled;
    }

    public JResource setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getName() {
        return name;
    }

    public JResource setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public JResource setType(String type) {
        this.type = type;
        return this;
    }


    @JsonRawValue
    public String getConfiguration() {
        return configuration == null ? null : configuration.toString();
    }

    public JResource setConfiguration(JsonNode configuration) {
        this.configuration = configuration;
        return this;
    }
}
