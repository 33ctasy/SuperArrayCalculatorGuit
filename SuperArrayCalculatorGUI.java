import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.DecimalFormat;

public class SuperArrayCalculatorGUI extends JFrame {
    private JTextField displayField;
    private JTextArea historyArea;
    private LinkedList<String> history = new LinkedList<>();
    private DecimalFormat df = new DecimalFormat("#.##");
    private java.util.List<Integer> currentArray = new ArrayList<>();
    private JPanel mainPanel;

    public SuperArrayCalculatorGUI() {
        setTitle("SuperArrayCalculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xF5F5F5));

        // Главная панель
        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Логотип-сетка
        mainPanel.add(createLogoPanel(), BorderLayout.NORTH);

        // Центральная панель
        JPanel centerPanel = new JPanel(new BorderLayout());
        displayField = new JTextField("0");
        displayField.setFont(new Font("Arial", Font.BOLD, 24));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(false);
        displayField.setBackground(Color.WHITE);

        historyArea = new JTextArea(5, 20);
        historyArea.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyArea);

        centerPanel.add(displayField, BorderLayout.NORTH);
        centerPanel.add(historyScroll, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = createButtonPanel();

        // Сборка интерфейса
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Обработка клавиатуры
        displayField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKeyInput(e.getKeyChar());
            }
        });
    }

    // Метод создания логотипа
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new GridLayout(10, 10, 1, 1));
        logoPanel.setBackground(new Color(0x4A90E2));
        
        for (int i = 1; i <= 100; i++) {
            JLabel numLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            numLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            numLabel.setForeground((i % 2 == 0) ? new Color(0x50E3C2) : Color.WHITE);
            logoPanel.add(numLabel);
        }
        return logoPanel;
    }

    // Метод создания кнопок
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        String[][] buttonLabels = {
            {"MRU", "M-M+", "MRU", "C"},
            {"%", "7", "8", "9"},
            {"±", "4", "5", "6"},
            {"Sort", "1", "2", "3"},
            {"Stats", "0", "00", "="}
        };

        for (String[] row : buttonLabels) {
            for (String label : row) {
                JButton btn = new JButton(label);
                btn.setFont(new Font("Arial", Font.BOLD, 18));
                btn.setForeground(Color.WHITE);
                btn.setBackground(label.matches("[0-9]") ? new Color(0x4A90E2) : new Color(0x50E3C2));
                btn.addActionListener(e -> handleButtonClick(label));
                buttonPanel.add(btn);
            }
        }
        return buttonPanel;
    }

    // Обработчик нажатий кнопок
    private void handleButtonClick(String command) {
        switch (command) {
            case "C":
                currentArray.clear();
                displayField.setText("0");
                break;
            case "=":
                addNumberToArray();
                break;
            case "Sort":
                showSortMenu();
                break;
            case "Stats":
                showStatsMenu();
                break;
            default:
                if (command.matches("[0-9]")) {
                    updateDisplay(command);
                }
        }
    }

    // Обработчик ввода с клавиатуры
    private void handleKeyInput(char key) {
        if (key == ' ') {
            addNumberToArray();
        } else if (Character.isDigit(key)) {
            updateDisplay(String.valueOf(key));
        }
    }

    // Обновление поля ввода
    private void updateDisplay(String input) {
        String current = displayField.getText();
        displayField.setText(current.equals("0") ? input : current + input);
    }

    // Добавление числа в массив
    private void addNumberToArray() {
        try {
            int num = Integer.parseInt(displayField.getText());
            currentArray.add(num);
            updateHistory("Добавлено: " + num);
            displayField.setText("0");
        } catch (NumberFormatException ex) {
            showError("Ошибка формата числа!");
        }
    }

    // Меню сортировки
    private void showSortMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem asc = new JMenuItem("По возрастанию");
        JMenuItem desc = new JMenuItem("По убыванию");
        
        asc.addActionListener(e -> sortArray(true));
        desc.addActionListener(e -> sortArray(false));
        
        menu.add(asc);
        menu.add(desc);
        menu.show(displayField, 0, displayField.getHeight());
    }

    // Сортировка массива
    private void sortArray(boolean ascending) {
        int[] arr = currentArray.stream().mapToInt(i -> i).toArray();
        Arrays.sort(arr);
        if (!ascending) arr = reverseArray(arr);
        currentArray.clear();
        for (int num : arr) currentArray.add(num);
        updateHistory("Отсортировано: " + Arrays.toString(arr));
    }

    // Реверс массива
    private int[] reverseArray(int[] arr) {
        int[] reversed = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            reversed[i] = arr[arr.length - 1 - i];
        }
        return reversed;
    }

    // Меню статистики
    private void showStatsMenu() {
        JPopupMenu menu = new JPopupMenu();
        String[] stats = {"Среднее", "Медиана", "Сумма", "Минимум", "Максимум", "Дисперсия"};
        
        for (String stat : stats) {
            JMenuItem item = new JMenuItem(stat);
            item.addActionListener(e -> showStatResult(stat));
            menu.add(item);
        }
        menu.show(displayField, 0, displayField.getHeight());
    }

    // Вывод статистики
    private void showStatResult(String stat) {
        int[] arr = currentArray.stream().mapToInt(i -> i).toArray();
        String result = switch (stat) {
            case "Среднее" -> df.format(Arrays.stream(arr).average().orElse(0));
            case "Медиана" -> calculateMedian(arr);
            case "Сумма" -> String.valueOf(Arrays.stream(arr).sum());
            case "Минимум" -> String.valueOf(Arrays.stream(arr).min().orElse(0));
            case "Максимум" -> String.valueOf(Arrays.stream(arr).max().orElse(0));
            case "Дисперсия" -> df.format(calculateVariance(arr));
            default -> "N/A";
        };
        
        displayField.setText(stat + ": " + result);
        updateHistory(stat + ": " + result);
    }

    // Расчет медианы
    private String calculateMedian(int[] arr) {
        int[] sorted = arr.clone();
        Arrays.sort(sorted);
        return (sorted.length % 2 == 0) ?
            df.format((sorted[sorted.length/2 - 1] + sorted[sorted.length/2]) / 2.0) :
            df.format(sorted[sorted.length/2]);
    }

    // Расчет дисперсии
    private double calculateVariance(int[] arr) {
        double mean = Arrays.stream(arr).average().orElse(0);
        return Arrays.stream(arr)
            .mapToDouble(num -> Math.pow(num - mean, 2))
            .sum() / arr.length;
    }

    // Обновление истории
    private void updateHistory(String entry) {
        if (history.size() >= 10) history.removeFirst();
        history.add(entry);
        historyArea.setText(String.join("\n", history));
    }

    // Вывод ошибки
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuperArrayCalculatorGUI().setVisible(true));
    }
}