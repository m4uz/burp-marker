package marker.rule;

import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.http.InterceptedRequest;

import java.util.List;
import java.util.function.Function;

public final class RequestProperties {
    private RequestProperties() {
    }

    public static final Function<HttpRequest, String> DOMAIN =
            request -> request.httpService().host();

    public static final Function<HttpRequest, String> METHOD =
            HttpRequest::method;

    public static final Function<HttpRequest, String> URL =
            HttpRequest::url;

    public static final Function<HttpRequest, String> FILE_EXTENSION =
            HttpRequest::fileExtension;

    public static final Function<HttpRequest, List<String>> COOKIE_NAMES =
            request -> request.parameters(HttpParameterType.COOKIE).stream()
                    .map(ParsedHttpParameter::name)
                    .toList();

    public static final Function<HttpRequest, List<String>> COOKIE_VALUES =
            request -> request.parameters(HttpParameterType.COOKIE).stream()
                    .map(ParsedHttpParameter::value)
                    .toList();

    public static final Function<HttpRequest, List<String>> HEADER_NAMES =
            request -> request.headers().stream().map(HttpHeader::name).toList();

    public static final Function<HttpRequest, List<String>> HEADER_VALUES =
            request -> request.headers().stream().map(HttpHeader::value).toList();

    public static final Function<HttpRequest, String> BODY =
            HttpRequest::bodyToString;

    public static final Function<HttpRequest, List<String>> PARAMETER_NAMES =
            request -> request.parameters().stream().map(ParsedHttpParameter::name).toList();

    public static final Function<HttpRequest, List<String>> PARAMETER_VALUES =
            request -> request.parameters().stream().map(ParsedHttpParameter::value).toList();

    public static final Function<InterceptedRequest, String> LISTENER_INTERFACE =
            InterceptedRequest::listenerInterface;
}
