package marker.rule;

import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.proxy.http.InterceptedRequest;
import marker.mock.MockBuilder;
import marker.mock.MockHttpRequest;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestRuleTest {

    private final MockHttpRequest mockRequest = MockBuilder.interceptedRequest()
            .listenerInterface("127.0.0.1:8080")
            .inScope(true)
            .httpService(MockBuilder.httpService()
                    .secure(true)
                    .host("example.com")
                    .build())
            .method("GET")
            .path("/burp/marker.txt")
            .url("https://example.com/burp/marker.txt")
            .fileExtension("txt")
            .httpParameter(MockBuilder.httpParameter()
                    .type(HttpParameterType.COOKIE)
                    .name("biscuit")
                    .value("jumbo-brownie")
                    .build())
            .httpParameter(MockBuilder.httpParameter()
                    .type(HttpParameterType.URL)
                    .name("format")
                    .value("json")
                    .build())
            .httpHeader(MockBuilder.httpHeader()
                    .name("user-agent")
                    .value("java")
                    .build())
            .body("burp-marker")
            .build();

    @Test
    void shouldMatchDomain() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.DOMAIN, Matchers.REGEX, "example.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.DOMAIN, Matchers.REGEX, ".+com", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.DOMAIN, Matchers.REGEX, "example.com", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.DOMAIN, Matchers.REGEX, "moc.elpmaxe", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchProtocol() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.IS_SECURE, Matchers.BOOLEAN_EQUALS, true, RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.IS_SECURE, Matchers.BOOLEAN_EQUALS, false, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchMethod() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.METHOD, Matchers.REGEX, "G.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.METHOD, Matchers.REGEX, ".+ET", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.METHOD, Matchers.REGEX, "GET", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.METHOD, Matchers.REGEX, "POST", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchURL() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.URL, Matchers.REGEX, "https://example.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.URL, Matchers.REGEX, ".+/burp/.+", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.URL, Matchers.REGEX, "https://example.com/burp/marker.txt", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.URL, Matchers.REGEX, "http://.*", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchInScope() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.IS_IN_SCOPE, Matchers.BOOLEAN_EQUALS, true, RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.IS_IN_SCOPE, Matchers.BOOLEAN_EQUALS, false, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchFileExtension() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.FILE_EXTENSION, Matchers.REGEX, "t.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.FILE_EXTENSION, Matchers.REGEX, ".+xt", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.FILE_EXTENSION, Matchers.REGEX, "txt", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.FILE_EXTENSION, Matchers.REGEX, "json", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchHasParameters() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.HAS_PARAMETERS, Matchers.BOOLEAN_EQUALS, true, RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.HAS_PARAMETERS, Matchers.BOOLEAN_EQUALS, false, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchCookieName() {

        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.COOKIE_NAMES, Matchers.ANY_REGEX, "bis.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.COOKIE_NAMES, Matchers.ANY_REGEX, ".+cuit", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.COOKIE_NAMES, Matchers.ANY_REGEX, "biscuit", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.COOKIE_NAMES, Matchers.ANY_REGEX, "cookie", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchCookieValue() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.COOKIE_VALUES, Matchers.ANY_REGEX, "jumbo-.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.COOKIE_VALUES, Matchers.ANY_REGEX, ".+brownie", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.COOKIE_VALUES, Matchers.ANY_REGEX, "jumbo-brownie", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.COOKIE_VALUES, Matchers.ANY_REGEX, "oatmeal", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchHeaderName() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.HEADER_NAMES, Matchers.ANY_REGEX, "user-.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.HEADER_NAMES, Matchers.ANY_REGEX, ".+agent", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.HEADER_NAMES, Matchers.ANY_REGEX, "user-agent", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.HEADER_NAMES, Matchers.ANY_REGEX, "server", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchHeaderValue() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.HEADER_VALUES, Matchers.ANY_REGEX, "ja.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.HEADER_VALUES, Matchers.ANY_REGEX, ".+va", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.HEADER_VALUES, Matchers.ANY_REGEX, "java", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.HEADER_VALUES, Matchers.ANY_REGEX, "python", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchBody() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.BODY, Matchers.REGEX, "burp-.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.BODY, Matchers.REGEX, ".+marker", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.BODY, Matchers.REGEX, "burp-marker", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.BODY, Matchers.REGEX, "marker-burp", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchParameterName() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.PARAMETER_NAMES, Matchers.ANY_REGEX, "for.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.PARAMETER_NAMES, Matchers.ANY_REGEX, ".+mat", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.PARAMETER_NAMES, Matchers.ANY_REGEX, "format", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.PARAMETER_NAMES, Matchers.ANY_REGEX, "payload", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchParameterValue() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.PARAMETER_VALUES, Matchers.ANY_REGEX, "js.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.PARAMETER_VALUES, Matchers.ANY_REGEX, ".+on", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.PARAMETER_VALUES, Matchers.ANY_REGEX, "json", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.PARAMETER_VALUES, Matchers.ANY_REGEX, "xml", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }

    @Test
    void shouldMatchListenerPort() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, RequestProperties.LISTENER_INTERFACE, Matchers.REGEX, "127\\.0\\.0\\.1:.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.LISTENER_INTERFACE, Matchers.REGEX, ".+:8080", RulePolarity.MATCH),
                Rule.of(Operator.OR, RequestProperties.LISTENER_INTERFACE, Matchers.REGEX, "127\\.0\\.0\\.1:8080", RulePolarity.MATCH),
                Rule.of(Operator.AND, RequestProperties.LISTENER_INTERFACE, Matchers.REGEX, ".+:9090", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockRequest));
    }
}
