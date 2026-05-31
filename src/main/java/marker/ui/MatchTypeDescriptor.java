package marker.ui;

import java.util.List;

public interface MatchTypeDescriptor {
    ConditionInputMode conditionInputMode();

    List<RuleRelationship> relationships();
}
