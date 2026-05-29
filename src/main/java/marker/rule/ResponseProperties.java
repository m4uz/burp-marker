package marker.rule;

import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.proxy.http.InterceptedResponse;

import java.util.List;
import java.util.function.Function;

public final class ResponseProperties {
    private ResponseProperties() {
    }

    public static final Function<InterceptedResponse, String> REQUEST_DOMAIN =
            response -> RequestProperties.DOMAIN.apply(response.request());

    public static final Function<InterceptedResponse, String> REQUEST_METHOD =
            response -> RequestProperties.METHOD.apply(response.request());

    public static final Function<InterceptedResponse, Boolean> REQUEST_IS_SECURE =
            response -> RequestProperties.IS_SECURE.apply(response.request());

    public static final Function<InterceptedResponse, String> REQUEST_URL =
            response -> RequestProperties.URL.apply(response.request());

    public static final Function<InterceptedResponse, Boolean> REQUEST_IS_IN_SCOPE =
            response -> RequestProperties.IS_IN_SCOPE.apply(response.request());

    public static final Function<InterceptedResponse, String> REQUEST_FILE_EXTENSION =
            response -> RequestProperties.FILE_EXTENSION.apply(response.request());

    public static final Function<InterceptedResponse, Boolean> REQUEST_HAS_PARAMETERS =
            response -> RequestProperties.HAS_PARAMETERS.apply(response.request());

    public static final Function<InterceptedResponse, List<String>> REQUEST_PARAMETER_NAMES =
            response -> RequestProperties.PARAMETER_NAMES.apply(response.request());

    public static final Function<InterceptedResponse, List<String>> REQUEST_PARAMETER_VALUES =
            response -> RequestProperties.PARAMETER_VALUES.apply(response.request());

    public static final Function<InterceptedResponse, List<String>> REQUEST_HEADER_NAMES =
            response -> RequestProperties.HEADER_NAMES.apply(response.request());

    public static final Function<InterceptedResponse, List<String>> REQUEST_HEADER_VALUES =
            response -> RequestProperties.HEADER_VALUES.apply(response.request());

    public static final Function<InterceptedResponse, List<String>> COOKIE_NAMES =
            response -> response.cookies().stream().map(Cookie::name).toList();

    public static final Function<InterceptedResponse, List<String>> COOKIE_VALUES =
            response -> response.cookies().stream().map(Cookie::value).toList();

    public static final Function<InterceptedResponse, List<String>> HEADER_NAMES =
            response -> response.headers().stream().map(HttpHeader::name).toList();

    public static final Function<InterceptedResponse, List<String>> HEADER_VALUES =
            response -> response.headers().stream().map(HttpHeader::value).toList();

    public static final Function<InterceptedResponse, String> BODY =
            InterceptedResponse::bodyToString;

    public static final Function<InterceptedResponse, Short> STATUS_CODE =
            InterceptedResponse::statusCode;

    public static final Function<InterceptedResponse, MimeType> MIME_TYPE =
            InterceptedResponse::mimeType;

    public static final Function<InterceptedResponse, String> CONTENT_TYPE =
            response -> response.headers().stream()
                    .filter(header -> "content-type".equalsIgnoreCase(header.name()))
                    .map(HttpHeader::value)
                    .findFirst()
                    .orElse("");

    public static final Function<InterceptedResponse, String> LISTENER_INTERFACE =
            InterceptedResponse::listenerInterface;
}
