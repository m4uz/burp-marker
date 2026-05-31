package marker.ui;

import marker.rule.HighlightRuleSet;

import java.util.ArrayList;
import java.util.List;

public class RuleSetEditorModel<M, T extends Enum<T> & MatchTypeDescriptor> {
    private final HighlightRuleSet<M> ruleSet;
    private final List<RuleRowModel<T>> rules;
    private final RuleTableModel<T> tableModel;

    public RuleSetEditorModel(HighlightRuleSet<M> ruleSet) {
        this.ruleSet = ruleSet;
        this.rules = new ArrayList<>();
        this.tableModel = new RuleTableModel<>(rules);
    }

    public HighlightRuleSet<M> getRuleSet() {
        return ruleSet;
    }

    public List<RuleRowModel<T>> getRules() {
        return List.copyOf(rules);
    }

    List<RuleRowModel<T>> mutableRules() {
        return rules;
    }

    public RuleTableModel<T> getTableModel() {
        return tableModel;
    }
}
