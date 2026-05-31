package marker.rule;

import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.proxy.http.InterceptedResponse;
import marker.ui.ResponseMatchType;
import marker.ui.RuleRelationship;
import marker.ui.RuleRowModel;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ResponseRuleMapper implements RuleMapper<InterceptedResponse, ResponseMatchType> {
    @Override
    public Rule<InterceptedResponse, ?, ?> toRule(RuleRowModel<ResponseMatchType> row) {
        return switch (row.getMatchType()) {
            case DOMAIN -> buildRule(row, ResponseProperties.REQUEST_DOMAIN, Matchers.REGEX, row.getCondition());
            case PROTOCOL -> buildRule(row, ResponseProperties.REQUEST_IS_SECURE, Matchers.BOOLEAN_EQUALS, "HTTPS".equals(row.getCondition()));
            case METHOD -> buildRule(row, ResponseProperties.REQUEST_METHOD, Matchers.REGEX, row.getCondition());
            case URL -> buildRule(row, ResponseProperties.REQUEST_URL, Matchers.REGEX, row.getCondition());
            case IN_SCOPE -> buildRule(row, ResponseProperties.REQUEST_IS_IN_SCOPE, Matchers.BOOLEAN_EQUALS, "Yes".equals(row.getCondition()));
            case FILE_EXTENSION -> buildRule(row, ResponseProperties.REQUEST_FILE_EXTENSION, Matchers.REGEX, row.getCondition());
            case HAS_PARAMETERS -> buildRule(row, ResponseProperties.REQUEST_HAS_PARAMETERS, Matchers.BOOLEAN_EQUALS, "Yes".equals(row.getCondition()));
            case REQUEST_HEADER_NAME -> buildRule(row, ResponseProperties.REQUEST_HEADER_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case REQUEST_HEADER_VALUE -> buildRule(row, ResponseProperties.REQUEST_HEADER_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case RESPONSE_COOKIE_NAME -> buildRule(row, ResponseProperties.COOKIE_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case RESPONSE_COOKIE_VALUE -> buildRule(row, ResponseProperties.COOKIE_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case RESPONSE_HEADER_NAME -> buildRule(row, ResponseProperties.HEADER_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case RESPONSE_HEADER_VALUE -> buildRule(row, ResponseProperties.HEADER_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case RESPONSE_BODY -> buildRule(row, ResponseProperties.BODY, Matchers.REGEX, row.getCondition());
            case PARAMETER_NAME -> buildRule(row, ResponseProperties.REQUEST_PARAMETER_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case PARAMETER_VALUE -> buildRule(row, ResponseProperties.REQUEST_PARAMETER_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case STATUS_CODE -> buildRule(row, ResponseProperties.STATUS_CODE, Matchers.SHORT_EQUALS, parseShortCondition(row.getCondition()));
            case CONTENT_TYPE -> buildRule(row, ResponseProperties.CONTENT_TYPE, Matchers.REGEX, row.getCondition());
            case MIME_TYPE -> buildRule(row, ResponseProperties.MIME_TYPE, MimeType::equals, MimeType.valueOf(row.getCondition()));
            case LISTENER_PORT -> buildRule(row, ResponseProperties.LISTENER_INTERFACE, Matchers.REGEX, row.getCondition());
        };
    }

    private <M, C> Rule<InterceptedResponse, M, C> buildRule(
            RuleRowModel<ResponseMatchType> row,
            Function<? super InterceptedResponse, M> extractor,
            BiFunction<M, C, Boolean> matcher,
            C condition
    ) {
        return Rule.of(row.getOperator(), extractor, matcher, condition, toRulePolarity(row.getRelationship()));
    }

    private short parseShortCondition(String value) {
        return Short.parseShort(value.trim());
    }

    private RulePolarity toRulePolarity(RuleRelationship relationship) {
        return switch (relationship) {
            case MATCHES, IS -> RulePolarity.MATCH;
            case DOES_NOT_MATCH, IS_NOT -> RulePolarity.NOT_MATCH;
        };
    }
}
