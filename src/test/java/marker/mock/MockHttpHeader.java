package marker.mock;

import burp.api.montoya.http.message.HttpHeader;

public class MockHttpHeader implements HttpHeader {
    private final String name;
    private final String value;

    public MockHttpHeader() {
        this(null, null);
    }

    public MockHttpHeader(String name, String value) {
        this.name = name;
        this.value = value;
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
    public String toString() {
        return null;
    }
}
