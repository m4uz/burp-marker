package marker.rule;

import marker.ui.MatchTypeDescriptor;
import marker.ui.RuleRowModel;

public interface RuleMapper<M, T extends Enum<T> & MatchTypeDescriptor> {
    Rule<M, ?, ?> toRule(RuleRowModel<T> row);
}
