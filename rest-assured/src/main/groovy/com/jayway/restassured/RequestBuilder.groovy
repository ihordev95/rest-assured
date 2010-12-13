package com.jayway.restassured

import com.jayway.restassured.assertion.Assertion
import com.jayway.restassured.assertion.JSONAssertion
import com.jayway.restassured.assertion.XMLAssertion
import com.jayway.restassured.exception.AssertionFailedException
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import groovyx.net.http.Method
import javax.xml.parsers.DocumentBuilderFactory
import org.hamcrest.Matcher
import org.hamcrest.xml.HasXPath
import org.w3c.dom.Element
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static org.hamcrest.Matchers.equalTo

class RequestBuilder {

  String baseUri
  String path
  int port
  Matcher<Integer> expectedStatusCode;
  Matcher<String> expectedStatusLine;
  Method method
  Map query
  HamcrestAssertionClosure assertionClosure;

  private Assertion assertion;

  def RequestBuilder content(String key, Matcher<?> matcher) {
    return new RequestBuilder(baseUri: RestAssured.baseURI, path: path, port: port, method: method, query: query, assertionClosure: new HamcrestAssertionClosure(key, matcher), expectedStatusCode : expectedStatusCode, expectedStatusLine: expectedStatusLine)
  }
  def RequestBuilder statusCode(Matcher<Integer> expectedStatusCode) {
    return new RequestBuilder(baseUri: RestAssured.baseURI, path: path, port: port, method: method, query: query, assertionClosure: assertionClosure, expectedStatusCode : expectedStatusCode, expectedStatusLine: expectedStatusLine)
  }

  def RequestBuilder statusCode(int expectedStatusCode) {
    return statusCode(equalTo(expectedStatusCode));
  }

  def RequestBuilder statusLine(Matcher<String> expectedStatusLine) {
    return new RequestBuilder(baseUri: RestAssured.baseURI, path: path, port: port, method: method, query: query, assertionClosure: assertionClosure, expectedStatusCode : expectedStatusCode, expectedStatusLine: expectedStatusLine)
  }

  def RequestBuilder statusLine(String expectedStatusLine) {
    return statusLine(equalTo(expectedStatusLine))
  }

  def RequestBuilder body(Matcher<?> matcher) {
    return content(null, matcher);
  }

  def RequestBuilder body(String key, Matcher<?> matcher) {
    return content(key, matcher);
  }

  def RequestBuilder response() {
    return this;
  }

  def RequestBuilder when() {
    return this;
  }

  def get(String path) {
    sendRequest(path, method, query, assertionClosure);
  }

  def then(Closure assertionClosure) {
    sendRequest(path, method, query, new GroovyAssertionClosure(assertionClosure));
  }

  def RequestBuilder parameters(Object...parameters) {
    if(parameters == null || parameters.length < 2) {
      throw new IllegalArgumentException("You must supply at least one key and one value.");
    } else if(parameters.length % 2 != 0) {
      throw new IllegalArgumentException("You must supply the same number of keys as values.")
    }

    Map<String, Object> map = new HashMap<String,Object>();
    for (int i = 0; i < parameters.length; i+=2) {
      map.put(parameters[i], parameters[i+1]);
    }
    return this.parameters(map)
  }

  def RequestBuilder parameters(Map<String, Object> map) {
    return new RequestBuilder(baseUri: RestAssured.baseURI, path: path, port: port, method: method, query: map, assertionClosure: assertionClosure, expectedStatusCode: expectedStatusCode, expectedStatusLine: expectedStatusLine)
  }

  def RequestBuilder and() {
    return this;
  }

  def RequestBuilder with() {
    return this;
  }

  def RequestBuilder then() {
    return this;
  }

  def RequestBuilder expect() {
    return this;
  }

  def RequestBuilder port(int port) {
    return new RequestBuilder(baseUri: RestAssured.baseURI, path: path, port: port, method: method, query: query, assertionClosure: assertionClosure, expectedStatusCode : expectedStatusCode, expectedStatusLine: expectedStatusLine)
  }

  private def sendRequest(path, method, query, assertionClosure) {
    if(port <= 0) {
      throw new IllegalArgumentException("Port must be greater than 0")
    }
    def url = baseUri.startsWith("http://") ? baseUri : "http://"+baseUri
    def http = new HTTPBuilder("$url:$port")
    if(POST.equals(method)) {
      try {
        http.post( path: path, body: query,
                requestContentType: URLENC ) { response, content ->
          if(assertionClosure != null) {
            assertionClosure.call (response, content)
          }
        }
      } catch(HttpResponseException e) {
        if(assertionClosure != null) {
          assertionClosure.call(e.getResponse())
        } else {
          throw e;
        }
      }
    } else if(GET.equals(method)) {
      def contentType = assertionClosure.isRawBodyMatcher() ? TEXT : ANY
      http.request(method, contentType) {
        uri.path = path
        if(query != null) {
          uri.query = query
        }

        Closure closure = assertionClosure.getClosure()
        // response handler for a success response code:
        response.success = closure

        // handler for any failure status code:
        response.failure = closure
      }
    } else {
      throw new IllegalArgumentException("Only GET and POST supported")
    }
  }

  class GroovyAssertionClosure {

    Closure closure;

    GroovyAssertionClosure(Closure closure) {
      this.closure = closure
    }

    boolean isRawBodyMatcher() {
      return false;
    }

    def call(response, content) {
      return getClosure().call(response, content)
    }

    def call(response) {
      return getClosure().call(response)
    }

    def getClosure() {
      closure
    }
  }

  class HamcrestAssertionClosure {
    private Matcher matcher;
    private String key;

    HamcrestAssertionClosure(String key, Matcher matcher) {
      this.key = key
      this.matcher = matcher
    }

    def call(response, content) {
      return getClosure().call(response, content)
    }

    def call(response) {
      return getClosure().call(response)
    }

    boolean isXPathMatcher() {
      matcher instanceof HasXPath
    }

    boolean isRawBodyMatcher() {
      isXPathMatcher() || key == null
    }

    def getClosure() {
      return { response, content ->
        def headers = response.headers
        if(expectedStatusCode != null) {
          def actualStatusCode = response.statusLine.statusCode
          if(!expectedStatusCode.matches(actualStatusCode)) {
            throw new AssertionFailedException(String.format("Expected status code %s doesn't match actual status code <%s>.", expectedStatusCode.toString(), actualStatusCode));
          }
        }

        if(expectedStatusLine != null) {
          def actualStatusLine = response.statusLine.toString()
          if(!expectedStatusLine.matches(actualStatusLine)) {
            throw new AssertionFailedException(String.format("Expected status line %s doesn't match actual status line \"%s\".", expectedStatusLine.toString(), actualStatusLine));
          }
        }
        def result
        if(key == null) {
          if(isXPathMatcher()) {
            result = content.readLines().join().toString()
            Element node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(new String(result).getBytes())).getDocumentElement();
            if (matcher.matches(node) == false) {
              throw new AssertionFailedException(String.format("Body doesn't match.\nExpected:\n%s\nActual:\n%s", matcher.toString(), result))
            }
          } else {
            if(content instanceof InputStreamReader) {
              result = content.readLines().join()
            } else {
              result = content.toString()
            }
            if (!matcher.matches(result)) {
              throw new AssertionFailedException(String.format("Body doesn't match.\nExpected:\n%s\nActual:\n%s", matcher.toString(), result))
            }
          }
        }  else {
          switch (response.contentType.toString().toLowerCase()) {
            case JSON.toString().toLowerCase():
              assertion = new JSONAssertion(key: key)
              break
            case XML.toString().toLowerCase():
              assertion = new XMLAssertion(key: key)
              break;
          }
          result = assertion.getResult(content)
          if (!matcher.matches(result)) {
            throw new AssertionFailedException(String.format("%s %s doesn't match %s, was <%s>.", assertion.description(), key, matcher.toString(), result))
          }
        }
      }
    }
  }
}
