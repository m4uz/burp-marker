package marker.ui;

import java.util.List;

public enum ResponseMatchType implements MatchTypeDescriptor {
    DOMAIN("Domain name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    PROTOCOL("Protocol", ConditionInputMode.PROTOCOL, List.of(RuleRelationship.IS, RuleRelationship.IS_NOT)),
    METHOD("HTTP method", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    URL("URL", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    IN_SCOPE("Target scope", ConditionInputMode.BOOLEAN, List.of(RuleRelationship.IS, RuleRelationship.IS_NOT)),
    FILE_EXTENSION("File extension", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    HAS_PARAMETERS("Contains parameters", ConditionInputMode.BOOLEAN, List.of(RuleRelationship.IS, RuleRelationship.IS_NOT)),
    REQUEST_HEADER_NAME("Request header name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    REQUEST_HEADER_VALUE("Request header value", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    RESPONSE_COOKIE_NAME("Response cookie name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    RESPONSE_COOKIE_VALUE("Response cookie value", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    RESPONSE_HEADER_NAME("Response header name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    RESPONSE_HEADER_VALUE("Response header value", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    RESPONSE_BODY("Response body", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    PARAMETER_NAME("Parameter name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    PARAMETER_VALUE("Parameter value", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    STATUS_CODE("Status code", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    CONTENT_TYPE("Content type header", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    MIME_TYPE("MIME type", ConditionInputMode.MIME_TYPE, List.of(RuleRelationship.IS, RuleRelationship.IS_NOT)),
    LISTENER_PORT("Listener port", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH));

    private final String displayName;
    private final ConditionInputMode conditionInputMode;
    private final List<RuleRelationship> relationships;

    ResponseMatchType(String displayName, ConditionInputMode conditionInputMode, List<RuleRelationship> relationships) {
        this.displayName = displayName;
        this.conditionInputMode = conditionInputMode;
        this.relationships = relationships;
    }

    @Override
    public ConditionInputMode conditionInputMode() {
        return conditionInputMode;
    }

    @Override
    public List<RuleRelationship> relationships() {
        return relationships;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
