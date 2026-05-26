package marker.rule;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.proxy.http.InterceptedRequest;
import marker.mock.MockBuilder;
import marker.mock.MockHttpRequest;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestRuleTest {

    private final MockHttpRequest mockRequest = MockBuilder.interceptedRequest()
            .listenerInterface("127.0.0.1:8080")
            .httpService(MockBuilder.httpService()
                    .host("example.com")
                    .build())
            .method("GET")
            .path("/burp/marker.txt")
            .url("http://example.com/burp/marker.txt")
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
        ruleSet.add(
                new Rule<>(Operator.OR, ir -> ir.httpService().host(), String::matches, "example.com")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchMethod() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedRequest::method, String::matches, "GET")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchURL() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedRequest::url, String::matches, "http://example.com/burp/marker.txt")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchFileExtension() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedRequest::fileExtension, String::matches, "txt")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchCookieName() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(
                        Operator.OR,
                        ir -> ir.parameters(HttpParameterType.COOKIE).stream().map(ParsedHttpParameter::name).toList(),
                        (cookieNames, condition) -> cookieNames.stream().anyMatch(cookieName -> cookieName.matches(condition)),
                        "biscuit")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchCookieValue() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(
                        Operator.OR,
                        ir -> ir.parameters(HttpParameterType.COOKIE).stream().map(ParsedHttpParameter::value).toList(),
                        (cookieValues, condition) -> cookieValues.stream().anyMatch(cookieValue -> cookieValue.matches(condition)),
                        "jumbo-brownie")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchHeaderName() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(
                        Operator.OR,
                        ir -> ir.headers().stream().map(HttpHeader::name).toList(),
                        (headerNames, condition) -> headerNames.stream().anyMatch(headerName -> headerName.matches(condition)),
                        "user-agent")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchHeaderValue() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(
                        Operator.OR,
                        ir -> ir.headers().stream().map(HttpHeader::value).toList(),
                        (headerValues, condition) -> headerValues.stream().anyMatch(headerValue -> headerValue.matches(condition)),
                        "java")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchBody() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedRequest::bodyToString, String::matches, "burp-marker")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchParameterName() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(
                        Operator.OR,
                        ir -> ir.parameters().stream().map(ParsedHttpParameter::name).toList(),
                        (parameterNames, condition) -> parameterNames.stream().anyMatch(parameterName -> parameterName.matches(condition)),
                        "format"));

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchParameterValue() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(
                        Operator.OR,
                        ir -> ir.parameters().stream().map(ParsedHttpParameter::value).toList(),
                        (parameterValues, condition) -> parameterValues.stream().anyMatch(parameterValue -> parameterValue.matches(condition)),
                        "json"));

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }

    @Test
    void shouldMatchListenerPort() {
        RuleSet<InterceptedRequest> ruleSet = new RuleSet<>();
        ruleSet.add(
                new Rule<>(Operator.OR, InterceptedRequest::listenerInterface, String::matches, ".+:8080")
        );

        boolean match = ruleSet.evaluate(mockRequest);
        assertTrue(match);
    }
}
