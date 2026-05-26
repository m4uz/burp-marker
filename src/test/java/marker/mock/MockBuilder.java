package marker.mock;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Marker;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.ContentType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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

    public static InterceptedRequestBuilder interceptedRequest() {
        return new InterceptedRequestBuilder();
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
}
