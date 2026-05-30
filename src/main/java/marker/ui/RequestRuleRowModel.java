package marker.ui;

import marker.rule.Operator;

public class RequestRuleRowModel {
    private boolean enabled = true;
    private Operator operator = Operator.OR;
    private RequestMatchType matchType = RequestMatchType.DOMAIN;
    private RuleRelationship relationship = RuleRelationship.MATCHES;
    private String condition = "";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public RequestMatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(RequestMatchType matchType) {
        this.matchType = matchType;
    }

    public RuleRelationship getRelationship() {
        return relationship;
    }

    public void setRelationship(RuleRelationship relationship) {
        this.relationship = relationship;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public RequestRuleRowModel copy() {
        RequestRuleRowModel copy = new RequestRuleRowModel();
        copy.enabled = enabled;
        copy.operator = operator;
        copy.matchType = matchType;
        copy.relationship = relationship;
        copy.condition = condition;
        return copy;
    }
}
