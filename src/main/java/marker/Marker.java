package marker;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

import javax.swing.*;

public class Marker implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Marker");
        MarkerPanel markerPanel = new MarkerPanel();
        MarkerProxyHandler proxyHandler = new MarkerProxyHandler(
                markerPanel::requestHighlightRuleSets,
                markerPanel::responseHighlightRuleSets
        );
        api.userInterface().registerSuiteTab("Marker", markerPanel);
        api.proxy().registerRequestHandler(proxyHandler);
        api.proxy().registerResponseHandler(proxyHandler);
    }
}
