package marker.rule;

import burp.api.montoya.core.HighlightColor;

public class HighlightRuleSet<T> {
    private boolean enabled;
    private HighlightColor color;
    private String comment;
    private final RuleSet<T> ruleSet;

    public HighlightRuleSet() {
        this(true, HighlightColor.RED, "", new RuleSet<>());
    }

    public HighlightRuleSet(boolean enabled, HighlightColor color, String comment, RuleSet<T> ruleSet) {
        this.enabled = enabled;
        this.color = color;
        this.comment = comment;
        this.ruleSet = ruleSet;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HighlightColor getColor() {
        return color;
    }

    public void setColor(HighlightColor color) {
        this.color = color;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public RuleSet<T> getRuleSet() {
        return ruleSet;
    }
}
