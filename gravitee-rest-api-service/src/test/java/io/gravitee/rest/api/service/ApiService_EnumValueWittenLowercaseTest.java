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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.rest.api.model.Visibility;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.service.spring.ServiceConfiguration;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Guillaume Gillon
 */
public class ApiService_EnumValueWittenLowercaseTest {

    private static final String API_ID = "id-api";

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
        objectMapper = serviceConfiguration.objectMapper();
    }

    @Test
    public void shouldConvertAsJsonForExportWithUppercaseEnum() throws IOException, JSONException {
        ApiEntity apiEntity = new ApiEntity();
        apiEntity.setId(API_ID);
        apiEntity.setName("test");
        apiEntity.setDescription("Gravitee.io");
        apiEntity.setVisibility(Visibility.PUBLIC);

        String result = objectMapper.writeValueAsString(apiEntity);
        JSONAssert.assertEquals("{\n" +
            "  \"name\" : \"test\",\n" +
            "  \"description\" : \"Gravitee.io\",\n" +
            "  \"visibility\" : \"PUBLIC\",\n" +
            "  \"paths\" : { },\n" +
            "  \"resources\" : [ ],\n" +
            "  \"disable_membership_notifications\" : false,\n" +
            "  \"id\" : \"id-api\",\n" +
            "  \"path_mappings\" : [ ]\n" +
            "}", result, JSONCompareMode.STRICT);
    }

    @Test
    public void shouldConvertAsObjectForImportWithLowercaseEnum() throws IOException {
        final String lowercaseApiDefinition = "{\n" +
            "  \"name\" : \"test\",\n" +
            "  \"description\" : \"Gravitee.io\",\n" +
            "  \"visibility\" : \"public\",\n" +
            "  \"paths\" : { },\n" +
            "  \"resources\" : [ ],\n" +
            "  \"path_mappings\" : [ ]\n" +
            "}";

        final ApiEntity apiEntity = objectMapper.readValue(lowercaseApiDefinition, ApiEntity.class);
        assertEquals(Visibility.PUBLIC, apiEntity.getVisibility());
    }

    @Test
    public void shouldConvertAsObjectForImportWithUppercaseEnum() throws IOException {
        final String lowercaseApiDefinition = "{\n" +
            "  \"name\" : \"test\",\n" +
            "  \"description\" : \"Gravitee.io\",\n" +
            "  \"visibility\" : \"PUBLIC\",\n" +
            "  \"paths\" : { },\n" +
            "  \"resources\" : [ ],\n" +
            "  \"path_mappings\" : [ ]\n" +
            "}";

        final ApiEntity apiEntity = objectMapper.readValue(lowercaseApiDefinition, ApiEntity.class);
        assertEquals(Visibility.PUBLIC, apiEntity.getVisibility());
    }
}
