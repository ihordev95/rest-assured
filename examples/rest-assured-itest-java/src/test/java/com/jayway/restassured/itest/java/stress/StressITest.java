/*
 * Copyright 2012 the original author or authors.
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

package com.jayway.restassured.itest.java.stress;

import com.jayway.restassured.RestAssured;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.data.Protocol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class StressITest {
    Component component;
    static final int wait = 60 * 1000;
    int iterations = 30;
    String post = "TEST";
    String expect = "HANG-TEST";
    String url = null;

    @Before
    public void setUp() throws Exception {
        url = "http://localhost:8081/restlet/test";
        component = new Component();
        component.getLogService().setEnabled( true );
        component.getServers().add( Protocol.HTTP, 8081 );
        component.getDefaultHost().attach( "/restlet", new StressApp() );
        component.start();
    }

    @After
    public void tearDown() throws Exception {
        component.stop();
        RestAssured.reset();
    }

    @Test(timeout=wait)
    public void stressWithRestAssuredGet() throws UnsupportedEncodingException {
        for( int i=0, n=iterations; i<n; i++ ) {
            given().
                    expect().body( equalTo( expect ) ).
                    when().get( url );
        }
    }

    @Test(timeout=wait)
    public void stressWithRestAssuredPost() throws UnsupportedEncodingException {
        for( int i=0, n=iterations; i<n; i++ ) {
            given().contentType("text/plain; charset=UTF-8").body( post.getBytes( "UTF-8" ) ).
                    expect().body( equalTo( expect ) ).
                    when().post( url );
        }
    }

    @Test(timeout=wait)
    @Ignore("Not working since upgrade of HTTP Client, is it a bug?")
    public void stressWithRestAssuredGetManualClose() throws IOException, InterruptedException {
        for( int i=0, n=iterations; i<n; i++ ) {
            String body = IOUtils.toString(get(url).andReturn().body().asInputStream());
            assertEquals(expect, body);
        }
    }
}
