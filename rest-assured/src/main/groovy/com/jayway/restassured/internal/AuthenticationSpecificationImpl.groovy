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
package com.jayway.restassured.internal

import com.jayway.restassured.authentication.*
import com.jayway.restassured.specification.AuthenticationSpecification
import com.jayway.restassured.specification.PreemptiveAuthSpec
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.spi.AuthFilter
import com.jayway.restassured.spi.Signature;

import java.security.KeyStore

import static com.jayway.restassured.internal.assertion.AssertParameter.notNull

/**
 * Specify an authentication scheme to use when sending a request.
 */
class AuthenticationSpecificationImpl implements AuthenticationSpecification {
    private RequestSpecification requestSpecification;

    AuthenticationSpecificationImpl(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
    }

    /**
     * Use http basic authentication.
     *
     * @param userName The user name.
     * @param password The password.
     * @return The request builder
     */
    def RequestSpecification basic(String userName, String password) {
        notNull userName, "userName"
        notNull password, "password"

        requestSpecification.authenticationScheme = new BasicAuthScheme(userName: userName, password: password)
        return requestSpecification
    }

    /**
     * Use http digest authentication.
     *
     * @param userName The user name.
     * @param password The password.
     * @return The request builder
     */
    def RequestSpecification digest(String userName, String password) {
        notNull userName, "userName"
        notNull password, "password"

        requestSpecification.authenticationScheme = new BasicAuthScheme(userName: userName, password: password)
        return requestSpecification
    }

    /**
     * Sets a certificate to be used for SSL authentication. See {@link java.lang.Class#getResource(String)}
     * for how to get a URL from a resource on the classpath.
     * <p>
     *     Uses keystore: <code>KeyStore.getDefaultType()</code>.
     * </p>
     *
     * @param certURL URL to a JKS keystore where the certificate is stored.
     * @param password password to decrypt the keystore
     * @return The request com.jayway.restassured.specification
     * @see #certificate(java.lang.String, java.lang.String, java.lang.String, int, com.jayway.restassured.authentication.KeystoreProvider)
     */
    def RequestSpecification certificate(String certURL, String password) {
        return certificate(certURL, password, KeyStore.getDefaultType(), 443)
    }

    def RequestSpecification certificate(String certURL, String password, String certType, int port) {
        return certificate(certURL, password, certType, port, new NoKeystoreSpecImpl())
    }

    def RequestSpecification certificate(String certURL, String password, String certType, int port, KeystoreProvider trustStoreProvider) {
        notNull certURL, "certURL"
        notNull password, "password"
        notNull certType, "Certification type"

        requestSpecification.authenticationScheme = new CertAuthScheme(certURL: certURL, password: password, certType: certType,
                port: port, trustStoreProvider: trustStoreProvider)
        return requestSpecification
    }
/**
 * Excerpt from the HttpBuilder docs:<br>
 * OAuth sign the request. Note that this currently does not wait for a WWW-Authenticate challenge before sending the the OAuth header.
 * All requests to all domains will be signed for this instance.
 * This assumes you've already generated an accessToken and secretToken for the site you're targeting.
 * For More information on how to achieve this, see the <a href="http://code.google.com/p/oauth-signpost/wiki/GettingStarted#Using_Signpost">Signpost documentation</a>.
 *
 * @param consumerKey
 * @param consumerSecret
 * @param accessToken
 * @param secretToken
 * @return The request com.jayway.restassured.specification
 */
    def RequestSpecification oauth(String consumerKey, String consumerSecret, String accessToken, String secretToken) {
        notNull consumerKey, "consumerKey"
        notNull consumerSecret, "consumerSecret"
        notNull accessToken, "accessToken"
        notNull secretToken, "secretToken"

        requestSpecification.authenticationScheme = new OAuthScheme(consumerKey: consumerKey, consumerSecret: consumerSecret, accessToken: accessToken, secretToken: secretToken)
        return requestSpecification
    }
	/**
	 * Excerpt from the HttpBuilder docs:<br>
	 * OAuth sign the request. Note that this currently does not wait for a WWW-Authenticate challenge before sending the the OAuth header.
	 * All requests to all domains will be signed for this instance.
	 * This assumes you've already generated an accessToken and secretToken for the site you're targeting.
	 * For More information on how to achieve this, see the <a href="http://code.google.com/p/oauth-signpost/wiki/GettingStarted#Using_Signpost">Signpost documentation</a>.
	 *
	 * @param consumerKey
	 * @param consumerSecret
	 * @param accessToken
	 * @param secretToken
	 * @param signature
	 * @return The request com.jayway.restassured.specification
	 */
		def RequestSpecification oauth(String consumerKey, String consumerSecret, String accessToken, String secretToken, Signature signature) {
			notNull consumerKey, "consumerKey"
			notNull consumerSecret, "consumerSecret"
			notNull accessToken, "accessToken"
			notNull secretToken, "secretToken"
			notNull signature, "signature"
	
			requestSpecification.authenticationScheme = new OAuthScheme(consumerKey: consumerKey, consumerSecret: consumerSecret, accessToken: accessToken, secretToken: secretToken, signature:signature)
			return requestSpecification
			}
		/**
		 * Excerpt from the HttpBuilder docs:<br>
		 * OAuth sign the request. Note that this currently does not wait for a WWW-Authenticate challenge before sending the the OAuth header.
		 * All requests to all domains will be signed for this instance.
		 * This assumes you've already generated an accessToken and secretToken for the site you're targeting.
		 * For More information on how to achieve this, see the <a href="http://code.google.com/p/oauth-signpost/wiki/GettingStarted#Using_Signpost">Signpost documentation</a>.
		 * @param accessToken
		 * @return The request com.jayway.restassured.specification
		 */
		def RequestSpecification oauth2(String accessToken) {
		notNull accessToken, "accessToken"

		requestSpecification.authenticationScheme = new OAuth2Scheme(accessToken: accessToken)
		return requestSpecification
		}
		/**
		 * Excerpt from the HttpBuilder docs:<br>
		 * OAuth sign the request. Note that this currently does not wait for a WWW-Authenticate challenge before sending the the OAuth header.
		 * All requests to all domains will be signed for this instance.
		 * This assumes you've already generated an accessToken and secretToken for the site you're targeting.
		 * For More information on how to achieve this, see the <a href="http://code.google.com/p/oauth-signpost/wiki/GettingStarted#Using_Signpost">Signpost documentation</a>.
		 * @param accessToken
		 * @return The request com.jayway.restassured.specification
		 */
		def RequestSpecification oauth2(String accessToken, Signature signature) {
		notNull accessToken, "accessToken"
	
		requestSpecification.authenticationScheme = new OAuth2Scheme(accessToken: accessToken, signature: signature)
		return requestSpecification
}
    def RequestSpecification none() {
        requestSpecification.authenticationScheme = new ExplicitNoAuthScheme();
        requestSpecification.filters.removeAll { it instanceof AuthFilter }
        return requestSpecification
    }

    def PreemptiveAuthSpec preemptive() {
        return new PreemptiveAuthSpecImpl(requestSpecification)
    }

    def RequestSpecification form(String userName, String password) {
        form(userName, password, null)
    }

    def RequestSpecification form(String userName, String password, FormAuthConfig config) {
        requestSpecification.authenticationScheme = new FormAuthScheme(userName: userName, password: password, config: config)
        return requestSpecification
    }
}
