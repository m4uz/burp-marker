package marker.rule;

import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.proxy.http.InterceptedResponse;
import marker.mock.MockBuilder;
import marker.mock.MockHttpRequest;
import marker.mock.MockHttpResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseRuleTest {

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

    private final MockHttpResponse mockResponse = MockBuilder.interceptedResponse()
            .request(mockRequest)
            .listenerInterface("127.0.0.1:8080")
            .statusCode((short) 200)
            .mimeType(MimeType.HTML)
            .httpHeader(MockBuilder.httpHeader()
                    .name("content-type")
                    .value("text/html")
                    .build())
            .httpHeader(MockBuilder.httpHeader()
                    .name("server")
                    .value("java")
                    .build())
            .body("response-marker")
            .cookie(MockBuilder.cookie()
                    .name("biscuit")
                    .value("coconut-macaron")
                    .build())
            .build();

    @Test
    public void shouldMatchDomain() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_DOMAIN, Matchers.REGEX, "example.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_DOMAIN, Matchers.REGEX, ".+com", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_DOMAIN, Matchers.REGEX, "example.com", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_DOMAIN, Matchers.REGEX, "moc.elpmaxe", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchProtocol() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_IS_SECURE, Matchers.BOOLEAN_EQUALS, true, RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_IS_SECURE, Matchers.BOOLEAN_EQUALS, false, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchMethod() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_METHOD, Matchers.REGEX, "G.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_METHOD, Matchers.REGEX, ".+ET", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_METHOD, Matchers.REGEX, "GET", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_METHOD, Matchers.REGEX, "POST", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchURL() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_URL, Matchers.REGEX, "https://example.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_URL, Matchers.REGEX, ".+/burp/.+", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_URL, Matchers.REGEX, "https://example.com/burp/marker.txt", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_URL, Matchers.REGEX, "http://.*", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchInScope() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_IS_IN_SCOPE, Matchers.BOOLEAN_EQUALS, true, RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_IS_IN_SCOPE, Matchers.BOOLEAN_EQUALS, false, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchFileExtension() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_FILE_EXTENSION, Matchers.REGEX, "t.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_FILE_EXTENSION, Matchers.REGEX, ".+xt", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_FILE_EXTENSION, Matchers.REGEX, "txt", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_FILE_EXTENSION, Matchers.REGEX, "json", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchHasParameters() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_HAS_PARAMETERS, Matchers.BOOLEAN_EQUALS, true, RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_HAS_PARAMETERS, Matchers.BOOLEAN_EQUALS, false, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchRequestHeaderName() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_HEADER_NAMES, Matchers.ANY_REGEX, "user-.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_HEADER_NAMES, Matchers.ANY_REGEX, ".+agent", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_HEADER_NAMES, Matchers.ANY_REGEX, "user-agent", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_HEADER_NAMES, Matchers.ANY_REGEX, "server", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchRequestHeaderValue() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_HEADER_VALUES, Matchers.ANY_REGEX, "ja.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_HEADER_VALUES, Matchers.ANY_REGEX, ".+va", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_HEADER_VALUES, Matchers.ANY_REGEX, "java", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_HEADER_VALUES, Matchers.ANY_REGEX, "python", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchCookieName() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.COOKIE_NAMES, Matchers.ANY_REGEX, "bis.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.COOKIE_NAMES, Matchers.ANY_REGEX, ".+cuit", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.COOKIE_NAMES, Matchers.ANY_REGEX, "biscuit", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.COOKIE_NAMES, Matchers.ANY_REGEX, "cookie", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchCookieValue() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.COOKIE_VALUES, Matchers.ANY_REGEX, "coconut-.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.COOKIE_VALUES, Matchers.ANY_REGEX, ".+macaron", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.COOKIE_VALUES, Matchers.ANY_REGEX, "coconut-macaron", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.COOKIE_VALUES, Matchers.ANY_REGEX, "oatmeal", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchHeaderName() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.HEADER_NAMES, Matchers.ANY_REGEX, "ser.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.HEADER_NAMES, Matchers.ANY_REGEX, ".+ver", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.HEADER_NAMES, Matchers.ANY_REGEX, "server", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.HEADER_NAMES, Matchers.ANY_REGEX, "location", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchHeaderValue() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.HEADER_VALUES, Matchers.ANY_REGEX, "ja.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.HEADER_VALUES, Matchers.ANY_REGEX, ".+va", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.HEADER_VALUES, Matchers.ANY_REGEX, "java", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.HEADER_VALUES, Matchers.ANY_REGEX, "python", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchBody() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.BODY, Matchers.REGEX, "response-.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.BODY, Matchers.REGEX, ".+marker", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.BODY, Matchers.REGEX, "response-marker", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.BODY, Matchers.REGEX, "marker-response", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchParameterName() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_PARAMETER_NAMES, Matchers.ANY_REGEX, "for.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_PARAMETER_NAMES, Matchers.ANY_REGEX, ".+mat", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_PARAMETER_NAMES, Matchers.ANY_REGEX, "format", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_PARAMETER_NAMES, Matchers.ANY_REGEX, "payload", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchParameterValue() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.REQUEST_PARAMETER_VALUES, Matchers.ANY_REGEX, "js.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_PARAMETER_VALUES, Matchers.ANY_REGEX, ".+on", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.REQUEST_PARAMETER_VALUES, Matchers.ANY_REGEX, "json", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.REQUEST_PARAMETER_VALUES, Matchers.ANY_REGEX, "xml", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchStatusCode() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.STATUS_CODE, Matchers.SHORT_EQUALS, (short) 200, RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.STATUS_CODE, Matchers.SHORT_EQUALS, (short) 404, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchContentType() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.CONTENT_TYPE, Matchers.REGEX, "text/.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.CONTENT_TYPE, Matchers.REGEX, ".+/html", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.CONTENT_TYPE, Matchers.REGEX, "text/html", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.CONTENT_TYPE, Matchers.REGEX, "application/json", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchMimeType() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.MIME_TYPE, MimeType::equals, MimeType.HTML, RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.MIME_TYPE, MimeType::equals, MimeType.JSON, RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }

    @Test
    public void shouldMatchListenerPort() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.addAll(List.of(
                Rule.of(Operator.OR, ResponseProperties.LISTENER_INTERFACE, Matchers.REGEX, "127\\.0\\.0\\.1:.+", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.LISTENER_INTERFACE, Matchers.REGEX, ".+:8080", RulePolarity.MATCH),
                Rule.of(Operator.OR, ResponseProperties.LISTENER_INTERFACE, Matchers.REGEX, "127\\.0\\.0\\.1:8080", RulePolarity.MATCH),
                Rule.of(Operator.AND, ResponseProperties.LISTENER_INTERFACE, Matchers.REGEX, ".+:9090", RulePolarity.NOT_MATCH)
        ));

        assertTrue(ruleSet.evaluate(mockResponse));
    }
}
