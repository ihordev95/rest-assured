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

package com.jayway.restassured.internal.support

import com.jayway.restassured.internal.RestAssuredResponseImpl
import com.jayway.restassured.parsing.Parser
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.FilterableRequestSpecification
import groovy.json.JsonOutput
import static org.apache.commons.lang3.StringUtils.isBlank

class Prettifier {

  def String getPrettifiedBodyIfPossible(FilterableRequestSpecification request) {
    def body = request.getBody();
    if(body == null) {
      return null
    } else if(!(body instanceof String)) {
      return body.toString()
    }
    def parser = Parser.fromContentType(request.getRequestContentType())
    prettyPrint(body as String, parser)
  }

  def String getPrettifiedBodyIfPossible(Response response) {
    def contentType = response.getContentType()
    def responseAsString = response.asString()
    if(isBlank(contentType) || !response instanceof RestAssuredResponseImpl) {
      return responseAsString
    }

    RestAssuredResponseImpl responseImpl = response as RestAssuredResponseImpl;
    def rpr = responseImpl.getRpr();
    def parser = rpr.getParser(contentType)
    prettyPrint(responseAsString, parser);
  }

  def String prettyPrint(String body, Parser parser) {
    def String prettifiedBody;
    switch (parser) {
      case Parser.JSON:
        prettifiedBody = JsonOutput.prettyPrint(body)
        break
      case Parser.XML:
        prettifiedBody = prettyPrintWithXmlParser(new XmlParser(), body)
        break
      case Parser.HTML:
        prettifiedBody = prettyPrintWithXmlParser(new XmlParser(new org.ccil.cowan.tagsoup.Parser()), body)
        break
      default:
        prettifiedBody = body
        break
    }
    return prettifiedBody
  }

  private String prettyPrintWithXmlParser(XmlParser xmlParser, responseAsString) {
    def stringWriter = new StringWriter()
    def node = xmlParser.parseText(responseAsString);
    new XmlNodePrinter(new PrintWriter(stringWriter)).print(node)
    def body = stringWriter.toString()
    if(body.endsWith("\n")) {
      body = body.substring(0, body.length()-1)
    }
    body
  }
}