package marker.ui;

import marker.rule.HighlightRuleSet;

public class RuleSetViewModel<M, T extends Enum<T> & MatchTypeDescriptor> {
    private final HighlightRuleSet<M> ruleSetModel;
    private final RuleTableModel<T> tableModel;

    public RuleSetViewModel(HighlightRuleSet<M> ruleSetModel) {
        this.ruleSetModel = ruleSetModel;
        this.tableModel = new RuleTableModel<>();
    }

    public HighlightRuleSet<M> getRuleSetModel() {
        return ruleSetModel;
    }

    public RuleTableModel<T> getTableModel() {
        return tableModel;
    }
}
