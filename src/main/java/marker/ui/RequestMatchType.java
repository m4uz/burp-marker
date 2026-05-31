package marker.ui;

import java.util.List;

public enum RequestMatchType implements MatchTypeDescriptor {
    DOMAIN("Domain name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    PROTOCOL("Protocol", ConditionInputMode.PROTOCOL, List.of(RuleRelationship.IS, RuleRelationship.IS_NOT)),
    METHOD("HTTP method", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    URL("URL", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    IN_SCOPE("Target scope", ConditionInputMode.BOOLEAN, List.of(RuleRelationship.IS, RuleRelationship.IS_NOT)),
    FILE_EXTENSION("File extension", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    HAS_PARAMETERS("Contains parameters", ConditionInputMode.BOOLEAN, List.of(RuleRelationship.IS, RuleRelationship.IS_NOT)),
    COOKIE_NAME("Cookie name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    COOKIE_VALUE("Cookie value", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    HEADER_NAME("Header name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    HEADER_VALUE("Header value", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    BODY("Body", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    PARAMETER_NAME("Parameter name", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    PARAMETER_VALUE("Parameter value", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH)),
    LISTENER_PORT("Listener port", ConditionInputMode.TEXT, List.of(RuleRelationship.MATCHES, RuleRelationship.DOES_NOT_MATCH));

    private final String displayName;
    private final ConditionInputMode conditionInputMode;
    private final List<RuleRelationship> relationships;

    RequestMatchType(String displayName, ConditionInputMode conditionInputMode, List<RuleRelationship> relationships) {
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
