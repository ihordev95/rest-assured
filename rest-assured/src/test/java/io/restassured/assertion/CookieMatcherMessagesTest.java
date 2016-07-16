package io.restassured.assertion;

import org.codehaus.groovy.runtime.GStringImpl;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Maciej Gawinecki
 */
public class CookieMatcherMessagesTest {

    String[] cookies = new String[]{"DEVICE_ID=123; Domain=.test.com; Expires=Thu, 12-Oct-2023 09:34:31 GMT; Path=/; Secure; HttpOnly;"};

    @Test
    public void shouldPrintValidErrorMessageForStandardMatchers() {

        CookieMatcher cookieMatcher = new CookieMatcher();
        cookieMatcher.setCookieName("DEVICE_ID");
        cookieMatcher.setMatcher(Matchers.containsString("X"));

        Map<String, Object> result = (Map<String, Object>) cookieMatcher.validateCookie(Arrays.asList(cookies));
        assertThat((Boolean)result.get("success"), equalTo(false));
        assertThat(((GStringImpl)result.get("errorMessage")).toString(), equalTo("Expected cookie \"DEVICE_ID\" was a string containing \"X\", was \"123\"\".\n"));
    }

    @Test
    public void shouldPrintValidErrorMessageForCustomMatcher() {

        CookieMatcher cookieMatcher = new CookieMatcher();
        cookieMatcher.setCookieName("DEVICE_ID");
        cookieMatcher.setMatcher(new ContainsXMatcher());

        Map<String, Object> result = (Map<String, Object>) cookieMatcher.validateCookie(Arrays.asList(cookies));
        assertThat((Boolean)result.get("success"), equalTo(false));
        assertThat(((GStringImpl)result.get("errorMessage")).toString(), equalTo("Expected cookie \"DEVICE_ID\" was containing 'X', was \"123\" not containing 'X'\".\n"));
    }


    static class ContainsXMatcher extends TypeSafeDiagnosingMatcher<String> {

        @Override
        protected boolean matchesSafely(String actual, Description mismatchDescription) {

            // Not this method will be called twice due to https://github.com/hamcrest/JavaHamcrest/issues/144

            if (actual.contains("X")) {
                return true;
            }

            mismatchDescription.appendValue(actual).appendText(" not containing 'X'");
            return false;
        }

        public void describeTo(Description description) {
            description.appendText("containing 'X'");
        }
    }

}
