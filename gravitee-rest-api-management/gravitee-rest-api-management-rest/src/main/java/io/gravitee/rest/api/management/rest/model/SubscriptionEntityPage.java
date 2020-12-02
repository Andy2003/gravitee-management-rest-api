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
package io.gravitee.rest.api.management.rest.model;

import io.gravitee.rest.api.model.SubscriptionEntity;

public class SubscriptionEntityPage extends PagedResult<SubscriptionEntity> {

    public SubscriptionEntityPage(io.gravitee.common.data.domain.Page<SubscriptionEntity> page, int perPage) {
        super(page, perPage);
    }
}
