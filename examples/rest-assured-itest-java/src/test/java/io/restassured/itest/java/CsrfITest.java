/*
 * Copyright 2019 the original author or authors.
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

package io.restassured.itest.java;

import io.restassured.RestAssured;
import io.restassured.config.CsrfConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.itest.java.support.WithJetty;
import org.apache.commons.io.output.WriterOutputStream;
import org.junit.Test;

import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.*;
import static io.restassured.config.CsrfConfig.CsrfPrioritization.FORM;
import static io.restassured.config.CsrfConfig.CsrfPrioritization.HEADER;
import static io.restassured.config.CsrfConfig.csrfConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CsrfITest extends WithJetty {

    @Test
    public void csrfConfigWithoutCsrfTokenPathDisablesCsrf() {
        given().
                config(config().csrfConfig(csrfConfig())).
        when().
                post("/loginPageWithCsrf").
        then().
                statusCode(403);
    }

    @Test
    public void csrfWithInvalidTokenPathAndAutoDetectionOfCsrfInputFieldName() {
        try {
            given().
                    csrf("/invalid").
            when().
                    post("/loginPageWithCsrf").
            then().
                    statusCode(200);
            fail("Expecting IllegalArgumentException");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertTrue(e.getMessage().contains("Not found"));
        }
    }

    @Test
    public void csrfWithInvalidTokenPathAndSpecificCsrfInputFieldName() {
        try {
            given().
                    csrf("/invalid", "_csrf").
            when().
                    post("/loginPageWithCsrf").
            then().
                    statusCode(200);
            fail("Expecting IllegalArgumentException");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertTrue(e.getMessage(), e.getMessage().contains("Couldn't find a the CSRF token in response. Expecting either an input field with name \"_csrf\" or a meta tag with name \"_csrf_header\". Response was:"));
        }
    }

    @Test
    public void csrfDslWithSpecificCsrfInputFieldName() {
        given().
                csrf("/loginPageWithCsrf", "_csrf").
        when().
                post("/loginPageWithCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void csrfAutoCsrfDetectionDefinedInRequestConfig() {
        given().
                config(config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf").loggingEnabled(LogDetail.BODY))).
        when().
                post("/loginPageWithCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void csrfConfigUsesAutoCsrfDetectionDefinedByDefaultWhenUsingConstructorToCreateCsrfConfig() {
        given().
                config(config().csrfConfig(new CsrfConfig("/loginPageWithCsrf"))).
        when().
                post("/loginPageWithCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void csrfConfigUsesAutoCsrfDetectionDefinedByDefaultWhenUsingStaticMethodToCreateCsrfConfig() {
        given().
                config(config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf"))).
        when().
                post("/loginPageWithCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void csrfConfigWithLogging() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);

        given().
                config(config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf").and().loggingEnabled(new LogConfig(captor, true)))).
        when().
                post("/loginPageWithCsrf").
        then().
                statusCode(200);

        assertThat(writer.toString(), startsWith(String.format("Request method:\tGET%n" +
                        "Request URI:\thttp://localhost:8080/loginPageWithCsrf%n" +
                        "Proxy:\t\t\t<none>%n" +
                        "Request params:\t<none>%n" +
                        "Query params:\t<none>%n" +
                        "Form params:\t<none>%n" +
                        "Path params:\t<none>%n" +
                        "Headers:\t\tAccept=*/*%n" +
                        "Cookies:\t\t<none>%n" +
                        "Multiparts:\t\t<none>%n" +
                        "Body:\t\t\t<none>%n" +
                        "HTTP/1.1 200 OK%n" +
                        "Content-Type: text/html;charset=utf-8%n" +
                        "Content-Length: 589%n" +
                        "Server: Jetty(9.4.34.v20201102)%n%n")));
    }

    @Test
    public void csrfIsNotUsedWhenTokenPathIsUndefined() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);

        given().
                config(config().csrfConfig(csrfConfig().with().loggingEnabled(new LogConfig(captor, true)))).
        when().
                post("/loginPageWithCsrf").
        then().
                statusCode(403);

        assertThat(writer.toString(), emptyString());
    }

    @Test
    public void csrfIsNotUsedForGetRequests() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);

        given().
                config(config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf").loggingEnabled(new LogConfig(captor, true)))).
        when().
                get("/loginPageWithCsrf").
        then().
                statusCode(200);

        assertThat(writer.toString(), emptyString());
    }

    @Test
    public void csrfIsNotUsedForHeadRequests() {
        final StringWriter writer = new StringWriter();
        final PrintStream captor = new PrintStream(new WriterOutputStream(writer, StandardCharsets.UTF_8), true);

        given().
                config(config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf").loggingEnabled(new LogConfig(captor, true)))).
        when().
                head("/loginPageWithCsrf").
        then().
                statusCode(200);

        assertThat(writer.toString(), emptyString());
    }

    @Test
    public void csrfAutoCsrfDetectionDefinedInStatically() {
        RestAssured.config = RestAssuredConfig.config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf"));

        try {
            when().
                    post("/loginPageWithCsrf").
            then().
                    statusCode(200);
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void csrfRequestConfigIsOverridingStaticConfig() {
        RestAssured.config = RestAssuredConfig.config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf2"));

        try {
            given().
                    config(config().csrfConfig(csrfConfig().with().csrfTokenPath("/loginPageWithCsrf"))).
            when().
                    post("/loginPageWithCsrf").
            then().
                    statusCode(200);
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void csrfDslIsMergedWithStaticConfigWhenSpecifyingDifferentThingInDslAndStaticConfig() {
        // TODO FIx test
        RestAssured.config = RestAssuredConfig.config().csrfConfig(csrfConfig().with().csrfInputFieldName("___csrf"));

        try {
            given().
                    queryParam("csrfInputFieldName", "___csrf").
                    csrf("/loginPageWithCsrfUnderscore").
            when().
                    post("/loginPageWithCsrf").
            then().
                    statusCode(200);
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void csrfDslIsOverridingWithStaticConfigWhenSpecifyingSamePropertyInDslAndStaticConfig() {
        RestAssured.config = RestAssuredConfig.config().csrfConfig(csrfConfig().with().csrfTokenPath("/error"));

        try {
            given().
                    csrf("/loginPageWithCsrf").
            when().
                    post("/loginPageWithCsrf").
            then().
                    statusCode(200);
        } finally {
            RestAssured.reset();
        }
    }

    @Test
    public void csrfHeader() {
        given().
                csrf("/pageWithDefaultHeaderCsrf").
        when().
                post("/pageThatRequireHeaderCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void csrfHeaderDerivedFromSpecifiedMetaTag() {
        given().
                config(RestAssuredConfig.config().csrfConfig(csrfConfig().csrfMetaTagName("___csrf_header"))).
                csrf("/pageWithCustomHeaderCsrf").
        when().
                post("/pageThatRequireHeaderCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void csrfHeaderWithCustomizedHeaderName() {
        given().
                config(RestAssuredConfig.config().csrfConfig(csrfConfig().csrfHeaderName("MyHeader"))).
                csrf("/pageWithDefaultHeaderCsrf").
                queryParam("headerName", "MyHeader").
        when().
                post("/pageThatRequireHeaderCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void prioritizeHeaderCsrfOverFormByDefaultWhenPageContainsBoth() {
        given().
                csrf("/pageWithHeaderAndFormCsrf").
                queryParam("expectedType", "HEADER").
        when().
                post("/pageThatRequireFormOrHeaderCsrf").
        then().
                statusCode(200);
    }

    @Test
    public void prioritizeHeaderCsrfOverFormWhenHeaderPrioritizationIsUsedWhenPageContainsBothCsrfHeaderAndInputField() {
        given().
                config(RestAssuredConfig.config().csrfConfig(csrfConfig().csrfPrioritization(HEADER))).
                csrf("/pageWithHeaderAndFormCsrf").
                queryParam("expectedType", "HEADER").
        when().
                post("/pageThatRequireFormOrHeaderCsrf").
        then().
                statusCode(200);
    }
    @Test
    public void prioritizeFormCsrfOverHeaderWhenFormPrioritizationIsUsedWhenPageContainsBothCsrfHeaderAndInputField() {
        given().
                config(RestAssuredConfig.config().csrfConfig(csrfConfig().csrfPrioritization(FORM))).
                csrf("/pageWithHeaderAndFormCsrf").
                queryParam("expectedType", "FORM").
        when().
                post("/pageThatRequireFormOrHeaderCsrf").
        then().
                statusCode(200);
    }
}