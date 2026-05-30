package marker.ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class RuleTableModel<T extends Enum<T> & MatchTypeDescriptor> extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Enabled", "Operator", "Match type", "Relationship", "Condition"
    };

    private final List<RuleRowModel<T>> rules = new ArrayList<>();

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
        RuleRowModel<T> rule = rules.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rule.isEnabled() ? "Enabled" : "Disabled";
            case 1 -> rule.getOperator();
            case 2 -> rule.getMatchType();
            case 3 -> rule.getRelationship();
            case 4 -> rule.getCondition();
            default -> "";
        };
    }

    public int addRule(RuleRowModel<T> rule) {
        rules.add(rule);
        int index = rules.size() - 1;
        fireTableRowsInserted(index, index);
        return index;
    }

    public RuleRowModel<T> getRule(int rowIndex) {
        return rules.get(rowIndex);
    }

    public void updateRule(int rowIndex, RuleRowModel<T> updatedRule) {
        rules.set(rowIndex, updatedRule);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void removeRule(int rowIndex) {
        rules.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void moveRule(int fromIndex, int toIndex) {
        RuleRowModel<T> movedRule = rules.remove(fromIndex);
        rules.add(toIndex, movedRule);
        fireTableDataChanged();
    }

    public List<RuleRowModel<T>> rules() {
        return List.copyOf(rules);
    }
}
