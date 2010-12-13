package com.jayway.restassured.itest

import com.jayway.restassured.itest.support.WithJetty
import org.junit.Ignore
import org.junit.Test
import static com.jayway.restassured.RestAssured.get
import static groovy.util.GroovyTestCase.assertEquals
import static org.hamcrest.Matchers.equalTo
import org.junit.BeforeClass

class GetITest extends WithJetty  {

  @Test
  public void withoutParameters() throws Exception {
    get ("/hello").then {response, json -> assertEquals "Hello Scalatra", json.hello }
  }

  @Test
  public void getWithPathParams() throws Exception {
    get ("/John/Doe"). then { response, json -> assertEquals "John", json.firstName }
  }

  @Test
  public void ognlAndPlainGroovy() throws Exception {
    get ("/lotto").then {response, json -> assertEquals 5, json.lotto.lottoId }
  }

  @Test
  public void ognlReturningArrayAndPlainGroovy() throws Exception {
    get ("/lotto").then {response, json -> assertEquals([23, 54], json.lotto.winners.winnerId)}
  }

  @Test
  public void getWithQueryParameters() throws Exception {
    get ("/greet").with().parameters([firstName: "John", lastName: "Doe"]).and().then { response, json -> assertEquals "Greetings John Doe", json.greeting }
  }
}
