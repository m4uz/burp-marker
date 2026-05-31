package marker.mock;

import burp.api.montoya.core.Annotations;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.core.Marker;
import burp.api.montoya.http.message.Cookie;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.http.message.StatusCodeClass;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.http.message.responses.analysis.Attribute;
import burp.api.montoya.http.message.responses.analysis.AttributeType;
import burp.api.montoya.http.message.responses.analysis.KeywordCount;
import burp.api.montoya.proxy.http.InterceptedResponse;

import java.net.InetAddress;
import java.util.List;
import java.util.regex.Pattern;

public class MockHttpResponse implements InterceptedResponse {
    private final HttpRequest request;
    private final Annotations annotations;
    private final short statusCode;
    private final String reasonPhrase;
    private final String httpVersion;
    private final List<HttpHeader> headers;
    private final int bodyOffset;
    private final String body;
    private final List<Marker> markers;
    private final List<Cookie> cookies;
    private final MimeType mimeType;
    private final MimeType statedMimeType;
    private final MimeType inferredMimeType;
    private final String pageTitle;
    private final int messageId;
    private final String listenerInterface;
    private final InetAddress sourceIpAddress;
    private final InetAddress destinationIpAddress;

    public MockHttpResponse() {
        this(null, null, (short) 0, null, null, null, 0, null, null, null, null, null, null, null, 0, null, null, null);
    }

    public MockHttpResponse(
            HttpRequest request,
            Annotations annotations,
            short statusCode,
            String reasonPhrase,
            String httpVersion,
            List<HttpHeader> headers,
            int bodyOffset,
            String body,
            List<Marker> markers,
            List<Cookie> cookies,
            MimeType mimeType,
            MimeType statedMimeType,
            MimeType inferredMimeType,
            String pageTitle,
            int messageId,
            String listenerInterface,
            InetAddress sourceIpAddress,
            InetAddress destinationIpAddress
    ) {
        this.request = request;
        this.annotations = annotations;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.bodyOffset = bodyOffset;
        this.body = body;
        this.markers = markers;
        this.cookies = cookies;
        this.mimeType = mimeType;
        this.statedMimeType = statedMimeType;
        this.inferredMimeType = inferredMimeType;
        this.pageTitle = pageTitle;
        this.messageId = messageId;
        this.listenerInterface = listenerInterface;
        this.sourceIpAddress = sourceIpAddress;
        this.destinationIpAddress = destinationIpAddress;
    }

    @Override
    public HttpRequest request() {
        return request;
    }

    @Override
    public HttpRequest initiatingRequest() {
        return request;
    }

    @Override
    public Annotations annotations() {
        return annotations;
    }

    @Override
    public short statusCode() {
        return statusCode;
    }

    @Override
    public String reasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public boolean isStatusCodeClass(StatusCodeClass statusCodeClass) {
        return false;
    }

    @Override
    public String httpVersion() {
        return httpVersion;
    }

    @Override
    public List<HttpHeader> headers() {
        return headers;
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
    public ByteArray body() {
        return body == null ? null : ByteArray.byteArray(body);
    }

    @Override
    public String bodyToString() {
        return body;
    }

    @Override
    public int bodyOffset() {
        return bodyOffset;
    }

    @Override
    public List<Marker> markers() {
        return markers;
    }

    @Override
    public List<Cookie> cookies() {
        return cookies;
    }

    @Override
    public Cookie cookie(String name) {
        if (cookies == null) {
            return null;
        }

        return cookies.stream()
                .filter(cookie -> name.equals(cookie.name()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String cookieValue(String name) {
        Cookie cookie = cookie(name);
        return cookie == null ? null : cookie.value();
    }

    @Override
    public boolean hasCookie(String name) {
        return cookie(name) != null;
    }

    @Override
    public boolean hasCookie(Cookie cookie) {
        if (cookies == null || cookie == null) {
            return false;
        }

        return cookies.stream().anyMatch(existing ->
                existing.name().equals(cookie.name()) && existing.value().equals(cookie.value()));
    }

    @Override
    public MimeType mimeType() {
        return mimeType;
    }

    @Override
    public MimeType statedMimeType() {
        return statedMimeType;
    }

    @Override
    public MimeType inferredMimeType() {
        return inferredMimeType;
    }

    public String pageTitle() {
        return pageTitle;
    }

    @Override
    public List<KeywordCount> keywordCounts(String... keywords) {
        return null;
    }

    @Override
    public List<Attribute> attributes(AttributeType... types) {
        return null;
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
    public HttpResponse copyToTempFile() {
        return null;
    }

    @Override
    public HttpResponse withStatusCode(short statusCode) {
        return null;
    }

    @Override
    public HttpResponse withReasonPhrase(String reasonPhrase) {
        return null;
    }

    @Override
    public HttpResponse withHttpVersion(String httpVersion) {
        return null;
    }

    @Override
    public HttpResponse withBody(String body) {
        return null;
    }

    @Override
    public HttpResponse withBody(ByteArray body) {
        return null;
    }

    @Override
    public HttpResponse withAddedHeader(HttpHeader header) {
        return null;
    }

    @Override
    public HttpResponse withAddedHeader(String name, String value) {
        return null;
    }

    @Override
    public HttpResponse withAddedHeaders(List<? extends HttpHeader> headers) {
        return null;
    }

    @Override
    public HttpResponse withAddedHeaders(HttpHeader... headers) {
        return null;
    }

    @Override
    public HttpResponse withUpdatedHeader(HttpHeader header) {
        return null;
    }

    @Override
    public HttpResponse withUpdatedHeader(String name, String value) {
        return null;
    }

    @Override
    public HttpResponse withUpdatedHeaders(List<? extends HttpHeader> headers) {
        return null;
    }

    @Override
    public HttpResponse withUpdatedHeaders(HttpHeader... headers) {
        return null;
    }

    @Override
    public HttpResponse withRemovedHeader(HttpHeader header) {
        return null;
    }

    @Override
    public HttpResponse withRemovedHeader(String name) {
        return null;
    }

    @Override
    public HttpResponse withRemovedHeaders(List<? extends HttpHeader> headers) {
        return null;
    }

    @Override
    public HttpResponse withRemovedHeaders(HttpHeader... headers) {
        return null;
    }

    @Override
    public HttpResponse withMarkers(List<Marker> markers) {
        return null;
    }

    @Override
    public HttpResponse withMarkers(Marker... markers) {
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
