package marker;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.http.message.MimeType;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;
import burp.api.montoya.proxy.http.ProxyResponseHandler;
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction;
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction;
import marker.rule.HighlightRuleSet;
import marker.rule.Matchers;
import marker.rule.RequestProperties;
import marker.rule.ResponseProperties;
import marker.rule.Rule;
import marker.rule.RulePolarity;
import marker.ui.HighlightColorRenderer;
import marker.ui.MatchTypeDescriptor;
import marker.ui.RequestMatchType;
import marker.ui.ResponseMatchType;
import marker.ui.RuleEditorDialog;
import marker.ui.RuleRelationship;
import marker.ui.RuleRowModel;
import marker.ui.RuleSetViewModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class MarkerPanel extends JPanel implements ProxyRequestHandler, ProxyResponseHandler {
    private JPanel requestRuleSetsPanel;
    private JPanel responseRuleSetsPanel;

    private final List<HighlightRuleSet<InterceptedRequest>> requestRuleSets = new ArrayList<>();
    private final List<HighlightRuleSet<InterceptedResponse>> responseRuleSets = new ArrayList<>();

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
        requestRuleSetsPanel = createRuleSetsPanel();
        addRequestRuleSet();
        return buildRulesTab("Request highlighting rules", requestRuleSetsPanel, this::addRequestRuleSet);
    }

    private JComponent buildResponseRulesTab() {
        responseRuleSetsPanel = createRuleSetsPanel();
        addResponseRuleSet();
        return buildRulesTab("Response highlighting rules", responseRuleSetsPanel, this::addResponseRuleSet);
    }

    private JPanel createRuleSetsPanel() {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0, wrap 1, gapy 16",
                "[grow]",
                ""
        ));
        panel.setOpaque(false);
        return panel;
    }

    private JComponent buildRulesTab(String titleText, JPanel ruleSetsPanel, Runnable addRuleSetAction) {
        JPanel content = new JPanel(new MigLayout(
                "fill, insets 16, wrap 1, gapy 12",
                "[grow]",
                "[][][][grow]push"
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        content.add(title, "growx");

        JButton addRuleSetButton = new JButton("Add ruleset");
        addRuleSetButton.addActionListener(event -> addRuleSetAction.run());
        content.add(addRuleSetButton, "split 2, wrap");

        content.add(ruleSetsPanel, "growx, top");
        content.add(Box.createVerticalGlue(), "growy, pushy");

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private <M, T extends Enum<T> & MatchTypeDescriptor> JComponent buildRuleSetPanel(
            RuleSetViewModel<M, T> viewModel,
            Consumer<JPanel> deleteRuleSetAction,
            Consumer<RuleSetViewModel<M, T>> addRuleAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> editRuleAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> removeRuleAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> moveUpAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> moveDownAction
    ) {
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

        JComponent metaPanel = buildRuleSetMetaPanel(viewModel.getRuleSetModel(), panel, deleteRuleSetAction);
        JComponent actionsPanel = buildRuleActionsPanel(
                viewModel,
                rulesTable,
                addRuleAction,
                editRuleAction,
                removeRuleAction,
                moveUpAction,
                moveDownAction
        );
        JComponent rulesTableScrollPane = buildRulesTable(rulesTable, actionsPanel.getPreferredSize().height);

        panel.add(new JPanel(), "growx");
        panel.add(metaPanel, "growx");
        panel.add(actionsPanel, "top");
        panel.add(rulesTableScrollPane, "growx, top");

        return panel;
    }

    private <M> JComponent buildRuleSetMetaPanel(
            HighlightRuleSet<M> ruleSetModel,
            JPanel ruleSetPanel,
            Consumer<JPanel> deleteRuleSetAction
    ) {
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
        deleteRuleSetButton.addActionListener(event -> deleteRuleSetAction.accept(ruleSetPanel));

        panel.add(enabled);
        panel.add(colorLabel);
        panel.add(colorCombo, "w 110!");
        panel.add(commentLabel);
        panel.add(commentField, "w 220!");
        panel.add(new JPanel(), "growx");
        panel.add(deleteRuleSetButton, "align right");

        return panel;
    }

    private <M, T extends Enum<T> & MatchTypeDescriptor> JComponent buildRuleActionsPanel(
            RuleSetViewModel<M, T> viewModel,
            JTable rulesTable,
            Consumer<RuleSetViewModel<M, T>> addRuleAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> editRuleAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> removeRuleAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> moveUpAction,
            BiConsumer<RuleSetViewModel<M, T>, Integer> moveDownAction
    ) {
        JPanel panel = new JPanel(new MigLayout(
                "insets 0, wrap 1, gapy 8",
                "[110!]",
                "[][][][][]"
        ));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(event -> {
            addRuleAction.accept(viewModel);
            int newIndex = viewModel.getTableModel().getRowCount() - 1;
            if (newIndex >= 0) {
                rulesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
            }
        });

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0) {
                editRuleAction.accept(viewModel, selectedRow);
                rulesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
            }
        });

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0) {
                removeRuleAction.accept(viewModel, selectedRow);
                if (viewModel.getTableModel().getRowCount() > 0) {
                    int selectionIndex = Math.min(selectedRow, viewModel.getTableModel().getRowCount() - 1);
                    rulesTable.getSelectionModel().setSelectionInterval(selectionIndex, selectionIndex);
                }
            }
        });

        JButton upButton = new JButton("Up");
        upButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow > 0) {
                moveUpAction.accept(viewModel, selectedRow);
                rulesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
            }
        });

        JButton downButton = new JButton("Down");
        downButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < viewModel.getTableModel().getRowCount() - 1) {
                moveDownAction.accept(viewModel, selectedRow);
                rulesTable.getSelectionModel().setSelectionInterval(selectedRow + 1, selectedRow + 1);
            }
        });

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
        RuleSetViewModel<InterceptedRequest, RequestMatchType> viewModel = new RuleSetViewModel<>(ruleSetModel);
        requestRuleSets.add(ruleSetModel);
        requestRuleSetsPanel.add(buildRuleSetPanel(
                viewModel,
                panel -> removeRequestRuleSet(ruleSetModel, panel),
                this::addRequestRule,
                this::editRequestRule,
                this::removeRequestRule,
                this::moveRequestRuleUp,
                this::moveRequestRuleDown
        ), "growx");
        refreshPanel(requestRuleSetsPanel);
    }

    private void removeRequestRuleSet(HighlightRuleSet<InterceptedRequest> ruleSetModel, JPanel ruleSetPanel) {
        requestRuleSets.remove(ruleSetModel);
        requestRuleSetsPanel.remove(ruleSetPanel);
        refreshPanel(requestRuleSetsPanel);
    }

    private void addResponseRuleSet() {
        if (responseRuleSetsPanel == null) {
            return;
        }

        HighlightRuleSet<InterceptedResponse> ruleSetModel = new HighlightRuleSet<>();
        RuleSetViewModel<InterceptedResponse, ResponseMatchType> viewModel = new RuleSetViewModel<>(ruleSetModel);
        responseRuleSets.add(ruleSetModel);
        responseRuleSetsPanel.add(buildRuleSetPanel(
                viewModel,
                panel -> removeResponseRuleSet(ruleSetModel, panel),
                this::addResponseRule,
                this::editResponseRule,
                this::removeResponseRule,
                this::moveResponseRuleUp,
                this::moveResponseRuleDown
        ), "growx");
        refreshPanel(responseRuleSetsPanel);
    }

    private void removeResponseRuleSet(HighlightRuleSet<InterceptedResponse> ruleSetModel, JPanel ruleSetPanel) {
        responseRuleSets.remove(ruleSetModel);
        responseRuleSetsPanel.remove(ruleSetPanel);
        refreshPanel(responseRuleSetsPanel);
    }

    private void refreshPanel(JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }

    private void addRequestRule(RuleSetViewModel<InterceptedRequest, RequestMatchType> viewModel) {
        RuleRowModel<RequestMatchType> createdRule = RuleEditorDialog.show(this, null, RequestMatchType.values());
        if (createdRule != null) {
            viewModel.getTableModel().addRule(createdRule);
            syncRequestRuleSet(viewModel);
        }
    }

    private void editRequestRule(RuleSetViewModel<InterceptedRequest, RequestMatchType> viewModel, int selectedRow) {
        RuleRowModel<RequestMatchType> existingRule = viewModel.getTableModel().getRule(selectedRow);
        RuleRowModel<RequestMatchType> editedRule = RuleEditorDialog.show(this, existingRule.copy(), RequestMatchType.values());
        if (editedRule != null) {
            viewModel.getTableModel().updateRule(selectedRow, editedRule);
            syncRequestRuleSet(viewModel);
        }
    }

    private void removeRequestRule(RuleSetViewModel<InterceptedRequest, RequestMatchType> viewModel, int selectedRow) {
        viewModel.getTableModel().removeRule(selectedRow);
        syncRequestRuleSet(viewModel);
    }

    private void moveRequestRuleUp(RuleSetViewModel<InterceptedRequest, RequestMatchType> viewModel, int selectedRow) {
        viewModel.getTableModel().moveRule(selectedRow, selectedRow - 1);
        syncRequestRuleSet(viewModel);
    }

    private void moveRequestRuleDown(RuleSetViewModel<InterceptedRequest, RequestMatchType> viewModel, int selectedRow) {
        viewModel.getTableModel().moveRule(selectedRow, selectedRow + 1);
        syncRequestRuleSet(viewModel);
    }

    private void addResponseRule(RuleSetViewModel<InterceptedResponse, ResponseMatchType> viewModel) {
        RuleRowModel<ResponseMatchType> createdRule = RuleEditorDialog.show(this, null, ResponseMatchType.values());
        if (createdRule != null) {
            viewModel.getTableModel().addRule(createdRule);
            syncResponseRuleSet(viewModel);
        }
    }

    private void editResponseRule(RuleSetViewModel<InterceptedResponse, ResponseMatchType> viewModel, int selectedRow) {
        RuleRowModel<ResponseMatchType> existingRule = viewModel.getTableModel().getRule(selectedRow);
        RuleRowModel<ResponseMatchType> editedRule = RuleEditorDialog.show(this, existingRule.copy(), ResponseMatchType.values());
        if (editedRule != null) {
            viewModel.getTableModel().updateRule(selectedRow, editedRule);
            syncResponseRuleSet(viewModel);
        }
    }

    private void removeResponseRule(RuleSetViewModel<InterceptedResponse, ResponseMatchType> viewModel, int selectedRow) {
        viewModel.getTableModel().removeRule(selectedRow);
        syncResponseRuleSet(viewModel);
    }

    private void moveResponseRuleUp(RuleSetViewModel<InterceptedResponse, ResponseMatchType> viewModel, int selectedRow) {
        viewModel.getTableModel().moveRule(selectedRow, selectedRow - 1);
        syncResponseRuleSet(viewModel);
    }

    private void moveResponseRuleDown(RuleSetViewModel<InterceptedResponse, ResponseMatchType> viewModel, int selectedRow) {
        viewModel.getTableModel().moveRule(selectedRow, selectedRow + 1);
        syncResponseRuleSet(viewModel);
    }

    private void syncRequestRuleSet(RuleSetViewModel<InterceptedRequest, RequestMatchType> viewModel) {
        viewModel.getRuleSetModel().getRuleSet().clear();

        List<Rule<InterceptedRequest, ?, ?>> rules = viewModel.getTableModel().rules().stream()
                .filter(RuleRowModel::isEnabled)
                .map(this::toRequestRule)
                .toList();

        viewModel.getRuleSetModel().getRuleSet().addAll(rules);
    }

    private void syncResponseRuleSet(RuleSetViewModel<InterceptedResponse, ResponseMatchType> viewModel) {
        viewModel.getRuleSetModel().getRuleSet().clear();

        List<Rule<InterceptedResponse, ?, ?>> rules = viewModel.getTableModel().rules().stream()
                .filter(RuleRowModel::isEnabled)
                .map(this::toResponseRule)
                .toList();

        viewModel.getRuleSetModel().getRuleSet().addAll(rules);
    }

    private Rule<InterceptedRequest, ?, ?> toRequestRule(RuleRowModel<RequestMatchType> row) {
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

    private Rule<InterceptedResponse, ?, ?> toResponseRule(RuleRowModel<ResponseMatchType> row) {
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

    private <T, X extends Enum<X> & MatchTypeDescriptor, M, C> Rule<T, M, C> buildRule(
            RuleRowModel<X> row,
            Function<? super T, M> extractor,
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

    private short parseShortCondition(String value) {
        return Short.parseShort(value.trim());
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

    @Override
    public ProxyResponseReceivedAction handleResponseReceived(InterceptedResponse interceptedResponse) {
        var annotations = interceptedResponse.annotations();

        for (HighlightRuleSet<InterceptedResponse> ruleSet : responseRuleSets) {
            if (!ruleSet.isEnabled()) {
                continue;
            }

            if (ruleSet.getRuleSet().evaluate(interceptedResponse)) {
                annotations = annotations
                        .withHighlightColor(ruleSet.getColor())
                        .withNotes(ruleSet.getComment());
            }
        }

        return ProxyResponseReceivedAction.continueWith(interceptedResponse, annotations);
    }

    @Override
    public ProxyResponseToBeSentAction handleResponseToBeSent(InterceptedResponse interceptedResponse) {
        return ProxyResponseToBeSentAction.continueWith(interceptedResponse);
    }
}
