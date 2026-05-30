package marker;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import burp.api.montoya.proxy.http.InterceptedRequest;
import marker.rule.HighlightRuleSet;
import marker.rule.Matchers;
import marker.rule.Operator;
import marker.rule.RequestProperties;
import marker.rule.Rule;
import marker.rule.RulePolarity;
import marker.ui.HighlightColorRenderer;
import marker.ui.RequestMatchType;
import marker.ui.RequestRuleEditorDialog;
import marker.ui.RequestRuleRowModel;
import marker.ui.RequestRuleSetViewModel;
import marker.ui.RuleRelationship;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class MarkerPanel extends JPanel implements ProxyRequestHandler {
    private JPanel requestRuleSetsPanel;
    private final List<HighlightRuleSet<InterceptedRequest>> requestRuleSets = new ArrayList<>();

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

        JTable rulesTable = new JTable(viewModel.getTableModel());
        configureRulesTable(rulesTable);

        JComponent metaPanel = buildRuleSetMetaPanel(viewModel.getRuleSetModel(), panel);
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
        bindTextField(commentField, ruleSetModel::setComment);

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
        requestRuleSetsPanel.add(buildRuleSetPanel(viewModel), "growx");
        refreshRequestRuleSetsPanel();
    }

    private void removeRequestRuleSet(HighlightRuleSet<InterceptedRequest> ruleSetModel, JPanel ruleSetPanel) {
        if (requestRuleSetsPanel == null) {
            return;
        }

        requestRuleSets.remove(ruleSetModel);
        requestRuleSetsPanel.remove(ruleSetPanel);
        refreshRequestRuleSetsPanel();
    }

    private void refreshRequestRuleSetsPanel() {
        requestRuleSetsPanel.revalidate();
        requestRuleSetsPanel.repaint();
    }

    private void addRule(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        RequestRuleRowModel createdRule = RequestRuleEditorDialog.show(this, null);
        if (createdRule == null) {
            return;
        }

        int newIndex = viewModel.getTableModel().addRule(createdRule);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
    }

    private void editRule(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        RequestRuleRowModel existingRule = viewModel.getTableModel().getRule(selectedRow);
        RequestRuleRowModel editedRule = RequestRuleEditorDialog.show(this, existingRule.copy());
        if (editedRule == null) {
            return;
        }

        viewModel.getTableModel().updateRule(selectedRow, editedRule);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
    }

    private void removeRule(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        viewModel.getTableModel().removeRule(selectedRow);
        syncRequestRuleSet(viewModel);
        if (viewModel.getTableModel().getRowCount() > 0) {
            int selectionIndex = Math.min(selectedRow, viewModel.getTableModel().getRowCount() - 1);
            rulesTable.getSelectionModel().setSelectionInterval(selectionIndex, selectionIndex);
        }
    }

    private void moveRuleUp(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow <= 0) {
            return;
        }

        viewModel.getTableModel().moveRule(selectedRow, selectedRow - 1);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
    }

    private void moveRuleDown(RequestRuleSetViewModel viewModel, JTable rulesTable) {
        int selectedRow = rulesTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= viewModel.getTableModel().getRowCount() - 1) {
            return;
        }

        viewModel.getTableModel().moveRule(selectedRow, selectedRow + 1);
        syncRequestRuleSet(viewModel);
        rulesTable.getSelectionModel().setSelectionInterval(selectedRow + 1, selectedRow + 1);
    }

    private void syncRequestRuleSet(RequestRuleSetViewModel viewModel) {
        viewModel.getRuleSetModel().getRuleSet().clear();

        List<Rule<InterceptedRequest, ?, ?>> rules = viewModel.getTableModel().rules().stream()
                .filter(RequestRuleRowModel::isEnabled)
                .map(this::toRequestRule)
                .toList();

        viewModel.getRuleSetModel().getRuleSet().addAll(rules);
    }

    private Rule<InterceptedRequest, ?, ?> toRequestRule(RequestRuleRowModel row) {
        return switch (row.getMatchType()) {
            case DOMAIN -> buildRequestRule(row, RequestProperties.DOMAIN, Matchers.REGEX, row.getCondition());
            case PROTOCOL -> buildRequestRule(
                    row,
                    RequestProperties.IS_SECURE,
                    Matchers.BOOLEAN_EQUALS,
                    "HTTPS".equals(row.getCondition())
            );
            case METHOD -> buildRequestRule(row, RequestProperties.METHOD, Matchers.REGEX, row.getCondition());
            case URL -> buildRequestRule(row, RequestProperties.URL, Matchers.REGEX, row.getCondition());
            case IN_SCOPE -> buildRequestRule(
                    row,
                    RequestProperties.IS_IN_SCOPE,
                    Matchers.BOOLEAN_EQUALS,
                    "Yes".equals(row.getCondition())
            );
            case FILE_EXTENSION ->
                    buildRequestRule(row, RequestProperties.FILE_EXTENSION, Matchers.REGEX, row.getCondition());
            case HAS_PARAMETERS -> buildRequestRule(
                    row,
                    RequestProperties.HAS_PARAMETERS,
                    Matchers.BOOLEAN_EQUALS,
                    "Yes".equals(row.getCondition())
            );
            case COOKIE_NAME ->
                    buildRequestRule(row, RequestProperties.COOKIE_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case COOKIE_VALUE ->
                    buildRequestRule(row, RequestProperties.COOKIE_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case HEADER_NAME ->
                    buildRequestRule(row, RequestProperties.HEADER_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case HEADER_VALUE ->
                    buildRequestRule(row, RequestProperties.HEADER_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case BODY -> buildRequestRule(row, RequestProperties.BODY, Matchers.REGEX, row.getCondition());
            case PARAMETER_NAME ->
                    buildRequestRule(row, RequestProperties.PARAMETER_NAMES, Matchers.ANY_REGEX, row.getCondition());
            case PARAMETER_VALUE ->
                    buildRequestRule(row, RequestProperties.PARAMETER_VALUES, Matchers.ANY_REGEX, row.getCondition());
            case LISTENER_PORT ->
                    buildRequestRule(row, RequestProperties.LISTENER_INTERFACE, Matchers.REGEX, row.getCondition());
        };
    }

    private <M, C> Rule<InterceptedRequest, M, C> buildRequestRule(
            RequestRuleRowModel row,
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

    private void updateEnabledPresentation(AbstractButton enabledButton, boolean enabled) {
        enabledButton.setText(enabled ? "Enabled" : "Disabled");
        enabledButton.setForeground(enabled ? new Color(20, 160, 40) : new Color(210, 30, 30));
    }

    private void bindTextField(JTextField field, Consumer<String> consumer) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                consumer.accept(field.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                consumer.accept(field.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                consumer.accept(field.getText());
            }
        });
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
}
