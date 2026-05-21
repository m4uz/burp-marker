package m4uz;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

import javax.swing.*;

public class Marker implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Marker");
        api.userInterface().registerSuiteTab("Marker", new JPanel());
    }
}
