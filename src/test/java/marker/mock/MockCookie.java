package marker.mock;

import burp.api.montoya.http.message.Cookie;

import java.time.ZonedDateTime;
import java.util.Optional;

public class MockCookie implements Cookie {
    private final String name;
    private final String value;
    private final String domain;
    private final String path;
    private final Optional<ZonedDateTime> expiration;

    public MockCookie(String name, String value, String domain, String path, Optional<ZonedDateTime> expiration) {
        this.name = name;
        this.value = value;
        this.domain = domain;
        this.path = path;
        this.expiration = expiration == null ? Optional.empty() : expiration;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String domain() {
        return domain;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public Optional<ZonedDateTime> expiration() {
        return expiration;
    }
}
