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

import com.fasterxml.jackson.annotation.JsonSetter;
import io.gravitee.definition.model.Proxy;
import io.gravitee.definition.model.VirtualHost;

import java.util.Collections;

public class JProxy extends Proxy {

    @JsonSetter("context_path")
    public void setContextPath(String contextPath) {
        if (contextPath == null){
            return;
        }
        String sContextPath = formatContextPath(contextPath);
        VirtualHost defaultHost = new VirtualHost();
        defaultHost.setPath(sContextPath);
        setVirtualHosts(Collections.singletonList(defaultHost));
    }

    private String formatContextPath(String contextPath) {
        String [] parts = contextPath.split("/");
        StringBuilder finalPath = new StringBuilder("/");

        for(String part : parts) {
            if (! part.isEmpty()) {
                finalPath.append(part).append('/');
            }
        }

        return finalPath.deleteCharAt(finalPath.length() - 1).toString();
    }
}
