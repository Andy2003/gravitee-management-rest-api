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
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.gravitee.definition.jackson.datatype.GraviteeMapper;
import io.gravitee.definition.model.*;
import io.gravitee.definition.model.endpoint.HttpEndpoint;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.ApiRepository;
import io.gravitee.repository.management.model.Api;
import io.gravitee.repository.management.model.ApiLifecycleState;
import io.gravitee.repository.management.model.Workflow;
import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.api.UpdateApiEntity;
import io.gravitee.rest.api.model.parameters.Key;
import io.gravitee.rest.api.model.permissions.RoleScope;
import io.gravitee.rest.api.model.permissions.SystemRole;
import io.gravitee.rest.api.service.exceptions.*;
import io.gravitee.rest.api.service.impl.ApiServiceImpl;
import io.gravitee.rest.api.service.jackson.filter.ApiPermissionFilter;
import io.gravitee.rest.api.service.search.SearchEngineService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static io.gravitee.rest.api.model.WorkflowReferenceType.API;
import static io.gravitee.rest.api.model.WorkflowType.REVIEW;
import static io.gravitee.rest.api.model.api.ApiLifecycleState.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.collections.Sets.newSet;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiService_UpdateTest {

    private static final String API_ID = "id-api";
    private static final String API_ID2 = "id-api2";
    private static final String API_NAME = "myAPI";
    private static final String USER_NAME = "myUser";
    public static final String API_DEFINITION = "{\n" +
            "  \"description\" : \"Gravitee.io\",\n" +
            "  \"paths\" : { },\n" +
            "  \"path_mappings\":[],\n" +
            "  \"proxy\": {\n" +
            "    \"virtual_hosts\": [{\n" +
            "      \"path\": \"/test\"\n" +
            "    }],\n" +
            "    \"strip_context_path\": false,\n" +
            "    \"preserve_host\":false,\n" +
            "    \"logging\": {\n" +
            "      \"mode\":\"CLIENT_PROXY\",\n" +
            "      \"condition\":\"condition\"\n" +
            "    },\n" +
            "    \"groups\": [\n" +
            "      {\n" +
            "        \"name\": \"default-group\",\n" +
            "        \"endpoints\": [\n" +
            "          {\n" +
            "            \"name\": \"default\",\n" +
            "            \"target\": \"http://test\",\n" +
            "            \"weight\": 1,\n" +
            "            \"backup\": false,\n" +
            "            \"type\": \"HTTP\",\n" +
            "            \"http\": {\n" +
            "              \"connectTimeout\": 5000,\n" +
            "              \"idleTimeout\": 60000,\n" +
            "              \"keepAlive\": true,\n" +
            "              \"readTimeout\": 10000,\n" +
            "              \"pipelining\": false,\n" +
            "              \"maxConcurrentConnections\": 100,\n" +
            "              \"useCompression\": true,\n" +
            "              \"followRedirects\": false,\n" +
            "              \"encodeURI\":false\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        \"load_balancing\": {\n" +
            "          \"type\": \"ROUND_ROBIN\"\n" +
            "        },\n" +
            "        \"http\": {\n" +
            "          \"connectTimeout\": 5000,\n" +
            "          \"idleTimeout\": 60000,\n" +
            "          \"keepAlive\": true,\n" +
            "          \"readTimeout\": 10000,\n" +
            "          \"pipelining\": false,\n" +
            "          \"maxConcurrentConnections\": 100,\n" +
            "          \"useCompression\": true,\n" +
            "          \"followRedirects\": false,\n" +
            "          \"encodeURI\":false\n" +
            "        }\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}\n";

    @InjectMocks
    private ApiServiceImpl apiService = new ApiServiceImpl();

    @Mock
    private ApiRepository apiRepository;
    @Mock
    private MembershipService membershipService;
    @Mock
    private RoleService roleService;
    @Spy
    private ObjectMapper objectMapper = new GraviteeMapper();

    private UpdateApiEntity existingApi ;
    
    private Api api ;
    
    @Mock
    private UserService userService;
    @Mock
    private AuditService auditService;
    @Mock
    private SearchEngineService searchEngineService;
    @Mock
    private TagService tagService;
    @Mock
    private ParameterService parameterService;
    @Mock
    private WorkflowService workflowService;
    @Mock
    private VirtualHostService virtualHostService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private PolicyService policyService;
	@Mock
	private NotifierService notifierService;

    @Before
    public void setUp() {
    	existingApi = new UpdateApiEntity();
	    existingApi.setPaths(new LinkedHashMap<>());
        PropertyFilter apiMembershipTypeFilter = new ApiPermissionFilter();
        objectMapper.setFilterProvider(new SimpleFilterProvider(Collections.singletonMap("apiMembershipTypeFilter", apiMembershipTypeFilter)));

        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(mock(Authentication.class));
        SecurityContextHolder.setContext(securityContext);

	    api = new Api();
        api.setId(API_ID);
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"}}");
    }

    @AfterClass
    public static void cleanSecurityContextHolder() {
        // reset authentication to avoid side effect during test executions.
        SecurityContextHolder.setContext(new SecurityContext() {
            @Override
            public Authentication getAuthentication() {
                return null;
            }
            @Override
            public void setAuthentication(Authentication authentication) {
            }
        });
    }

    @Test
    public void shouldUpdate() throws TechnicalException {
        prepareUpdate();

        final ApiEntity apiEntity = apiService.update(API_ID, existingApi);

        assertNotNull(apiEntity);
        assertEquals(API_NAME, apiEntity.getName());
    }

    @Test(expected = ApiNotFoundException.class)
    public void shouldNotUpdateBecauseNotFound() throws TechnicalException {
        when(apiRepository.findById(API_ID)).thenReturn(Optional.empty());

        apiService.update(API_ID, existingApi);
    }

    @Test(expected = TechnicalManagementException.class)
    public void shouldNotUpdateBecauseTechnicalException() throws TechnicalException {
        existingApi.setName(API_NAME);
        existingApi.setVersion("v1");
        existingApi.setDescription("Ma description");
        final Proxy proxy = new Proxy();
        EndpointGroup endpointGroup = new EndpointGroup();
        Endpoint endpoint = new HttpEndpoint(null, null);
        endpointGroup.setEndpoints(singleton(endpoint));
        proxy.setGroups(singleton(endpointGroup));
        existingApi.setProxy(proxy);
        proxy.setVirtualHosts(singletonList(new VirtualHost("/context")));
        existingApi.setLifecycleState(CREATED);

        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));
        api.setApiLifecycleState(ApiLifecycleState.CREATED);
        when(apiRepository.update(any())).thenThrow(TechnicalException.class);

        apiService.update(API_ID, existingApi);
    }

    @Test(expected = EndpointNameInvalidException.class)
    public void shouldNotUpdateWithInvalidEndpointGroupName() throws TechnicalException {
        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));

        final Proxy proxy = new Proxy();
        existingApi.setProxy(proxy);
        proxy.setVirtualHosts(singletonList(new VirtualHost("/new")));
        final EndpointGroup group = new EndpointGroup();
        group.setName("inva:lid");
        proxy.setGroups(singleton(group));
        group.setEndpoints(singleton(new HttpEndpoint("","localhost")));

        apiService.update(API_ID, existingApi);

        fail("should throw EndpointNameInvalidException");
    }

    @Test(expected = EndpointNameInvalidException.class)
    public void shouldNotUpdateWithInvalidEndpointName() throws TechnicalException {
        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));

        final Proxy proxy = new Proxy();
        existingApi.setProxy(proxy);
        proxy.setVirtualHosts(singletonList(new VirtualHost("/new")));
        final EndpointGroup group = new EndpointGroup();
        group.setName("group");
        proxy.setGroups(singleton(group));
        Endpoint endpoint = new HttpEndpoint("inva:lid", "localhost");
        group.setEndpoints(singleton(endpoint));

        apiService.update(API_ID, existingApi);

        fail("should throw EndpointNameInvalidException");
    }

    @Test(expected = InvalidDataException.class)
    public void shouldNotUpdateWithInvalidPolicyConfiguration() throws TechnicalException {

        prepareUpdate();

        HashMap<String, Path> paths = new HashMap<>();
        Path path = new Path();
        path.setPath("/");
        ArrayList<Rule> rules = new ArrayList<>();
        Rule rule = new Rule();
        Policy policy = new Policy();
        rule.setPolicy(policy);
        rule.setEnabled(true);
        rules.add(rule);
        path.setRules(rules);
        paths.put("/", path);

        existingApi.setPaths(paths);
        doThrow(new InvalidDataException()).when(policyService).validatePolicyConfiguration(any(Policy.class));

        apiService.update(API_ID, existingApi);

        fail("should throw InvalidDataException");
    }

    private void prepareUpdate() throws TechnicalException {
        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));
        when(apiRepository.update(any())).thenReturn(api);
        api.setName(API_NAME);
        api.setApiLifecycleState(ApiLifecycleState.CREATED);

        existingApi.setName(API_NAME);
        existingApi.setVersion("v1");
        existingApi.setDescription("Ma description");
        final Proxy proxy = new Proxy();
        EndpointGroup endpointGroup = new EndpointGroup();
        Endpoint endpoint = new HttpEndpoint(null, null);
        endpointGroup.setEndpoints(singleton(endpoint));
        proxy.setGroups(singleton(endpointGroup));
        Cors cors = new Cors();
        cors.setAccessControlAllowOrigin(newSet("http://example.com", "localhost", "https://10.140.238.25:8080", "(http|https)://[a-z]{6}.domain.[a-zA-Z]{2,6}"));
        proxy.setCors(cors);
        existingApi.setProxy(proxy);
        existingApi.setLifecycleState(CREATED);
        proxy.setVirtualHosts(singletonList(new VirtualHost("/context")));
        
        RoleEntity poRoleEntity = new RoleEntity();
        poRoleEntity.setName(SystemRole.PRIMARY_OWNER.name());
        poRoleEntity.setScope(RoleScope.API);
        
        MemberEntity po = new MemberEntity();
        po.setId(USER_NAME);
        po.setReferenceId(API_ID);
        po.setReferenceType(MembershipReferenceType.API);
        po.setRoles(Collections.singletonList(poRoleEntity));
        when(membershipService.getMembersByReferencesAndRole(any(), any(), any())).thenReturn(Collections.singleton(po));
        when(roleService.findByScopeAndName(any(), any())).thenReturn(Optional.of(poRoleEntity));
    }

    @Test
    public void shouldUpdateWithAllowedTag() throws TechnicalException {
        prepareUpdate();
        existingApi.setTags(singleton("public"));
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"} ,\"tags\": [\"public\"]}");
        final ApiEntity apiEntity = apiService.update(API_ID, existingApi);
        assertNotNull(apiEntity);
        assertEquals(API_NAME, apiEntity.getName());
    }

    @Test
    public void shouldUpdateWithExistingAllowedTag() throws TechnicalException {
        prepareUpdate();
        existingApi.setTags(singleton("private"));
        Proxy proxy = new Proxy();
        EndpointGroup endpointGroup = new EndpointGroup();
        Endpoint endpoint = new HttpEndpoint(null, null);
        endpointGroup.setEndpoints(singleton(endpoint));
        proxy.setGroups(singleton(endpointGroup));
        existingApi.setProxy(proxy);
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"} ,\"tags\": [\"public\"]}");
        when(tagService.findByUser(any())).thenReturn(Sets.newSet("public", "private"));
        final ApiEntity apiEntity = apiService.update(API_ID, existingApi);
        assertNotNull(apiEntity);
        assertEquals(API_NAME, apiEntity.getName());
    }

    @Test
    public void shouldUpdateWithExistingAllowedTags() throws TechnicalException {
        prepareUpdate();
        existingApi.setTags(newSet("public", "private"));
        Proxy proxy = new Proxy();
        EndpointGroup endpointGroup = new EndpointGroup();
        Endpoint endpoint = new HttpEndpoint(null, null);
        endpointGroup.setEndpoints(singleton(endpoint));
        proxy.setGroups(singleton(endpointGroup));
        existingApi.setProxy(proxy);
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"} ,\"tags\": [\"public\"]}");
        when(tagService.findByUser(any())).thenReturn(Sets.newSet("public", "private"));
        final ApiEntity apiEntity = apiService.update(API_ID, existingApi);
        assertNotNull(apiEntity);
        assertEquals(API_NAME, apiEntity.getName());
    }

    @Test
    public void shouldUpdateWithExistingNotAllowedTag() throws TechnicalException {
        prepareUpdate();
        existingApi.setTags(newSet("public", "private"));
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"} ,\"tags\": [\"public\", \"private\"]}");
        final ApiEntity apiEntity = apiService.update(API_ID, existingApi);
        assertNotNull(apiEntity);
        assertEquals(API_NAME, apiEntity.getName());
    }

    @Test(expected = TagNotAllowedException.class)
    public void shouldNotUpdateWithNotAllowedTag() throws TechnicalException {
        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"} ,\"tags\": [\"public\"]}");
        existingApi.setTags(singleton("private"));
        final Proxy proxy = new Proxy();
        EndpointGroup endpointGroup = new EndpointGroup();
        Endpoint endpoint = new HttpEndpoint(null, null);
        endpointGroup.setEndpoints(singleton(endpoint));
        proxy.setGroups(singleton(endpointGroup));
        existingApi.setProxy(proxy);
        proxy.setVirtualHosts(singletonList(new VirtualHost("/context")));
        when(tagService.findByUser(any())).thenReturn(emptySet());
        apiService.update(API_ID, existingApi);
    }

    @Test(expected = TagNotAllowedException.class)
    public void shouldNotUpdateWithExistingNotAllowedTag() throws TechnicalException {
        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"} ,\"tags\": [\"public\"]}");
        existingApi.setTags(singleton("private"));
        final Proxy proxy = new Proxy();
        EndpointGroup endpointGroup = new EndpointGroup();
        Endpoint endpoint = new HttpEndpoint(null, null);
        endpointGroup.setEndpoints(singleton(endpoint));
        proxy.setGroups(singleton(endpointGroup));
        existingApi.setProxy(proxy);
        proxy.setVirtualHosts(singletonList(new VirtualHost("/context")));
        when(tagService.findByUser(any())).thenReturn(singleton("public"));
        apiService.update(API_ID, existingApi);
    }

    @Test(expected = TagNotAllowedException.class)
    public void shouldNotUpdateWithExistingNotAllowedTags() throws TechnicalException {
        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));
        api.setDefinition("{\"id\": \"" + API_ID + "\",\"name\": \"" + API_NAME + "\",\"proxy\": {\"context_path\": \"/old\"} ,\"tags\": [\"public\", \"private\"]}");
        existingApi.setTags(emptySet());
        final Proxy proxy = new Proxy();
        EndpointGroup endpointGroup = new EndpointGroup();
        Endpoint endpoint = new HttpEndpoint(null, null);
        endpointGroup.setEndpoints(singleton(endpoint));
        proxy.setGroups(singleton(endpointGroup));
        existingApi.setProxy(proxy);
        proxy.setVirtualHosts(singletonList(new VirtualHost("/context")));
        when(tagService.findByUser(any())).thenReturn(singleton("private"));
        apiService.update(API_ID, existingApi);
    }

    @Test
    public void shouldPublishApi() throws TechnicalException {
        prepareUpdate();
        // from UNPUBLISHED state
        existingApi.setLifecycleState(UNPUBLISHED);
        api.setApiLifecycleState(ApiLifecycleState.PUBLISHED);
        ApiEntity apiEntity = apiService.update(API_ID, existingApi);
        assertNotNull(apiEntity);
        assertEquals(io.gravitee.rest.api.model.api.ApiLifecycleState.PUBLISHED, apiEntity.getLifecycleState());
        // from CREATED state
        existingApi.setLifecycleState(CREATED);
        api.setApiLifecycleState(ApiLifecycleState.PUBLISHED);
        apiEntity = apiService.update(API_ID, existingApi);
        assertNotNull(apiEntity);
        assertEquals(io.gravitee.rest.api.model.api.ApiLifecycleState.PUBLISHED, apiEntity.getLifecycleState());
    }

    @Test
    public void shouldUnpublishApi() throws TechnicalException {
        prepareUpdate();
        existingApi.setLifecycleState(PUBLISHED);
        api.setApiLifecycleState(ApiLifecycleState.UNPUBLISHED);
        final ApiEntity apiEntity = apiService.update(API_ID, existingApi);
        assertNotNull(apiEntity);
        assertEquals(UNPUBLISHED, apiEntity.getLifecycleState());
    }

    @Test
    public void shouldNotChangeLifecycleStateFromUnpublishedToCreated() throws TechnicalException {
        prepareUpdate();
        assertUpdate(ApiLifecycleState.UNPUBLISHED, CREATED, true);
        assertUpdate(ApiLifecycleState.UNPUBLISHED, PUBLISHED, false);
        assertUpdate(ApiLifecycleState.UNPUBLISHED, UNPUBLISHED, false);
        assertUpdate(ApiLifecycleState.UNPUBLISHED, ARCHIVED, false);
    }

    @Test
    public void shouldNotUpdateADeprecatedApi() throws TechnicalException {
        prepareUpdate();
        assertUpdate(ApiLifecycleState.DEPRECATED, CREATED, true);
        assertUpdate(ApiLifecycleState.DEPRECATED, PUBLISHED, true);
        assertUpdate(ApiLifecycleState.DEPRECATED, UNPUBLISHED, true);
        assertUpdate(ApiLifecycleState.DEPRECATED, ARCHIVED, true);
        assertUpdate(ApiLifecycleState.DEPRECATED, DEPRECATED, true);
    }

    @Test
    public void shouldNotChangeLifecycleStateFromArchived() throws TechnicalException {
        prepareUpdate();
        assertUpdate(ApiLifecycleState.ARCHIVED, CREATED, true);
        assertUpdate(ApiLifecycleState.ARCHIVED, PUBLISHED, true);
        assertUpdate(ApiLifecycleState.ARCHIVED, UNPUBLISHED, true);
        assertUpdate(ApiLifecycleState.ARCHIVED, DEPRECATED, true);
    }

    @Test
    public void shouldTraceReviewReject() throws TechnicalException {
        prepareReviewAuditTest();

        final ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setMessage("Test Review msg");
        apiService.rejectReview(API_ID, USER_NAME, reviewEntity);

        verify(auditService).createApiAuditLog(argThat(apiId -> apiId.equals(API_ID)), anyMap(), argThat(evt -> Workflow.AuditEvent.API_REVIEW_REJECTED.equals(evt)), any(), any(), any());
    }


    @Test
    public void shouldTraceReviewAsked() throws TechnicalException {
        prepareReviewAuditTest();

        final ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setMessage("Test Review msg");
        apiService.askForReview(API_ID, USER_NAME, reviewEntity);
        verify(auditService).createApiAuditLog(argThat(apiId -> apiId.equals(API_ID)), anyMap(), argThat(evt -> Workflow.AuditEvent.API_REVIEW_ASKED.equals(evt)), any(), any(), any());
    }

    @Test
    public void shouldTraceReviewAccepted() throws TechnicalException {
        prepareReviewAuditTest();

        final ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setMessage("Test Review msg");
        apiService.acceptReview(API_ID, USER_NAME, reviewEntity);
        verify(auditService).createApiAuditLog(argThat(apiId -> apiId.equals(API_ID)), anyMap(), argThat(evt -> Workflow.AuditEvent.API_REVIEW_ACCEPTED.equals(evt)), any(), any(), any());
    }

    private void prepareReviewAuditTest() throws TechnicalException {
        api.setDefinition(API_DEFINITION);
        when(apiRepository.findById(API_ID)).thenReturn(Optional.of(api));

        final MembershipEntity membership = new MembershipEntity();
        membership.setMemberId(USER_NAME);
        when(membershipService.getPrimaryOwner(
                MembershipReferenceType.API,
                API_ID)).thenReturn(membership);

        when(userService.findById(USER_NAME)).thenReturn(mock(UserEntity.class));

        final Workflow workflow = new Workflow();
        workflow.setState(WorkflowState.REQUEST_FOR_CHANGES.name());
        when(workflowService.create(any(), any(), any(), any(), any(), any())).thenReturn(workflow);
    }

    @Test
    public void shouldNotChangeLifecycleStateFromCreatedInReview() throws TechnicalException {
        prepareUpdate();
        when(parameterService.findAsBoolean(Key.API_REVIEW_ENABLED)).thenReturn(true);
        final Workflow workflow = new Workflow();
        workflow.setState("IN_REVIEW");
        when(workflowService.findByReferenceAndType(API, API_ID, REVIEW)).thenReturn(singletonList(workflow));

        assertUpdate(ApiLifecycleState.CREATED, CREATED, false);
        assertUpdate(ApiLifecycleState.CREATED, PUBLISHED, true);
        assertUpdate(ApiLifecycleState.CREATED, UNPUBLISHED, true);
        assertUpdate(ApiLifecycleState.CREATED, DEPRECATED, true);
    }

    @Test(expected = AllowOriginNotAllowedException.class)
    public void shouldHaveAllowOriginNotAllowed() throws TechnicalException {
        prepareUpdate();
        existingApi.getProxy().getCors().getAccessControlAllowOrigin().add("/test^");
        apiService.update(API_ID, existingApi);
    }

    private void assertUpdate(final ApiLifecycleState fromLifecycleState,
                              final io.gravitee.rest.api.model.api.ApiLifecycleState lifecycleState, final boolean shouldFail) {
        api.setApiLifecycleState(fromLifecycleState);
        existingApi.setLifecycleState(lifecycleState);
        boolean failed = false;
        try {
            apiService.update(API_ID, existingApi);
        } catch (final LifecycleStateChangeNotAllowedException ise) {
            failed = true;
        }
        if (!failed && shouldFail) {
            fail("Should not be possible to change the lifecycle state of a " + fromLifecycleState + " API to " + lifecycleState);
        }
    }
}
