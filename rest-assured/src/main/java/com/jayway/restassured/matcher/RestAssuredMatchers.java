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

package com.jayway.restassured.matcher;

import com.jayway.restassured.internal.matcher.xml.XmlDtdMatcher;
import com.jayway.restassured.internal.matcher.xml.XmlXsdMatcher;
import com.jayway.restassured.response.Response;
import org.hamcrest.Matcher;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import static org.hamcrest.Matchers.*;

/**
 * Providers Hamcrest matchers that may be useful when validating a response.
 */
public class RestAssuredMatchers {

    /**
     * Evaluates to true if an XML string matches the supplied XSD (Xml Schema).
     *
     * @param xsd The XSD to match
     * @return The XSD matcher
     */
    public static XmlXsdMatcher matchesXsd(String xsd) {
        return XmlXsdMatcher.matchesXsd(xsd);
    }

    /**
     * Evaluates to true if an XML string matches the supplied XSD (Xml Schema).
     *
     * @param xsd The XSD to match
     * @return The XSD matcher
     */
    public static XmlXsdMatcher matchesXsd(InputStream xsd) {
        return XmlXsdMatcher.matchesXsd(xsd);
    }

    /**
     * Evaluates to true if an XML string matches the supplied XSD (Xml Schema).
     *
     * @param xsd The XSD to match
     * @return The XSD matcher
     */
    public static XmlXsdMatcher matchesXsd(Reader xsd) {
        return XmlXsdMatcher.matchesXsd(xsd);
    }

    /**
     * Evaluates to true if an XML string matches the supplied XSD (Xml Schema).
     *
     * @param xsd The XSD to match
     * @return The XSD matcher
     */
    public static XmlXsdMatcher matchesXsd(File xsd) {
        return XmlXsdMatcher.matchesXsd(xsd);
    }

    /**
     * Evaluates to true if an XML string matches the supplied DTD.
     *
     * @param dtd The DTD to match
     * @return The DTD matcher
     */
    public static Matcher<String> matchesDtd(String dtd) {
        return XmlDtdMatcher.matchesDtd(dtd);
    }

    /**
     * Evaluates to true if an XML string matches the supplied DTD.
     *
     * @param dtd The DTD to match
     * @return The DTD matcher
     */
    public static Matcher<String> matchesDtd(InputStream dtd) {
        return XmlDtdMatcher.matchesDtd(dtd);
    }

    /**
     * Evaluates to true if an XML string matches the supplied DTD.
     *
     * @param dtd The DTD to match
     * @return The DTD matcher
     */
    public static Matcher<String> matchesDtd(File dtd) {
        return XmlDtdMatcher.matchesDtd(dtd);
    }

    /**
     * Evaluates to true if an XML string matches the supplied DTD.
     *
     * @param url The DTD to match
     * @return The DTD matcher
     */
    public static Matcher<String> matchesDtd(URL url) {
        return XmlDtdMatcher.matchesDtd(url);
    }

    /**
     * Creates a {@link com.jayway.restassured.matcher.ResponseAwareMatcher} that extracts the given path from the response and
     * wraps it in a {@link org.hamcrest.Matchers#equalTo(Object)} matcher. This is useful if you have a resource that e.g. returns the given JSON:
     * <pre>
     * {
     *      "userId" : "my-id",
     *      "playerId" : "my-id"
     * }
     * </pre>
     * you can then test it like this:
     * <pre>
     * get("/x").then().body("userId", equalToPath("playerId"));
     * </pre>
     *
     * @param path The path to check
     * @return A {@link com.jayway.restassured.matcher.ResponseAwareMatcher}
     */
    public static ResponseAwareMatcher<Response> equalToPath(final String path) {
        return new ResponseAwareMatcher<Response>() {
            public Matcher<?> matcher(Response response) {
                return equalTo(response.path(path));
            }
        };
    }

    /**
     * Creates a {@link com.jayway.restassured.matcher.ResponseAwareMatcher} that extracts the given path from the response and
     * wraps it in a {@link org.hamcrest.Matchers#equalTo(Object)} matcher. This is useful if you have a resource that e.g. returns the given JSON:
     * <pre>
     * {
     *      "userId" : "my-id",
     *      "href" : "http://localhost:8080/my-id"
     * }
     * </pre>
     * you can then test it like this:
     * <pre>
     * get("/x").then().body("href", endsWithPath("userId"));
     * </pre>
     *
     * @param path The path to check
     * @return A {@link com.jayway.restassured.matcher.ResponseAwareMatcher}
     */
    public static ResponseAwareMatcher<Response> endsWithPath(final String path) {
        return new ResponseAwareMatcher<Response>() {
            public Matcher<?> matcher(Response response) {
                return endsWith(response.<String>path(path));
            }
        };
    }

    /**
     * Creates a {@link com.jayway.restassured.matcher.ResponseAwareMatcher} that extracts the given path from the response and
     * wraps it in a {@link org.hamcrest.Matchers#equalTo(Object)} matcher. This is useful if you have a resource that e.g. returns the given JSON:
     * <pre>
     * {
     *      "userId" : "my-id",
     *      "baseUri" : "http://localhost:8080",
     *      "href" : "http://localhost:8080/my-id"
     * }
     * </pre>
     * you can then test it like this:
     * <pre>
     * get("/x").then().body("href", startsWithPath("baseUri"));
     * </pre>
     *
     * @param path The path to check
     * @return A {@link com.jayway.restassured.matcher.ResponseAwareMatcher}
     */
    public static ResponseAwareMatcher<Response> startsWithPath(final String path) {
        return new ResponseAwareMatcher<Response>() {
            public Matcher<?> matcher(Response response) {
                return startsWith(response.<String>path(path));
            }
        };
    }

    /**
     * Creates a {@link com.jayway.restassured.matcher.ResponseAwareMatcher} that extracts the given path from the response and
     * wraps it in a {@link org.hamcrest.Matchers#equalTo(Object)} matcher. This is useful if you have a resource that e.g. returns the given JSON:
     * <pre>
     * {
     *      "userId" : "my-id",
     *      "href" : "http://localhost:8080/my-id"
     * }
     * </pre>
     * you can then test it like this:
     * <pre>
     * get("/x").then().body("href", containsPath("userId"));
     * </pre>
     *
     * @param path The path to check
     * @return A {@link com.jayway.restassured.matcher.ResponseAwareMatcher}
     */
    public static ResponseAwareMatcher<Response> containsPath(final String path) {
        return new ResponseAwareMatcher<Response>() {
            public Matcher<?> matcher(Response response) {
                return containsString(response.<String>path(path));
            }
        };
    }

}