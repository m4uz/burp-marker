package marker.rule;

import java.util.ArrayList;
import java.util.List;

public class RuleSet<T> {
    private final List<Rule<T, ?, ?>> rules = new ArrayList<>();

    public void add(Rule<T, ?, ?> rule) {
        this.rules.add(rule);
    }

    public void addAll(List<Rule<T, ?, ?>> rules) {
        this.rules.addAll(rules);
    }

    public boolean evaluate(T object) {
        boolean match = false;
        for (int i = 0; i < this.rules.size(); i++) {
            boolean ruleMatch = this.rules.get(i).test(object);

            if (i == 0) {
                match = ruleMatch;
                continue;
            }

            if (rules.get(i).getOperator() == Operator.AND) {
                match = match && ruleMatch;
            } else if (rules.get(i).getOperator() == Operator.OR) {
                match = match || ruleMatch;
            }
        }
        return match;
    }
}
