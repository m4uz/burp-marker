package marker;

import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;
import marker.rule.HighlightRuleSet;

import java.util.List;
import java.util.function.Supplier;

public class MarkerProxyHandler implements ProxyRequestHandler, ProxyResponseHandler {
    private final Supplier<List<HighlightRuleSet<InterceptedRequest>>> requestRuleSetsSupplier;
    private final Supplier<List<HighlightRuleSet<InterceptedResponse>>> responseRuleSetsSupplier;

    public MarkerProxyHandler(
            Supplier<List<HighlightRuleSet<InterceptedRequest>>> requestRuleSetsSupplier,
            Supplier<List<HighlightRuleSet<InterceptedResponse>>> responseRuleSetsSupplier
    ) {
        this.requestRuleSetsSupplier = requestRuleSetsSupplier;
        this.responseRuleSetsSupplier = responseRuleSetsSupplier;
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        var annotations = interceptedRequest.annotations();

        for (HighlightRuleSet<InterceptedRequest> ruleSet : requestRuleSetsSupplier.get()) {
            if (!ruleSet.isEnabled()) {
                continue;
            }

            if (ruleSet.getRuleSet().evaluate(interceptedRequest)) {
                annotations = annotations
                        .withHighlightColor(ruleSet.getColor())
                        .withNotes(ruleSet.getComment());
            }
        }

        return ProxyRequestReceivedAction.continueWith(interceptedRequest, annotations);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        var annotations = interceptedResponse.annotations();

        for (HighlightRuleSet<InterceptedResponse> ruleSet : responseRuleSetsSupplier.get()) {
            if (!ruleSet.isEnabled()) {
                continue;
            }

            if (ruleSet.getRuleSet().evaluate(interceptedResponse)) {
                annotations = annotations
                        .withHighlightColor(ruleSet.getColor())
                        .withNotes(ruleSet.getComment());
            }
        }

        return ProxyResponseReceivedAction.continueWith(interceptedResponse, annotations);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }
}
