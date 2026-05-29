package marker.rule;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Rule<T, M, C> implements Predicate<T> {
    private final Operator operator;
    private final Function<T, M> extractor;
    private final BiFunction<M, C, Boolean> matcher;
    private final C condition;
    private final RulePolarity polarity;

    public Rule(Operator operator, Function<T, M> extractor, BiFunction<M, C, Boolean> matcher, C condition) {
        this(operator, extractor, matcher, condition, RulePolarity.MATCH);
    }

    public Rule(
            Operator operator,
            Function<T, M> extractor,
            BiFunction<M, C, Boolean> matcher,
            C condition,
            RulePolarity polarity
    ) {
        this.operator = operator;
        this.extractor = extractor;
        this.matcher = matcher;
        this.condition = condition;
        this.polarity = polarity;
    }

    public static <T, M, C> Rule<T, M, C> of(
            Operator operator,
            Function<? super T, M> property,
            BiFunction<M, C, Boolean> matcher,
            C condition,
            RulePolarity polarity
    ) {
        return new Rule<>(operator, property::apply, matcher, condition, polarity);
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean test(T t) {
        boolean matches = matcher.apply(extractor.apply(t), condition);
        return polarity == RulePolarity.MATCH ? matches : !matches;
    }
}
