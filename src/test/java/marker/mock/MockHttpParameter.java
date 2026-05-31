package marker.mock;

import burp.api.montoya.core.Range;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;

public class MockHttpParameter implements ParsedHttpParameter {
    private final HttpParameterType type;
    private final String name;
    private final String value;

    public MockHttpParameter() {
        this(null, null, null);
    }

    public MockHttpParameter(String name, String value, HttpParameterType type) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public HttpParameterType type() {
        return type;
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
    public Range nameOffsets() {
        return null;
    }

    @Override
    public Range valueOffsets() {
        return null;
    }
}
