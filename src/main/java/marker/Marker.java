package marker;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

import javax.swing.*;

public class Marker implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Marker");
        MarkerPanel markerPanel = new MarkerPanel();
        api.userInterface().registerSuiteTab("Marker", markerPanel);
        api.proxy().registerRequestHandler(markerPanel);
    }
}
