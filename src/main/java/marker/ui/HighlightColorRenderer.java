package marker.ui;

import burp.api.montoya.core.HighlightColor;

import javax.swing.*;
import java.awt.*;

public class HighlightColorRenderer extends DefaultListCellRenderer {
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
}
