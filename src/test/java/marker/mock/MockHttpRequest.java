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
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.requests.HttpTransformation;
import burp.api.montoya.proxy.http.InterceptedRequest;

import java.net.InetAddress;
import java.util.List;
import java.util.regex.Pattern;

public class MockHttpRequest implements InterceptedRequest {
    private final Annotations annotations;
    private final boolean inScope;
    private final HttpService httpService;
    private final String url;
    private final String method;
    private final String path;
    private final String query;
    private final String pathWithoutQuery;
    private final String fileExtension;
    private final ContentType contentType;
    private final List<ParsedHttpParameter> parameters;
    private final List<HttpHeader> headers;
    private final int bodyOffset;
    private final String body;
    private final List<Marker> markers;
    private final int messageId;
    private final String listenerInterface;
    private final InetAddress sourceIpAddress;
    private final InetAddress destinationIpAddress;

    public MockHttpRequest() {
        this(null, false, null, null, null, null, null, null, null, null, null, null, 0, null, null, 0, null, null, null);
    }

    public MockHttpRequest(
            Annotations annotations,
            boolean inScope,
            HttpService httpService,
            String url,
            String method,
            String path,
            String query,
            String pathWithoutQuery,
            String fileExtension,
            ContentType contentType,
            List<ParsedHttpParameter> parameters,
            List<HttpHeader> headers,
            int bodyOffset,
            String body,
            List<Marker> markers,
            int messageId,
            String listenerInterface,
            InetAddress sourceIpAddress,
            InetAddress destinationIpAddress
    ) {
        this.annotations = annotations;
        this.inScope = inScope;
        this.httpService = httpService;
        this.url = url;
        this.method = method;
        this.path = path;
        this.query = query;
        this.pathWithoutQuery = pathWithoutQuery;
        this.fileExtension = fileExtension;
        this.contentType = contentType;
        this.parameters = parameters;
        this.headers = headers;
        this.bodyOffset = bodyOffset;
        this.body = body;
        this.markers = markers;
        this.messageId = messageId;
        this.listenerInterface = listenerInterface;
        this.sourceIpAddress = sourceIpAddress;
        this.destinationIpAddress = destinationIpAddress;
    }

    @Override
    public Annotations annotations() {
        return annotations;
    }

    @Override
    public boolean isInScope() {
        return inScope;
    }

    @Override
    public HttpService httpService() {
        return httpService;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public String pathWithoutQuery() {
        return pathWithoutQuery;
    }

    @Override
    public String fileExtension() {
        return fileExtension;
    }

    @Override
    public ContentType contentType() {
        return contentType;
    }

    @Override
    public List<ParsedHttpParameter> parameters() {
        return parameters;
    }

    @Override
    public List<ParsedHttpParameter> parameters(HttpParameterType type) {
        if (parameters == null) {
            return null;
        }

        return parameters.stream()
                .filter(parameter -> parameter.type() == type)
                .toList();
    }

    @Override
    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    @Override
    public boolean hasParameters(HttpParameterType type) {
        return parameters != null && parameters.stream().anyMatch(parameter -> parameter.type() == type);
    }

    @Override
    public ParsedHttpParameter parameter(String name, HttpParameterType type) {
        if (parameters == null) {
            return null;
        }

        return parameters.stream()
                .filter(parameter -> name.equals(parameter.name()) && type == parameter.type())
                .findFirst()
                .orElse(null);
    }

    @Override
    public String parameterValue(String name, HttpParameterType type) {
        ParsedHttpParameter parameter = parameter(name, type);
        return parameter == null ? null : parameter.value();
    }

    @Override
    public ParsedHttpParameter parameter(String name) {
        if (parameters == null) {
            return null;
        }

        return parameters.stream()
                .filter(parameter -> name.equals(parameter.name()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String parameterValue(String name) {
        ParsedHttpParameter parameter = parameter(name);
        return parameter == null ? null : parameter.value();
    }

    @Override
    public boolean hasParameter(String name, HttpParameterType type) {
        return parameter(name, type) != null;
    }

    @Override
    public boolean hasParameter(HttpParameter parameter) {
        if (parameters == null || parameter == null) {
            return false;
        }

        return parameters.stream().anyMatch(existing ->
                existing.type() == parameter.type()
                        && existing.name().equals(parameter.name())
                        && existing.value().equals(parameter.value()));
    }

    @Override
    public boolean hasHeader(HttpHeader header) {
        if (headers == null || header == null) {
            return false;
        }

        return headers.stream().anyMatch(existing ->
                existing.name().equals(header.name()) && existing.value().equals(header.value()));
    }

    @Override
    public boolean hasHeader(String name) {
        return header(name) != null;
    }

    @Override
    public boolean hasHeader(String name, String value) {
        if (headers == null) {
            return false;
        }

        return headers.stream().anyMatch(header ->
                name.equals(header.name()) && value.equals(header.value()));
    }

    @Override
    public HttpHeader header(String name) {
        if (headers == null) {
            return null;
        }

        return headers.stream()
                .filter(header -> name.equals(header.name()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String headerValue(String name) {
        HttpHeader header = header(name);
        return header == null ? null : header.value();
    }

    @Override
    public List<HttpHeader> headers() {
        return headers;
    }

    @Override
    public String httpVersion() {
        return null;
    }

    @Override
    public int bodyOffset() {
        return bodyOffset;
    }

    @Override
    public ByteArray body() {
        return null;
    }

    @Override
    public String bodyToString() {
        return body;
    }

    @Override
    public List<Marker> markers() {
        return markers;
    }

    @Override
    public boolean contains(String searchTerm, boolean caseSensitive) {
        return false;
    }

    @Override
    public boolean contains(Pattern pattern) {
        return false;
    }

    @Override
    public ByteArray toByteArray() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public HttpRequest copyToTempFile() {
        return null;
    }

    @Override
    public HttpRequest withService(HttpService service) {
        return null;
    }

    @Override
    public HttpRequest withPath(String path) {
        return null;
    }

    @Override
    public HttpRequest withMethod(String method) {
        return null;
    }

    @Override
    public HttpRequest withHeader(HttpHeader header) {
        return null;
    }

    @Override
    public HttpRequest withHeader(String name, String value) {
        return null;
    }

    @Override
    public HttpRequest withParameter(HttpParameter parameters) {
        return null;
    }

    @Override
    public HttpRequest withAddedParameters(List<? extends HttpParameter> parameters) {
        return null;
    }

    @Override
    public HttpRequest withAddedParameters(HttpParameter... parameters) {
        return null;
    }

    @Override
    public HttpRequest withRemovedParameters(List<? extends HttpParameter> parameters) {
        return null;
    }

    @Override
    public HttpRequest withRemovedParameters(HttpParameter... parameters) {
        return null;
    }

    @Override
    public HttpRequest withUpdatedParameters(List<? extends HttpParameter> parameters) {
        return null;
    }

    @Override
    public HttpRequest withUpdatedParameters(HttpParameter... parameters) {
        return null;
    }

    @Override
    public HttpRequest withTransformationApplied(HttpTransformation transformation) {
        return null;
    }

    @Override
    public HttpRequest withBody(String body) {
        return null;
    }

    @Override
    public HttpRequest withBody(ByteArray body) {
        return null;
    }

    @Override
    public HttpRequest withAddedHeader(String name, String value) {
        return null;
    }

    @Override
    public HttpRequest withAddedHeader(HttpHeader header) {
        return null;
    }

    @Override
    public HttpRequest withAddedHeaders(List<? extends HttpHeader> headers) {
        return null;
    }

    @Override
    public HttpRequest withAddedHeaders(HttpHeader... headers) {
        return null;
    }

    @Override
    public HttpRequest withUpdatedHeader(String name, String value) {
        return null;
    }

    @Override
    public HttpRequest withUpdatedHeader(HttpHeader header) {
        return null;
    }

    @Override
    public HttpRequest withUpdatedHeaders(List<? extends HttpHeader> headers) {
        return null;
    }

    @Override
    public HttpRequest withUpdatedHeaders(HttpHeader... headers) {
        return null;
    }

    @Override
    public HttpRequest withRemovedHeader(String name) {
        return null;
    }

    @Override
    public HttpRequest withRemovedHeader(HttpHeader header) {
        return null;
    }

    @Override
    public HttpRequest withRemovedHeaders(List<? extends HttpHeader> headers) {
        return null;
    }

    @Override
    public HttpRequest withRemovedHeaders(HttpHeader... headers) {
        return null;
    }

    @Override
    public HttpRequest withMarkers(List<Marker> markers) {
        return null;
    }

    @Override
    public HttpRequest withMarkers(Marker... markers) {
        return null;
    }

    @Override
    public HttpRequest withDefaultHeaders() {
        return null;
    }

    @Override
    public int messageId() {
        return messageId;
    }

    @Override
    public String listenerInterface() {
        return listenerInterface;
    }

    @Override
    public InetAddress sourceIpAddress() {
        return sourceIpAddress;
    }

    @Override
    public InetAddress destinationIpAddress() {
        return destinationIpAddress;
    }
}
