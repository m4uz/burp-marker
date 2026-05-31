package marker.ui;

import marker.rule.Operator;

public class RuleRowModel<T extends Enum<T> & MatchTypeDescriptor> {
    private boolean enabled = true;
    private Operator operator = Operator.OR;
    private T matchType;
    private RuleRelationship relationship = RuleRelationship.MATCHES;
    private String condition = "";

    public RuleRowModel(T defaultMatchType) {
        this.matchType = defaultMatchType;
    }

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

    public T getMatchType() {
        return matchType;
    }

    public void setMatchType(T matchType) {
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

    public RuleRowModel<T> copy() {
        RuleRowModel<T> copy = new RuleRowModel<>(matchType);
        copy.enabled = enabled;
        copy.operator = operator;
        copy.matchType = matchType;
        copy.relationship = relationship;
        copy.condition = condition;
        return copy;
    }
}
