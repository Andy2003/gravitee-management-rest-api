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
package io.gravitee.rest.api.service;

public enum ApiVersion {
    DEFAULT("default"), V_1_15("1.15"), V_1_20("1.20"), V_1_25("1.25"), V_3_0("3.0");
    private final String version;

    ApiVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }
}
