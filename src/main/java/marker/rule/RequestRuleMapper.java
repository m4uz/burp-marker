package marker.rule;

import burp.api.montoya.proxy.http.InterceptedRequest;
import marker.ui.RequestMatchType;
import marker.ui.RuleRelationship;
import marker.ui.RuleRowModel;

import java.util.function.BiFunction;
import java.util.function.Function;

public class RequestRuleMapper implements RuleMapper<InterceptedRequest, RequestMatchType> {
    @Override
    public Rule<InterceptedRequest, ?, ?> toRule(RuleRowModel<RequestMatchType> row) {
        return switch (row.getMatchType()) {
            case DOMAIN -> buildRule(row, RequestProperties.DOMAIN, Matchers.REGEX, row.getCondition());
            case PROTOCOL -> buildRule(row, RequestProperties.IS_SECURE, Matchers.BOOLEAN_EQUALS, "HTTPS".equals(row.getCondition()));
            case METHOD -> buildRule(row, RequestProperties.METHOD, Matchers.REGEX, row.getCondition());
            case URL -> buildRule(row, RequestProperties.URL, Matchers.REGEX, row.getCondition());
            case IN_SCOPE -> buildRule(row, RequestProperties.IS_IN_SCOPE, Matchers.BOOLEAN_EQUALS, "Yes".equals(row.getCondition()));
            case FILE_EXTENSION -> buildRule(row, RequestProperties.FILE_EXTENSION, Matchers.REGEX, row.getCondition());
            case HAS_PARAMETERS -> buildRule(row, RequestProperties.HAS_PARAMETERS, Matchers.BOOLEAN_EQUALS, "Yes".equals(row.getCondition()));
            case COOKIE_NAME -> buildRule(row, RequestProperties.COOKIE_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case COOKIE_VALUE -> buildRule(row, RequestProperties.COOKIE_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case HEADER_NAME -> buildRule(row, RequestProperties.HEADER_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case HEADER_VALUE -> buildRule(row, RequestProperties.HEADER_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case BODY -> buildRule(row, RequestProperties.BODY, Matchers.REGEX, row.getCondition());
            case PARAMETER_NAME -> buildRule(row, RequestProperties.PARAMETER_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case PARAMETER_VALUE -> buildRule(row, RequestProperties.PARAMETER_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case LISTENER_PORT -> buildRule(row, RequestProperties.LISTENER_INTERFACE, Matchers.REGEX, row.getCondition());
        };
    }

    private <M, C> Rule<InterceptedRequest, M, C> buildRule(
            RuleRowModel<RequestMatchType> row,
            Function<? super InterceptedRequest, M> extractor,
            BiFunction<M, C, Boolean> matcher,
            C condition
    ) {
        return Rule.of(row.getOperator(), extractor, matcher, condition, toRulePolarity(row.getRelationship()));
    }

    private RulePolarity toRulePolarity(RuleRelationship relationship) {
        return switch (relationship) {
            case MATCHES, IS -> RulePolarity.MATCH;
            case DOES_NOT_MATCH, IS_NOT -> RulePolarity.NOT_MATCH;
        };
    }
}
