package marker.ui;

import marker.rule.Operator;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class RequestRuleEditorDialog {
    private RequestRuleEditorDialog() {
    }

    public static RequestRuleRowModel show(Component parent, RequestRuleRowModel initialRule) {
        RequestRuleRowModel draft = initialRule == null ? new RequestRuleRowModel() : initialRule;

        JCheckBox enabledBox = new JCheckBox("Enabled", draft.isEnabled());
        JComboBox<Operator> operatorCombo = new JComboBox<>(Operator.values());
        operatorCombo.setSelectedItem(draft.getOperator());

        JComboBox<RequestMatchType> matchTypeCombo = new JComboBox<>(RequestMatchType.values());
        matchTypeCombo.setSelectedItem(draft.getMatchType());

        JComboBox<RuleRelationship> relationshipCombo = new JComboBox<>();
        JComboBox<String> protocolConditionCombo = new JComboBox<>(new String[]{"HTTP", "HTTPS"});
        JComboBox<String> booleanConditionCombo = new JComboBox<>(new String[]{"Yes", "No"});
        JTextField textConditionField = new JTextField(draft.getCondition(), 20);

        JPanel conditionPanel = new JPanel(new CardLayout());
        conditionPanel.add(textConditionField, ConditionInputMode.TEXT.name());
        conditionPanel.add(protocolConditionCombo, ConditionInputMode.PROTOCOL.name());
        conditionPanel.add(booleanConditionCombo, ConditionInputMode.BOOLEAN.name());

        Runnable refreshFormState = () -> {
            RequestMatchType selectedMatchType = (RequestMatchType) matchTypeCombo.getSelectedItem();
            RuleRelationship currentRelationship = (RuleRelationship) relationshipCombo.getSelectedItem();
            DefaultComboBoxModel<RuleRelationship> relationshipModel = new DefaultComboBoxModel<>(
                    selectedMatchType.relationships().toArray(new RuleRelationship[0])
            );
            relationshipCombo.setModel(relationshipModel);
            if (currentRelationship != null && selectedMatchType.relationships().contains(currentRelationship)) {
                relationshipCombo.setSelectedItem(currentRelationship);
            } else {
                relationshipCombo.setSelectedItem(selectedMatchType.relationships().getFirst());
            }

            CardLayout cardLayout = (CardLayout) conditionPanel.getLayout();
            cardLayout.show(conditionPanel, selectedMatchType.conditionInputMode().name());
        };

        matchTypeCombo.addActionListener(event -> refreshFormState.run());
        refreshFormState.run();

        if (draft.getMatchType().conditionInputMode() == ConditionInputMode.PROTOCOL) {
            protocolConditionCombo.setSelectedItem(draft.getCondition().isBlank() ? "HTTPS" : draft.getCondition());
        } else if (draft.getMatchType().conditionInputMode() == ConditionInputMode.BOOLEAN) {
            booleanConditionCombo.setSelectedItem(draft.getCondition().isBlank() ? "Yes" : draft.getCondition());
        }

        JPanel form = new JPanel(new MigLayout(
                "fillx, insets 12, wrap 2, gapx 12, gapy 10",
                "[right][grow]",
                ""
        ));
        addFormRow(form, "Enabled", enabledBox);
        addFormRow(form, "Operator", operatorCombo);
        addFormRow(form, "Match type", matchTypeCombo);
        addFormRow(form, "Relationship", relationshipCombo);
        addFormRow(form, "Condition", conditionPanel);

        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(parent),
                form,
                initialRule == null ? "Add rule" : "Edit rule",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        RequestRuleRowModel updatedRule = new RequestRuleRowModel();
        updatedRule.setEnabled(enabledBox.isSelected());
        updatedRule.setOperator((Operator) operatorCombo.getSelectedItem());
        updatedRule.setMatchType((RequestMatchType) matchTypeCombo.getSelectedItem());
        updatedRule.setRelationship((RuleRelationship) relationshipCombo.getSelectedItem());
        updatedRule.setCondition(switch (updatedRule.getMatchType().conditionInputMode()) {
            case TEXT -> textConditionField.getText();
            case PROTOCOL -> (String) protocolConditionCombo.getSelectedItem();
            case BOOLEAN -> (String) booleanConditionCombo.getSelectedItem();
        });

        return updatedRule;
    }

    private static void addFormRow(JPanel form, String label, JComponent component) {
        form.add(new JLabel(label));
        form.add(component, "growx");
    }
}
