package marker;

import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.InterceptedResponse;
import marker.rule.HighlightRuleSet;
import marker.rule.RequestRuleMapper;
import marker.rule.ResponseRuleMapper;
import marker.rule.Rule;
import marker.ui.HighlightColorRenderer;
import marker.ui.MatchTypeDescriptor;
import marker.ui.RequestMatchType;
import marker.ui.ResponseMatchType;
import marker.ui.RuleEditorDialog;
import marker.ui.RuleRowModel;
import marker.ui.RuleSetEditorModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class MarkerPanel extends JPanel {
    private final RequestRuleMapper requestRuleMapper = new RequestRuleMapper();
    private final ResponseRuleMapper responseRuleMapper = new ResponseRuleMapper();

    private JPanel requestRuleSetsPanel;
    private JPanel responseRuleSetsPanel;

    private final List<RuleSetEditorModel<InterceptedRequest, RequestMatchType>> requestRuleSets = new ArrayList<>();
    private final List<RuleSetEditorModel<InterceptedResponse, ResponseMatchType>> responseRuleSets = new ArrayList<>();

    public MarkerPanel() {
        setupUI();
    }

    public List<HighlightRuleSet<InterceptedRequest>> requestHighlightRuleSets() {
        return requestRuleSets.stream().map(RuleSetEditorModel::getRuleSet).toList();
    }

    public List<HighlightRuleSet<InterceptedResponse>> responseHighlightRuleSets() {
        return responseRuleSets.stream().map(RuleSetEditorModel::getRuleSet).toList();
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
            RuleSetEditorModel<M, T> editorModel,
            Consumer<JPanel> deleteRuleSetAction,
            Consumer<RuleSetEditorModel<M, T>> addRuleAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> editRuleAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> removeRuleAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> moveUpAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> moveDownAction
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

        JTable rulesTable = new JTable(editorModel.getTableModel());
        configureRulesTable(rulesTable);

        JComponent metaPanel = buildRuleSetMetaPanel(editorModel.getRuleSet(), panel, deleteRuleSetAction);
        JComponent actionsPanel = buildRuleActionsPanel(
                editorModel,
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
            HighlightRuleSet<M> ruleSet,
            JPanel ruleSetPanel,
            Consumer<JPanel> deleteRuleSetAction
    ) {
        JPanel panel = new JPanel(new MigLayout(
                "fillx, insets 0, gapx 12",
                "[pref!][pref!][110!][pref!][220!][grow][pref!]",
                "[]"
        ));

        JCheckBox enabled = new JCheckBox("Enabled", ruleSet.isEnabled());
        enabled.setFont(enabled.getFont().deriveFont(Font.BOLD, 13f));
        updateEnabledPresentation(enabled, ruleSet.isEnabled());
        enabled.addActionListener(event -> {
            ruleSet.setEnabled(enabled.isSelected());
            updateEnabledPresentation(enabled, enabled.isSelected());
        });

        JLabel colorLabel = new JLabel("Color");
        JComboBox<HighlightColor> colorCombo = new JComboBox<>(HighlightColor.values());
        colorCombo.setRenderer(new HighlightColorRenderer());
        colorCombo.setSelectedItem(ruleSet.getColor());
        colorCombo.addActionListener(event -> ruleSet.setColor((HighlightColor) colorCombo.getSelectedItem()));

        JLabel commentLabel = new JLabel("Comment");
        JTextField commentField = new JTextField(ruleSet.getComment());
        bindTextField(commentField, ruleSet::setComment);

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
            RuleSetEditorModel<M, T> editorModel,
            JTable rulesTable,
            Consumer<RuleSetEditorModel<M, T>> addRuleAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> editRuleAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> removeRuleAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> moveUpAction,
            BiConsumer<RuleSetEditorModel<M, T>, Integer> moveDownAction
    ) {
        JPanel panel = new JPanel(new MigLayout(
                "insets 0, wrap 1, gapy 8",
                "[110!]",
                "[][][][][]"
        ));

        JButton addButton = new JButton("Add");
        addButton.addActionListener(event -> {
            int previousCount = editorModel.getTableModel().getRowCount();
            addRuleAction.accept(editorModel);
            int newIndex = editorModel.getTableModel().getRowCount() - 1;
            if (editorModel.getTableModel().getRowCount() > previousCount && newIndex >= 0) {
                rulesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
            }
        });

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0) {
                editRuleAction.accept(editorModel, selectedRow);
                rulesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
            }
        });

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0) {
                removeRuleAction.accept(editorModel, selectedRow);
                if (editorModel.getTableModel().getRowCount() > 0) {
                    int selectionIndex = Math.min(selectedRow, editorModel.getTableModel().getRowCount() - 1);
                    rulesTable.getSelectionModel().setSelectionInterval(selectionIndex, selectionIndex);
                }
            }
        });

        JButton upButton = new JButton("Up");
        upButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow > 0) {
                moveUpAction.accept(editorModel, selectedRow);
                rulesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
            }
        });

        JButton downButton = new JButton("Down");
        downButton.addActionListener(event -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0 && selectedRow < editorModel.getTableModel().getRowCount() - 1) {
                moveDownAction.accept(editorModel, selectedRow);
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

        RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel = new RuleSetEditorModel<>(new HighlightRuleSet<>());
        requestRuleSets.add(editorModel);
        requestRuleSetsPanel.add(buildRuleSetPanel(
                editorModel,
                panel -> removeRequestRuleSet(editorModel, panel),
                this::addRequestRule,
                this::editRequestRule,
                this::removeRequestRule,
                this::moveRequestRuleUp,
                this::moveRequestRuleDown
        ), "growx");
        refreshPanel(requestRuleSetsPanel);
    }

    private void removeRequestRuleSet(RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel, JPanel ruleSetPanel) {
        requestRuleSets.remove(editorModel);
        requestRuleSetsPanel.remove(ruleSetPanel);
        refreshPanel(requestRuleSetsPanel);
    }

    private void addResponseRuleSet() {
        if (responseRuleSetsPanel == null) {
            return;
        }

        RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel = new RuleSetEditorModel<>(new HighlightRuleSet<>());
        responseRuleSets.add(editorModel);
        responseRuleSetsPanel.add(buildRuleSetPanel(
                editorModel,
                panel -> removeResponseRuleSet(editorModel, panel),
                this::addResponseRule,
                this::editResponseRule,
                this::removeResponseRule,
                this::moveResponseRuleUp,
                this::moveResponseRuleDown
        ), "growx");
        refreshPanel(responseRuleSetsPanel);
    }

    private void removeResponseRuleSet(RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel, JPanel ruleSetPanel) {
        responseRuleSets.remove(editorModel);
        responseRuleSetsPanel.remove(ruleSetPanel);
        refreshPanel(responseRuleSetsPanel);
    }

    private void refreshPanel(JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }

    private void addRequestRule(RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel) {
        RuleRowModel<RequestMatchType> createdRule = RuleEditorDialog.show(this, null, RequestMatchType.values());
        if (createdRule != null) {
            editorModel.getTableModel().addRule(createdRule);
            syncRequestRuleSet(editorModel);
        }
    }

    private void editRequestRule(RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel, int selectedRow) {
        RuleRowModel<RequestMatchType> existingRule = editorModel.getTableModel().getRule(selectedRow);
        RuleRowModel<RequestMatchType> editedRule = RuleEditorDialog.show(this, existingRule.copy(), RequestMatchType.values());
        if (editedRule != null) {
            editorModel.getTableModel().updateRule(selectedRow, editedRule);
            syncRequestRuleSet(editorModel);
        }
    }

    private void removeRequestRule(RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel, int selectedRow) {
        editorModel.getTableModel().removeRule(selectedRow);
        syncRequestRuleSet(editorModel);
    }

    private void moveRequestRuleUp(RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel, int selectedRow) {
        editorModel.getTableModel().moveRule(selectedRow, selectedRow - 1);
        syncRequestRuleSet(editorModel);
    }

    private void moveRequestRuleDown(RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel, int selectedRow) {
        editorModel.getTableModel().moveRule(selectedRow, selectedRow + 1);
        syncRequestRuleSet(editorModel);
    }

    private void addResponseRule(RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel) {
        RuleRowModel<ResponseMatchType> createdRule = RuleEditorDialog.show(this, null, ResponseMatchType.values());
        if (createdRule != null) {
            editorModel.getTableModel().addRule(createdRule);
            syncResponseRuleSet(editorModel);
        }
    }

    private void editResponseRule(RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel, int selectedRow) {
        RuleRowModel<ResponseMatchType> existingRule = editorModel.getTableModel().getRule(selectedRow);
        RuleRowModel<ResponseMatchType> editedRule = RuleEditorDialog.show(this, existingRule.copy(), ResponseMatchType.values());
        if (editedRule != null) {
            editorModel.getTableModel().updateRule(selectedRow, editedRule);
            syncResponseRuleSet(editorModel);
        }
    }

    private void removeResponseRule(RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel, int selectedRow) {
        editorModel.getTableModel().removeRule(selectedRow);
        syncResponseRuleSet(editorModel);
    }

    private void moveResponseRuleUp(RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel, int selectedRow) {
        editorModel.getTableModel().moveRule(selectedRow, selectedRow - 1);
        syncResponseRuleSet(editorModel);
    }

    private void moveResponseRuleDown(RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel, int selectedRow) {
        editorModel.getTableModel().moveRule(selectedRow, selectedRow + 1);
        syncResponseRuleSet(editorModel);
    }

    private void syncRequestRuleSet(RuleSetEditorModel<InterceptedRequest, RequestMatchType> editorModel) {
        syncRuleSet(editorModel.getRuleSet(), editorModel.getRules(), requestRuleMapper::toRule);
    }

    private void syncResponseRuleSet(RuleSetEditorModel<InterceptedResponse, ResponseMatchType> editorModel) {
        syncRuleSet(editorModel.getRuleSet(), editorModel.getRules(), responseRuleMapper::toRule);
    }

    private <M, T extends Enum<T> & MatchTypeDescriptor> void syncRuleSet(
            HighlightRuleSet<M> ruleSet,
            List<RuleRowModel<T>> rows,
            Function<RuleRowModel<T>, Rule<M, ?, ?>> mapper
    ) {
        ruleSet.getRuleSet().clear();
        ruleSet.getRuleSet().addAll(rows.stream()
                .filter(RuleRowModel::isEnabled)
                .map(mapper)
                .toList());
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
}
