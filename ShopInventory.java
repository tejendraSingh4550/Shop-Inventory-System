import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ShopInventory extends JFrame {

    // ---------- Color Palette ----------
    private static final Color BG_DARK = new Color(24, 28, 38);
    private static final Color BG_CARD = new Color(34, 40, 54);
    private static final Color ACCENT_ORANGE = new Color(249, 115, 22);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color TEXT_LIGHT = new Color(226, 232, 240);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color ROW_OK = new Color(22, 74, 47);
    private static final Color ROW_LOW = new Color(97, 78, 16);
    private static final Color ROW_OUT = new Color(97, 30, 30);

    // ---------- Data ----------
    private final List<Item> items = new ArrayList<>();
    private static final int LOW_STOCK_THRESHOLD = 5;
    private double totalSalesRevenue = 0;

    // ---------- UI Components ----------
    private final JTextField nameField = styledField();
    private final JTextField qtyField = styledField();
    private final JTextField priceField = styledField();
    private final JTextField categoryField = styledField();

    private final String[] columns = {"Item", "Category", "Qty", "Price (₹)", "Value (₹)", "Status"};
    private final DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField sellQtyField = styledField();
    private final JLabel summaryLabel = new JLabel();
    private final JLabel alertLabel = new JLabel();

    public ShopInventory() {
        setTitle("🏪 Shop Inventory Manager");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(new EmptyBorder(0, 16, 16, 16));
        root.setBackground(BG_DARK);

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_DARK);
        center.add(buildAddItemPanel());
        center.add(Box.createVerticalStrut(14));
        center.add(buildTablePanel());
        center.add(Box.createVerticalStrut(14));
        center.add(buildBottomPanel());

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        add(root);
        updateSummary();
    }

    // ---------- Header ----------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(18, 4, 10, 4));

        JLabel title = new JLabel("🏪  Shop Inventory Manager");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT_LIGHT);

        JLabel subtitle = new JLabel("Track stock, get low-stock alerts, and monitor sales in real time");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);

        JPanel textWrap = new JPanel();
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        textWrap.setBackground(BG_DARK);
        textWrap.add(title);
        textWrap.add(subtitle);

        header.add(textWrap, BorderLayout.WEST);
        return header;
    }

    // ---------- Card wrapper helper ----------
    private JPanel card(String titleText) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        if (titleText != null) {
            JLabel label = new JLabel(titleText);
            label.setFont(new Font("SansSerif", Font.BOLD, 15));
            label.setForeground(ACCENT_ORANGE);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.setLayout(new BorderLayout(10, 10));
            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(BG_CARD);
            top.add(label, BorderLayout.WEST);
            panel.add(top, BorderLayout.NORTH);
        }
        return panel;
    }

    private static JTextField styledField() {
        JTextField field = new JTextField();
        field.setBackground(new Color(15, 23, 42));
        field.setForeground(TEXT_LIGHT);
        field.setCaretColor(TEXT_LIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }

    private static JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    private static JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    // ---------- Add Item Panel ----------
    private JPanel buildAddItemPanel() {
        JPanel panel = card("➕  Add / Restock Item");

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBackground(BG_CARD);

        form.add(styledLabel("Item name"));
        form.add(nameField);
        form.add(styledLabel("Category"));
        form.add(categoryField);
        form.add(styledLabel("Quantity"));
        form.add(qtyField);
        form.add(styledLabel("Price per unit (₹)"));
        form.add(priceField);

        JButton addBtn = styledButton("Add Item to Inventory", ACCENT_ORANGE);
        addBtn.addActionListener(e -> addItem());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnWrap.setBackground(BG_CARD);
        btnWrap.setBorder(new EmptyBorder(12, 0, 0, 0));
        btnWrap.add(addBtn);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_CARD);
        wrap.add(form, BorderLayout.CENTER);
        wrap.add(btnWrap, BorderLayout.SOUTH);

        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private void addItem() {
        String name = nameField.getText().trim();
        String category = categoryField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter item name.");
            return;
        }
        if (category.isEmpty()) category = "General";

        int qty;
        double price;
        try {
            qty = Integer.parseInt(qtyField.getText().trim());
            if (qty < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity.");
            return;
        }
        try {
            price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid price.");
            return;
        }

        Item existing = findItem(name);
        if (existing != null) {
            existing.qty += qty;
            existing.price = price;
            existing.category = category;
        } else {
            items.add(new Item(name, category, qty, price));
        }

        nameField.setText("");
        categoryField.setText("");
        qtyField.setText("");
        priceField.setText("");

        refreshTable();
        updateSummary();
    }

    private Item findItem(String name) {
        for (Item i : items) {
            if (i.name.equalsIgnoreCase(name)) return i;
        }
        return null;
    }

    // ---------- Table Panel ----------
    private JPanel buildTablePanel() {
        JPanel panel = card("📦  Current Inventory");
        panel.setPreferredSize(new Dimension(0, 280));

        table.setRowHeight(30);
        table.setBackground(new Color(15, 23, 42));
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(51, 65, 85));
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setRowSelectionAllowed(true);
        table.setDefaultRenderer(Object.class, new StatusRowRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Custom renderer: colors each row based on the Status column
    private class StatusRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
            String status = (String) tbl.getModel().getValueAt(row, 5);

            if (!isSelected) {
                Color bg;
                switch (status) {
                    case "OUT OF STOCK": bg = ROW_OUT; break;
                    case "LOW STOCK": bg = ROW_LOW; break;
                    default: bg = ROW_OK;
                }
                c.setBackground(bg);
                c.setForeground(TEXT_LIGHT);
            } else {
                c.setBackground(ACCENT_BLUE);
                c.setForeground(Color.WHITE);
            }
            setHorizontalAlignment(column == 0 || column == 1 ? LEFT : CENTER);
            return c;
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Item i : items) {
            double value = i.qty * i.price;
            String status = i.qty == 0 ? "OUT OF STOCK" : (i.qty <= LOW_STOCK_THRESHOLD ? "LOW STOCK" : "OK");
            tableModel.addRow(new Object[]{
                    i.name, i.category, i.qty,
                    String.format("%.2f", i.price),
                    String.format("%.2f", value),
                    status
            });
        }
        checkLowStockAlerts();
    }

    private void checkLowStockAlerts() {
        List<String> lowItems = new ArrayList<>();
        for (Item i : items) {
            if (i.qty <= LOW_STOCK_THRESHOLD) lowItems.add(i.name + " (" + i.qty + " left)");
        }
        if (lowItems.isEmpty()) {
            alertLabel.setText(" ");
        } else {
            alertLabel.setText("⚠  Low stock: " + String.join(", ", lowItems));
            alertLabel.setForeground(ACCENT_RED);
        }
    }

    // ---------- Bottom Panel: Sell Item + Summary ----------
    private JPanel buildBottomPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(BG_DARK);

        JPanel sellPanel = card("💵  Sell Item");
        JPanel sellContent = new JPanel(new BorderLayout(10, 10));
        sellContent.setBackground(BG_CARD);
        sellContent.add(styledLabel("Select item in table above, enter quantity sold:"), BorderLayout.NORTH);

        JPanel sellRow = new JPanel(new BorderLayout(10, 0));
        sellRow.setBackground(BG_CARD);
        sellRow.add(sellQtyField, BorderLayout.CENTER);
        JButton sellBtn = styledButton("Record Sale", ACCENT_GREEN);
        sellBtn.addActionListener(e -> sellItem());
        sellRow.add(sellBtn, BorderLayout.EAST);
        sellContent.add(sellRow, BorderLayout.CENTER);

        sellPanel.add(sellContent, BorderLayout.CENTER);

        alertLabel.setText(" ");
        alertLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        summaryLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        summaryLabel.setForeground(TEXT_LIGHT);

        JPanel summaryCard = card(null);
        summaryCard.setLayout(new BorderLayout());
        summaryCard.add(summaryLabel, BorderLayout.CENTER);

        wrapper.add(sellPanel);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(alertLabel);
        wrapper.add(Box.createVerticalStrut(10));
        wrapper.add(summaryCard);

        return wrapper;
    }

    private void sellItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item from the table first.");
            return;
        }
        String name = (String) tableModel.getValueAt(row, 0);
        Item item = findItem(name);
        if (item == null) return;

        int qtySold;
        try {
            qtySold = Integer.parseInt(sellQtyField.getText().trim());
            if (qtySold <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity to sell.");
            return;
        }

        if (qtySold > item.qty) {
            JOptionPane.showMessageDialog(this, "Not enough stock! Only " + item.qty + " left.");
            return;
        }

        item.qty -= qtySold;
        totalSalesRevenue += qtySold * item.price;
        sellQtyField.setText("");

        refreshTable();
        updateSummary();
    }

    private void updateSummary() {
        double totalValue = 0;
        int totalItems = 0;
        for (Item i : items) {
            totalValue += i.qty * i.price;
            totalItems += i.qty;
        }
        summaryLabel.setText(String.format(
                "<html>📊 Total items in stock: <b>%d</b> &nbsp;|&nbsp; 💰 Inventory value: <b>₹%.2f</b> &nbsp;|&nbsp; 🧾 Total sales revenue: <b>₹%.2f</b></html>",
                totalItems, totalValue, totalSalesRevenue));
    }

    // ---------- Helper Class ----------
    private static class Item {
        String name;
        String category;
        int qty;
        double price;

        Item(String name, String category, int qty, double price) {
            this.name = name;
            this.category = category;
            this.qty = qty;
            this.price = price;
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // fall back to default look and feel if Nimbus isn't available
        }

        SwingUtilities.invokeLater(() -> {
            ShopInventory app = new ShopInventory();
            app.setVisible(true);
        });
    }
}