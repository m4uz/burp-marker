package marker.mock;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Marker;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.ContentType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MockBuilder {
    private MockBuilder() {
    }

    public static HttpServiceBuilder httpService() {
        return new HttpServiceBuilder();
    }

    public static HttpHeaderBuilder httpHeader() {
        return new HttpHeaderBuilder();
    }

    public static HttpParameterBuilder httpParameter() {
        return new HttpParameterBuilder();
    }

    public static CookieBuilder cookie() {
        return new CookieBuilder();
    }

    public static InterceptedRequestBuilder interceptedRequest() {
        return new InterceptedRequestBuilder();
    }

    public static InterceptedResponseBuilder interceptedResponse() {
        return new InterceptedResponseBuilder();
    }

    public static final class HttpServiceBuilder {
        private String host;
        private int port;
        private boolean secure;
        private String ipAddress;

        public HttpServiceBuilder host(String host) {
            this.host = host;
            return this;
        }

        public HttpServiceBuilder port(int port) {
            this.port = port;
            return this;
        }

        public HttpServiceBuilder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public HttpServiceBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public MockHttpService build() {
            return new MockHttpService(host, port, secure, ipAddress);
        }
    }

    public static final class HttpHeaderBuilder {
        private String name;
        private String value;

        public HttpHeaderBuilder name(String name) {
            this.name = name;
            return this;
        }

        public HttpHeaderBuilder value(String value) {
            this.value = value;
            return this;
        }

        public MockHttpHeader build() {
            return new MockHttpHeader(name, value);
        }
    }

    public static final class HttpParameterBuilder {
        private String name;
        private String value;
        private HttpParameterType type;

        public HttpParameterBuilder name(String name) {
            this.name = name;
            return this;
        }

        public HttpParameterBuilder value(String value) {
            this.value = value;
            return this;
        }

        public HttpParameterBuilder type(HttpParameterType type) {
            this.type = type;
            return this;
        }

        public MockHttpParameter build() {
            return new MockHttpParameter(name, value, type);
        }
    }

    public static final class CookieBuilder {
        private String name;
        private String value;
        private String domain;
        private String path;
        private Optional<ZonedDateTime> expiration = Optional.empty();

        public CookieBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CookieBuilder value(String value) {
            this.value = value;
            return this;
        }

        public CookieBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public CookieBuilder path(String path) {
            this.path = path;
            return this;
        }

        public CookieBuilder expiration(ZonedDateTime expiration) {
            this.expiration = Optional.ofNullable(expiration);
            return this;
        }

        public MockCookie build() {
            return new MockCookie(name, value, domain, path, expiration);
        }
    }

    public static final class InterceptedRequestBuilder {
        private Annotations annotations;
        private boolean inScope;
        private HttpService httpService;
        private String url;
        private String method;
        private String path;
        private String query;
        private String pathWithoutQuery;
        private String fileExtension;
        private ContentType contentType;
        private final List<ParsedHttpParameter> parameters = new ArrayList<>();
        private final List<HttpHeader> headers = new ArrayList<>();
        private int bodyOffset;
        private String body;
        private String bodyToString;
        private List<Marker> markers;
        private int messageId;
        private String listenerInterface;
        private InetAddress sourceIpAddress;
        private InetAddress destinationIpAddress;

        public InterceptedRequestBuilder annotations(Annotations annotations) {
            this.annotations = annotations;
            return this;
        }

        public InterceptedRequestBuilder inScope(boolean inScope) {
            this.inScope = inScope;
            return this;
        }

        public InterceptedRequestBuilder httpService(HttpService httpService) {
            this.httpService = httpService;
            return this;
        }

        public InterceptedRequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public InterceptedRequestBuilder method(String method) {
            this.method = method;
            return this;
        }

        public InterceptedRequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        public InterceptedRequestBuilder query(String query) {
            this.query = query;
            return this;
        }

        public InterceptedRequestBuilder pathWithoutQuery(String pathWithoutQuery) {
            this.pathWithoutQuery = pathWithoutQuery;
            return this;
        }

        public InterceptedRequestBuilder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            return this;
        }

        public InterceptedRequestBuilder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public InterceptedRequestBuilder httpParameter(HttpParameter parameter) {
            if (parameter instanceof ParsedHttpParameter parsedParameter) {
                this.parameters.add(parsedParameter);
            } else {
                this.parameters.add(new MockHttpParameter(parameter.name(), parameter.value(), parameter.type()));
            }
            return this;
        }

        public InterceptedRequestBuilder httpParameters(List<? extends HttpParameter> parameters) {
            for (HttpParameter parameter : parameters) {
                httpParameter(parameter);
            }
            return this;
        }

        public InterceptedRequestBuilder httpHeader(HttpHeader header) {
            this.headers.add(header);
            return this;
        }

        public InterceptedRequestBuilder httpHeaders(List<? extends HttpHeader> headers) {
            this.headers.addAll(headers);
            return this;
        }

        public InterceptedRequestBuilder headers(List<HttpHeader> headers) {
            this.headers.clear();
            this.headers.addAll(headers);
            return this;
        }

        public InterceptedRequestBuilder bodyOffset(int bodyOffset) {
            this.bodyOffset = bodyOffset;
            return this;
        }

        public InterceptedRequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public InterceptedRequestBuilder bodyToString(String bodyToString) {
            this.bodyToString = bodyToString;
            return this;
        }

        public InterceptedRequestBuilder markers(List<Marker> markers) {
            this.markers = markers;
            return this;
        }

        public InterceptedRequestBuilder messageId(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public InterceptedRequestBuilder listenerInterface(String listenerInterface) {
            this.listenerInterface = listenerInterface;
            return this;
        }

        public InterceptedRequestBuilder sourceIpAddress(InetAddress sourceIpAddress) {
            this.sourceIpAddress = sourceIpAddress;
            return this;
        }

        public InterceptedRequestBuilder destinationIpAddress(InetAddress destinationIpAddress) {
            this.destinationIpAddress = destinationIpAddress;
            return this;
        }

        public MockHttpRequest build() {
            return new MockHttpRequest(
                    annotations,
                    inScope,
                    httpService,
                    url,
                    method,
                    path,
                    query,
                    pathWithoutQuery,
                    fileExtension,
                    contentType,
                    parameters.isEmpty() ? null : List.copyOf(parameters),
                    headers.isEmpty() ? null : List.copyOf(headers),
                    bodyOffset,
                    body,
                    markers,
                    messageId,
                    listenerInterface,
                    sourceIpAddress,
                    destinationIpAddress
            );
        }
    }

    public static final class InterceptedResponseBuilder {
        private HttpRequest request;
        private Annotations annotations;
        private short statusCode;
        private String reasonPhrase;
        private String httpVersion;
        private final List<HttpHeader> headers = new ArrayList<>();
        private int bodyOffset;
        private String body;
        private List<Marker> markers;
        private final List<Cookie> cookies = new ArrayList<>();
        private MimeType mimeType;
        private MimeType statedMimeType;
        private MimeType inferredMimeType;
        private String pageTitle;
        private int messageId;
        private String listenerInterface;
        private InetAddress sourceIpAddress;
        private InetAddress destinationIpAddress;

        public InterceptedResponseBuilder request(HttpRequest request) {
            this.request = request;
            return this;
        }

        public InterceptedResponseBuilder initiatingRequest(HttpRequest request) {
            this.request = request;
            return this;
        }

        public InterceptedResponseBuilder annotations(Annotations annotations) {
            this.annotations = annotations;
            return this;
        }

        public InterceptedResponseBuilder statusCode(short statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public InterceptedResponseBuilder reasonPhrase(String reasonPhrase) {
            this.reasonPhrase = reasonPhrase;
            return this;
        }

        public InterceptedResponseBuilder httpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public InterceptedResponseBuilder httpHeader(HttpHeader header) {
            this.headers.add(header);
            return this;
        }

        public InterceptedResponseBuilder httpHeaders(List<? extends HttpHeader> headers) {
            this.headers.addAll(headers);
            return this;
        }

        public InterceptedResponseBuilder headers(List<HttpHeader> headers) {
            this.headers.clear();
            this.headers.addAll(headers);
            return this;
        }

        public InterceptedResponseBuilder bodyOffset(int bodyOffset) {
            this.bodyOffset = bodyOffset;
            return this;
        }

        public InterceptedResponseBuilder body(String body) {
            this.body = body;
            return this;
        }

        public InterceptedResponseBuilder markers(List<Marker> markers) {
            this.markers = markers;
            return this;
        }

        public InterceptedResponseBuilder cookie(Cookie cookie) {
            if (cookie instanceof MockCookie) {
                this.cookies.add(cookie);
            } else {
                this.cookies.add(new MockCookie(cookie.name(), cookie.value(), cookie.domain(), cookie.path(), cookie.expiration()));
            }
            return this;
        }

        public InterceptedResponseBuilder cookies(List<? extends Cookie> cookies) {
            for (Cookie cookie : cookies) {
                cookie(cookie);
            }
            return this;
        }

        public InterceptedResponseBuilder mimeType(MimeType mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public InterceptedResponseBuilder statedMimeType(MimeType statedMimeType) {
            this.statedMimeType = statedMimeType;
            return this;
        }

        public InterceptedResponseBuilder inferredMimeType(MimeType inferredMimeType) {
            this.inferredMimeType = inferredMimeType;
            return this;
        }

        public InterceptedResponseBuilder pageTitle(String pageTitle) {
            this.pageTitle = pageTitle;
            return this;
        }

        public InterceptedResponseBuilder messageId(int messageId) {
            this.messageId = messageId;
            return this;
        }

        public InterceptedResponseBuilder listenerInterface(String listenerInterface) {
            this.listenerInterface = listenerInterface;
            return this;
        }

        public InterceptedResponseBuilder sourceIpAddress(InetAddress sourceIpAddress) {
            this.sourceIpAddress = sourceIpAddress;
            return this;
        }

        public InterceptedResponseBuilder destinationIpAddress(InetAddress destinationIpAddress) {
            this.destinationIpAddress = destinationIpAddress;
            return this;
        }

        public MockHttpResponse build() {
            return new MockHttpResponse(
                    request,
                    annotations,
                    statusCode,
                    reasonPhrase,
                    httpVersion,
                    headers.isEmpty() ? null : List.copyOf(headers),
                    bodyOffset,
                    body,
                    markers,
                    cookies.isEmpty() ? null : List.copyOf(cookies),
                    mimeType,
                    statedMimeType,
                    inferredMimeType,
                    pageTitle,
                    messageId,
                    listenerInterface,
                    sourceIpAddress,
                    destinationIpAddress
            );
        }
    }
}
