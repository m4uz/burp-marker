package marker;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import net.miginfocom.swing.MigLayout;
import burp.api.montoya.proxy.http.InterceptedRequest;
import marker.rule.HighlightRuleSet;
import marker.rule.Matchers;
import marker.rule.Operator;
import marker.rule.RequestProperties;
import marker.rule.Rule;
import marker.rule.RulePolarity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static burp.api.montoya.core.HighlightColor.RED;

public class MarkerPanel extends JPanel implements ProxyRequestHandler {
    private JPanel requestRuleSetsPanel;
    private final List<HighlightRuleSet<InterceptedRequest>> requestRuleSets = new ArrayList<>();
    private final List<RequestRuleSetViewModel> requestRuleSetViewModels = new ArrayList<>();

    public MarkerPanel() {
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Request rules", buildRequestRulesTab());
        tabs.addTab("Response rules", buildResponseRulesTab());

        add(tabs, BorderLayout.CENTER);
    }

    private JComponent buildRequestRulesTab() {
        JPanel content = new JPanel(new MigLayout(
                "fill, insets 16, wrap 1, gapy 12",
                "[grow]",
                "[][][][grow]push"
        ));

        JLabel title = new JLabel("Request highlighting rules");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        content.add(title, "growx");

        JButton addRuleSetButton = new JButton("Add ruleset");
        addRuleSetButton.addActionListener(event -> addRequestRuleSet());
        content.add(addRuleSetButton, "split 2, wrap");

        requestRuleSetsPanel = new JPanel(new MigLayout(
                "fillx, insets 0, wrap 1, gapy 16",
                "[grow]",
                ""
        ));
        requestRuleSetsPanel.setOpaque(false);
        addRequestRuleSet();
        content.add(requestRuleSetsPanel, "growx, top");
        content.add(Box.createVerticalGlue(), "growy, pushy");

        JPanel wrapper = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent buildResponseRulesTab() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 16", "[grow]", "[grow]"));
        JLabel placeholder = new JLabel("Response highlighting rules");
        placeholder.setFont(placeholder.getFont().deriveFont(Font.BOLD, 28f));
        panel.add(placeholder, "dock north");
        return panel;
    }

    private JComponent buildRuleSetPanel(RequestRuleSetViewModel viewModel) {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0, wrap 2, gapx 16, gapy 10",
                "[140!][grow]",
                "[][]"
        ));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")),
                new EmptyBorder(16, 0, 0, 0)
        ));

        JTable rulesTable = new JTable(viewModel.tableModel);
        configureRulesTable(rulesTable);

        JComponent metaPanel = buildRuleSetMetaPanel(viewModel.ruleSetModel, panel);
        JComponent actionsPanel = buildRuleActionsPanel(viewModel, rulesTable);
        JComponent rulesTableScrollPane = buildRulesTable(rulesTable, actionsPanel.getPreferredSize().height);

        panel.add(new JPanel(), "growx");
        panel.add(metaPanel, "growx");
        panel.add(actionsPanel, "top");
        panel.add(rulesTableScrollPane, "growx, top");

        return panel;
    }

    private JComponent buildRuleSetMetaPanel(HighlightRuleSet<InterceptedRequest> ruleSetModel, JPanel ruleSetPanel) {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0, gapx 12",
                "[pref!][pref!][110!][pref!][220!][grow][pref!]",
                "[]"
        ));

        JCheckBox enabled = new JCheckBox("Enabled", ruleSetModel.isEnabled());
        enabled.setFont(enabled.getFont().deriveFont(Font.BOLD, 13f));
        updateEnabledPresentation(enabled, ruleSetModel.isEnabled());
        enabled.addActionListener(event -> {
            ruleSetModel.setEnabled(enabled.isSelected());
            updateEnabledPresentation(enabled, enabled.isSelected());
        });

        JLabel colorLabel = new JLabel("Color");
        JComboBox<HighlightColor> colorCombo = new JComboBox<>(HighlightColor.values());
        colorCombo.setRenderer(new HighlightColorRenderer());
        colorCombo.setSelectedItem(ruleSetModel.getColor());
        colorCombo.addActionListener(event -> ruleSetModel.setColor((HighlightColor) colorCombo.getSelectedItem()));
        JLabel commentLabel = new JLabel("Comment");
        JTextField commentField = new JTextField(ruleSetModel.getComment());
        commentField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateComment();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateComment();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateComment();
            }

            private void updateComment() {
                ruleSetModel.setComment(commentField.getText());
            }
        });
        JButton deleteRuleSetButton = new JButton("Delete ruleset");
        deleteRuleSetButton.addActionListener(event -> removeRequestRuleSet(ruleSetModel, ruleSetPanel));

        panel.add(enabled);
        panel.add(colorLabel);
        panel.add(colorCombo, "w 110!");
        panel.add(commentLabel);
        panel.add(commentField, "w 220!");
        panel.add(new JPanel(), "growx");
        panel.add(deleteRuleSetButton, "align right");

        return panel;
    }

    private JComponent buildRuleActionsPanel(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        JPanel panel = new JPanel(new MigLayout(
                "insets 0, wrap 1, gapy 8",
                "[110!]",
                "[][][][][]"
        ));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(event -> addRule(viewModel, rulesTable));
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(event -> editRule(viewModel, rulesTable));
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(event -> removeRule(viewModel, rulesTable));
        JButton upButton = new JButton("Up");
        upButton.addActionListener(event -> moveRuleUp(viewModel, rulesTable));
        JButton downButton = new JButton("Down");
        downButton.addActionListener(event -> moveRuleDown(viewModel, rulesTable));

        panel.add(addButton, "growx");
        panel.add(editButton, "growx");
        panel.add(removeButton, "growx");
        panel.add(upButton, "growx");
        panel.add(downButton, "growx");

        return panel;
    }

    private void configureRulesTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JComponent buildRulesTable(JTable table, int preferredHeight) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, preferredHeight));
        return scrollPane;
    }

    private void addRequestRuleSet() {
        if (requestRuleSetsPanel == null) {
            return;
        }

        HighlightRuleSet<InterceptedRequest> ruleSetModel = new HighlightRuleSet<>();
        RequestRuleSetViewModel viewModel = new RequestRuleSetViewModel(ruleSetModel);
        requestRuleSets.add(ruleSetModel);
        requestRuleSetViewModels.add(viewModel);
        requestRuleSetsPanel.add(buildRuleSetPanel(viewModel), "growx");
        requestRuleSetsPanel.revalidate();
        requestRuleSetsPanel.repaint();
    }

    private void removeRequestRuleSet(HighlightRuleSet<InterceptedRequest> ruleSetModel, JPanel ruleSetPanel) {
        if (requestRuleSetsPanel == null) {
            return;
        }

        requestRuleSets.remove(ruleSetModel);
        requestRuleSetViewModels.removeIf(viewModel -> viewModel.ruleSetModel == ruleSetModel);
        requestRuleSetsPanel.remove(ruleSetPanel);
        requestRuleSetsPanel.revalidate();
        requestRuleSetsPanel.repaint();
    }

    private void addRule(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        RequestRuleRowModel createdRule = showRuleEditorDialog(null);
        if (createdRule == null) {
            return;
        }

        int newIndex = viewModel.tableModel.addRule(createdRule);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
    }

    private void editRule(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        RequestRuleRowModel existingRule = viewModel.tableModel.getRule(selectedRow);
        RequestRuleRowModel editedRule = showRuleEditorDialog(existingRule.copy());
        if (editedRule == null) {
            return;
        }

        viewModel.tableModel.updateRule(selectedRow, editedRule);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
    }

    private void removeRule(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        viewModel.tableModel.removeRule(selectedRow);
        syncRequestRuleSet(viewModel);
        if (viewModel.tableModel.getRowCount() > 0) {
            int selectionIndex = Math.min(selectedRow, viewModel.tableModel.getRowCount() - 1);
            rulesTable.getSelectionModel().setSelectionInterval(selectionIndex, selectionIndex);
        }
    }

    private void moveRuleUp(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow <= 0) {
            return;
        }

        viewModel.tableModel.moveRule(selectedRow, selectedRow - 1);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
    }

    private void moveRuleDown(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= viewModel.tableModel.getRowCount() - 1) {
            return;
        }

        viewModel.tableModel.moveRule(selectedRow, selectedRow + 1);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(selectedRow + 1, selectedRow + 1);
    }

    private void syncRequestRuleSet(RequestRuleSetViewModel viewModel) {
        viewModel.ruleSetModel.getRuleSet().clear();

        List<Rule<InterceptedRequest, ?, ?>> rules = viewModel.tableModel.rules().stream()
                .filter(RequestRuleRowModel::enabled)
                .map(this::toRequestRule)
                .toList();

        viewModel.ruleSetModel.getRuleSet().addAll(rules);
    }

    private Rule<InterceptedRequest, ?, ?> toRequestRule(RequestRuleRowModel row) {
        return switch (row.matchType) {
            case DOMAIN -> buildRequestRule(row, RequestProperties.DOMAIN, Matchers.REGEX, row.condition);
            case PROTOCOL -> buildRequestRule(
                    row,
                    RequestProperties.IS_SECURE,
                    Matchers.BOOLEAN_EQUALS,
                    "HTTPS".equals(row.condition)
            );
            case METHOD -> buildRequestRule(row, RequestProperties.METHOD, Matchers.REGEX, row.condition);
            case URL -> buildRequestRule(row, RequestProperties.URL, Matchers.REGEX, row.condition);
            case IN_SCOPE -> buildRequestRule(
                    row,
                    RequestProperties.IS_IN_SCOPE,
                    Matchers.BOOLEAN_EQUALS,
                    "Yes".equals(row.condition)
            );
            case FILE_EXTENSION -> buildRequestRule(row, RequestProperties.FILE_EXTENSION, Matchers.REGEX, row.condition);
            case HAS_PARAMETERS -> buildRequestRule(
                    row,
                    RequestProperties.HAS_PARAMETERS,
                    Matchers.BOOLEAN_EQUALS,
                    "Yes".equals(row.condition)
            );
            case COOKIE_NAME -> buildRequestRule(row, RequestProperties.COOKIE_NAMES, Matchers.ANY_REGEX, row.condition);
            case COOKIE_VALUE -> buildRequestRule(row, RequestProperties.COOKIE_VALUES, Matchers.ANY_REGEX, row.condition);
            case HEADER_NAME -> buildRequestRule(row, RequestProperties.HEADER_NAMES, Matchers.ANY_REGEX, row.condition);
            case HEADER_VALUE -> buildRequestRule(row, RequestProperties.HEADER_VALUES, Matchers.ANY_REGEX, row.condition);
            case BODY -> buildRequestRule(row, RequestProperties.BODY, Matchers.REGEX, row.condition);
            case PARAMETER_NAME -> buildRequestRule(row, RequestProperties.PARAMETER_NAMES, Matchers.ANY_REGEX, row.condition);
            case PARAMETER_VALUE -> buildRequestRule(row, RequestProperties.PARAMETER_VALUES, Matchers.ANY_REGEX, row.condition);
            case LISTENER_PORT -> buildRequestRule(row, RequestProperties.LISTENER_INTERFACE, Matchers.REGEX, row.condition);
        };
    }

    private <M, C> Rule<InterceptedRequest, M, C> buildRequestRule(
            RequestRuleRowModel row,
            Function<? super InterceptedRequest, M> extractor,
            BiFunction<M, C, Boolean> matcher,
            C condition
    ) {
        return Rule.of(row.operator, extractor, matcher, condition, toRulePolarity(row.relationship));
    }

    private RulePolarity toRulePolarity(RuleRelationship relationship) {
        return switch (relationship) {
            case MATCHES, IS -> RulePolarity.MATCH;
            case DOES_NOT_MATCH, IS_NOT -> RulePolarity.NOT_MATCH;
        };
    }

    private RequestRuleRowModel showRuleEditorDialog(RequestRuleRowModel initialRule) {
        RequestRuleRowModel draft = initialRule == null ? new RequestRuleRowModel() : initialRule;

        JCheckBox enabledBox = new JCheckBox("Enabled", draft.enabled);
        JComboBox<Operator> operatorCombo = new JComboBox<>(Operator.values());
        operatorCombo.setSelectedItem(draft.operator);
        JComboBox<RequestMatchType> matchTypeCombo = new JComboBox<>(RequestMatchType.values());
        matchTypeCombo.setSelectedItem(draft.matchType);
        JComboBox<RuleRelationship> relationshipCombo = new JComboBox<>();
        JComboBox<String> protocolConditionCombo = new JComboBox<>(new String[]{"HTTP", "HTTPS"});
        JComboBox<String> booleanConditionCombo = new JComboBox<>(new String[]{"Yes", "No"});
        JTextField textConditionField = new JTextField(draft.condition, 20);

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

        if (draft.matchType.conditionInputMode() == ConditionInputMode.PROTOCOL) {
            protocolConditionCombo.setSelectedItem(draft.condition.isBlank() ? "HTTPS" : draft.condition);
        } else if (draft.matchType.conditionInputMode() == ConditionInputMode.BOOLEAN) {
            booleanConditionCombo.setSelectedItem(draft.condition.isBlank() ? "Yes" : draft.condition);
        }

        JPanel form = new JPanel(new MigLayout(
                "fillx, insets 12, wrap 2, gapx 12, gapy 10",
                "[right][grow]",
                ""
        ));
        form.add(new JLabel("Enabled"));
        form.add(enabledBox);
        form.add(new JLabel("Operator"));
        form.add(operatorCombo, "growx");
        form.add(new JLabel("Match type"));
        form.add(matchTypeCombo, "growx");
        form.add(new JLabel("Relationship"));
        form.add(relationshipCombo, "growx");
        form.add(new JLabel("Condition"));
        form.add(conditionPanel, "growx");

        int result = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                form,
                initialRule == null ? "Add rule" : "Edit rule",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        RequestRuleRowModel updatedRule = new RequestRuleRowModel();
        updatedRule.enabled = enabledBox.isSelected();
        updatedRule.operator = (Operator) operatorCombo.getSelectedItem();
        updatedRule.matchType = (RequestMatchType) matchTypeCombo.getSelectedItem();
        updatedRule.relationship = (RuleRelationship) relationshipCombo.getSelectedItem();
        updatedRule.condition = switch (updatedRule.matchType.conditionInputMode()) {
            case TEXT -> textConditionField.getText();
            case PROTOCOL -> (String) protocolConditionCombo.getSelectedItem();
            case BOOLEAN -> (String) booleanConditionCombo.getSelectedItem();
        };

        return updatedRule;
    }

    private void updateEnabledPresentation(AbstractButton enabledButton, boolean enabled) {
        enabledButton.setText(enabled ? "Enabled" : "Disabled");
        enabledButton.setForeground(enabled ? new Color(20, 160, 40) : new Color(210, 30, 30));
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        var annotations = interceptedRequest.annotations();

        for (HighlightRuleSet<InterceptedRequest> ruleSet : requestRuleSets) {
            if (!ruleSet.isEnabled()) {
                continue;
            }

            if (ruleSet.getRuleSet().evaluate(interceptedRequest)) {
                annotations = annotations
                        .withHighlightColor(ruleSet.getColor())
                        .withNotes(ruleSet.getComment());
            }
        }

        return ProxyRequestReceivedAction.continueWith(interceptedRequest, annotations);
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        return ProxyRequestToBeSentAction.continueWith(interceptedRequest);
    }

    private static final class HighlightColorRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof HighlightColor highlightColor) {
                label.setText(highlightColor.displayName());
                label.setIcon(new ColorSwatchIcon(toAwtColor(highlightColor)));
            }

            return label;
        }
    }

    private static final class ColorSwatchIcon implements Icon {
        private final Color color;

        private ColorSwatchIcon(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
            g.setColor(color);
            g.fillRect(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
        }

        @Override
        public int getIconWidth() {
            return 12;
        }

        @Override
        public int getIconHeight() {
            return 12;
        }
    }

    private static Color toAwtColor(HighlightColor highlightColor) {
        return switch (highlightColor) {
            case RED -> new Color(220, 53, 69);
            case ORANGE -> new Color(253, 126, 20);
            case YELLOW -> new Color(255, 193, 7);
            case GREEN -> new Color(40, 167, 69);
            case CYAN -> new Color(23, 162, 184);
            case BLUE -> new Color(0, 123, 255);
            case PINK -> new Color(232, 62, 140);
            case MAGENTA -> new Color(156, 39, 176);
            case GRAY -> new Color(108, 117, 125);
            case NONE -> Color.WHITE;
        };
    }

    private static final class RequestRuleSetViewModel {
        private final HighlightRuleSet<InterceptedRequest> ruleSetModel;
        private final RequestRuleTableModel tableModel = new RequestRuleTableModel();

        private RequestRuleSetViewModel(HighlightRuleSet<InterceptedRequest> ruleSetModel) {
            this.ruleSetModel = ruleSetModel;
        }
    }

    private static final class RequestRuleTableModel extends AbstractTableModel {
        private static final String[] COLUMNS = {
                "Enabled", "Operator", "Match type", "Relationship", "Condition"
        };

        private final List<RequestRuleRowModel> rules = new ArrayList<>();

        @Override
        public int getRowCount() {
            return rules.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RequestRuleRowModel rule = rules.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> rule.enabled ? "Enabled" : "Disabled";
                case 1 -> rule.operator;
                case 2 -> rule.matchType;
                case 3 -> rule.relationship;
                case 4 -> rule.condition;
                default -> "";
            };
        }

        private int addRule(RequestRuleRowModel rule) {
            rules.add(rule);
            int index = rules.size() - 1;
            fireTableRowsInserted(index, index);
            return index;
        }

        private RequestRuleRowModel getRule(int rowIndex) {
            return rules.get(rowIndex);
        }

        private void updateRule(int rowIndex, RequestRuleRowModel updatedRule) {
            rules.set(rowIndex, updatedRule);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }

        private void removeRule(int rowIndex) {
            rules.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

        private void moveRule(int fromIndex, int toIndex) {
            RequestRuleRowModel movedRule = rules.remove(fromIndex);
            rules.add(toIndex, movedRule);
            fireTableDataChanged();
        }

        private List<RequestRuleRowModel> rules() {
            return List.copyOf(rules);
        }
    }

    private static final class RequestRuleRowModel {
        private boolean enabled = true;
        private Operator operator = Operator.OR;
        private RequestMatchType matchType = RequestMatchType.DOMAIN;
        private RuleRelationship relationship = RuleRelationship.MATCHES;
        private String condition = "";

        private boolean enabled() {
            return enabled;
        }

        private RequestRuleRowModel copy() {
            RequestRuleRowModel copy = new RequestRuleRowModel();
            copy.enabled = enabled;
            copy.operator = operator;
            copy.matchType = matchType;
            copy.relationship = relationship;
            copy.condition = condition;
            return copy;
        }
    }

    private enum RequestMatchType {
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

        public ConditionInputMode conditionInputMode() {
            return conditionInputMode;
        }

        public List<RuleRelationship> relationships() {
            return relationships;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private enum RuleRelationship {
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

    private enum ConditionInputMode {
        TEXT,
        BOOLEAN,
        PROTOCOL
    }
}
