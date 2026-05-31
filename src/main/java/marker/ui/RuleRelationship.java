package marker.ui;

public enum RuleRelationship {
    MATCHES("Matches"),
    DOES_NOT_MATCH("Does not match"),
    IS("Is"),
    IS_NOT("Is not");

    private final String displayName;

    RuleRelationship(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
