package marker.rule;

import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.proxy.http.InterceptedResponse;
import marker.mock.MockBuilder;
import marker.mock.MockHttpRequest;
import marker.mock.MockHttpResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResponseRuleTest {

    private final MockHttpRequest mockRequest = MockBuilder.interceptedRequest()
            .listenerInterface("127.0.0.1:8080")
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
            .pageTitle("burp-marker")
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
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.request().httpService().host(), String::matches, "example.com")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchProtocol() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.request().httpService().secure(), Boolean::equals, true)
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchMethod() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.request().method(), String::matches, "GET")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchFileExtension() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.request().fileExtension(), String::matches, "txt")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchCookieName() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.cookies().stream().map(Cookie::name).toList(),
                        (cookieNames, condition) -> cookieNames.stream().anyMatch(cookieName -> cookieName.matches(condition)),
                        "biscuit")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchCookieValue() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.cookies().stream().map(Cookie::value).toList(),
                        (cookieValues, condition) -> cookieValues.stream().anyMatch(cookieValue -> cookieValue.matches(condition)),
                        "coconut-macaron")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchHeaderName() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.headers().stream().map(HttpHeader::name).toList(),
                        (headerNames, condition) -> headerNames.stream().anyMatch(headerName -> headerName.matches(condition)),
                        "server")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchHeaderValue() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.headers().stream().map(HttpHeader::value).toList(),
                        (headerValues, condition) -> headerValues.stream().anyMatch(headerValue -> headerValue.matches(condition)),
                        "java")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchBody() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedResponse::bodyToString, String::matches, "response-marker")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchParameterName() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.request().parameters().stream().map(ParsedHttpParameter::name).toList(),
                        (parameterNames, condition) -> parameterNames.stream().anyMatch(parameterName -> parameterName.matches(condition)),
                        "format")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchParameterValue() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.request().parameters().stream().map(ParsedHttpParameter::value).toList(),
                        (parameterValues, condition) -> parameterValues.stream().anyMatch(parameterValue -> parameterValue.matches(condition)),
                        "json")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchStatusCode() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedResponse::statusCode, Short::equals, (short) 200)
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchContentType() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.headerValue("content-type"), String::matches, "text/html")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchMimeType() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedResponse::mimeType, MimeType::equals, MimeType.HTML)
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchPageTitle() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ((MockHttpResponse) ir).pageTitle(), String::matches, "burp-marker")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }

    @Test
    public void shouldMatchListenerPort() {
        RuleSet<InterceptedResponse> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedResponse::listenerInterface, String::matches, ".+:8080")
        );

        boolean match = ruleSet.evaluate(mockResponse);
        assertTrue(match);
    }
}
