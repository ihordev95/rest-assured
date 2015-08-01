/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jayway.restassured.specification;

/**
 * Specify a preemptive authentication scheme to use when sending a request.
 */
public interface PreemptiveAuthSpec {
    /**
     * Use preemptive http basic authentication. This means that the authentication details are sent in the request
     * header regardless if the server has challenged for authentication or not.
     *
     * @param username The username.
     * @param password The password.
     *
     * @return The Request specification
     */
    RequestSpecification basic(String username, String password);
}
