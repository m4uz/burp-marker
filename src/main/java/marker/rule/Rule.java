package marker.rule;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Rule<T, M, C> implements Predicate<T> {
    private final Operator operator;
    private final Function<T, M> extractor;
    private final BiFunction<M, C, Boolean> matcher;
    private final C condition;
    private final boolean negated;

    public Rule(Operator operator, Function<T, M> extractor, BiFunction<M, C, Boolean> matcher, C condition) {
        this.operator = operator;
        this.extractor = extractor;
        this.matcher = matcher;
        this.condition = condition;
        this.negated = false;
    }

    public Rule(Operator operator, Function<T, M> extractor, BiFunction<M, C, Boolean> matcher, C condition, boolean negated) {
        this.operator = operator;
        this.extractor = extractor;
        this.matcher = matcher;
        this.condition = condition;
        this.negated = negated;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean test(T t) {
        return negated != matcher.apply(extractor.apply(t), condition);
    }
}
