package marker.mock;

import burp.api.montoya.http.HttpService;

public class MockHttpService implements HttpService {
    private final String host;
    private final int port;
    private final boolean secure;
    private final String ipAddress;

    public MockHttpService() {
        this(null, 0, false, null);
    }

    public MockHttpService(String host, int port, boolean secure, String ipAddress) {
        this.host = host;
        this.port = port;
        this.secure = secure;
        this.ipAddress = ipAddress;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public boolean secure() {
        return secure;
    }

    @Override
    public String ipAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return null;
    }
}
