package marker.ui;

import burp.api.montoya.proxy.http.InterceptedRequest;
import marker.rule.HighlightRuleSet;

public class RequestRuleSetViewModel {
    private final HighlightRuleSet<InterceptedRequest> ruleSetModel;
    private final RequestRuleTableModel tableModel;

    public RequestRuleSetViewModel(HighlightRuleSet<InterceptedRequest> ruleSetModel) {
        this.ruleSetModel = ruleSetModel;
        this.tableModel = new RequestRuleTableModel();
    }

    public HighlightRuleSet<InterceptedRequest> getRuleSetModel() {
        return ruleSetModel;
    }

    public RequestRuleTableModel getTableModel() {
        return tableModel;
    }
}
