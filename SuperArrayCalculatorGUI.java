import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperArrayCalculatorGUI extends JFrame {
    private JTextField displayField;
    private JTextArea historyArea;
    private ArrayList<Double> currentArray = new ArrayList<>();
    private LinkedList<String> history = new LinkedList<>();
    private String currentOperation = "";
    private double firstNumber = 0;

    private final Color BG_MAIN = Color.decode("#F5F5F5");
    private final Color PANEL_BG = Color.decode("#FFFFFF");
    private final Color ACCENT = Color.decode("#4A90E2");
    private final Color SUCCESS = Color.decode("#50E3C2");
    private final Color TEXT_MAIN = Color.decode("#333333");
    private final Color TEXT_HINT = Color.decode("#888888");
    private final Color ERROR = Color.decode("#D0021B");

    public SuperArrayCalculatorGUI() {
        setTitle("SuperArrayCalculator");
        setSize(500, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);

     
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setForeground(TEXT_MAIN);
        tabbedPane.setBackground(BG_MAIN);

   
        JPanel calculatorTab = createCalculatorTab();
        tabbedPane.addTab("Калькулятор", calculatorTab);


        JPanel converterTab = createUnitConverterTab();
        tabbedPane.addTab("Конвертер", converterTab);

        add(tabbedPane);
    }

    private JPanel createCalculatorTab() {
        JPanel calcPanel = new JPanel(new BorderLayout(10, 10));
        calcPanel.setBackground(BG_MAIN);

        // Верх: displayField
        displayField = new JTextField("0");
        displayField.setFont(new Font("Arial", Font.BOLD, 24));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(true);
        displayField.setBackground(PANEL_BG);
        displayField.setForeground(TEXT_MAIN);
        displayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        displayField.addActionListener(e -> addNumberToArray());
        displayField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String text = displayField.getText();
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(new StringSelection(text), null);
                    updateHistory("📋 Скопійовано: " + text);
                }
            }
        });
        calcPanel.add(displayField, BorderLayout.NORTH);

  
        historyArea = new JTextArea(5, 20);
        historyArea.setEditable(false);
        historyArea.setBackground(PANEL_BG);
        historyArea.setForeground(TEXT_MAIN);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT),
                "Історія", 0, 0,
                new Font("Arial", Font.BOLD, 16),
                TEXT_MAIN
        ));
        calcPanel.add(scrollPane, BorderLayout.CENTER);

     
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(BG_MAIN);

     
        JPanel scientificPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        scientificPanel.setBackground(BG_MAIN);
        String[] scientificButtons = {"sin", "cos", "tan", "ctg", "More"};
        for (String label : scientificButtons) {
            JButton button = createButton(label, SUCCESS);
            button.addActionListener(e -> {
                if (label.equals("More")) showAdvancedStatistics();
                else handleScientificFunction(label);
            });
            scientificPanel.add(button);
        }
        mainPanel.add(scientificPanel, BorderLayout.NORTH);

      
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBackground(BG_MAIN);
        String[] buttons = {
                "Clear", "←", "±", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "=", "Undo"
        };
        for (String label : buttons) {
            JButton button = createButton(label, ACCENT);
            button.addActionListener(e -> handleButton(label));
            buttonPanel.add(button);
        }
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        calcPanel.add(mainPanel, BorderLayout.SOUTH);

        return calcPanel;
    }

    private JPanel createUnitConverterTab() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_MAIN);
        panel.setLayout(null);

      
        JLabel inputLabel = new JLabel("Значення:");
        inputLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        inputLabel.setForeground(TEXT_MAIN);
        inputLabel.setBounds(30, 30, 100, 25);
        panel.add(inputLabel);

        
        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 18));
        inputField.setBackground(PANEL_BG);
        inputField.setForeground(TEXT_MAIN);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        inputField.setBounds(140, 30, 200, 25);
        panel.add(inputField);

     
        JLabel fromLabel = new JLabel("Звідки:");
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        fromLabel.setForeground(TEXT_MAIN);
        fromLabel.setBounds(30, 75, 100, 25);
        panel.add(fromLabel);


        JComboBox<String> fromUnit = new JComboBox<>(new String[]{
                "метри", "сантиметри", "кілометри", "грами", "кілограми"
        });
        fromUnit.setFont(new Font("Arial", Font.PLAIN, 16));
        fromUnit.setBackground(PANEL_BG);
        fromUnit.setForeground(TEXT_MAIN);
        fromUnit.setBounds(140, 75, 200, 25);
        panel.add(fromUnit);


        JLabel toLabel = new JLabel("Куди:");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        toLabel.setForeground(TEXT_MAIN);
        toLabel.setBounds(30, 120, 100, 25);
        panel.add(toLabel);

    
        JComboBox<String> toUnit = new JComboBox<>(new String[]{
                "метри", "сантиметри", "кілометри", "грами", "кілограми"
        });
        toUnit.setFont(new Font("Arial", Font.PLAIN, 16));
        toUnit.setBackground(PANEL_BG);
        toUnit.setForeground(TEXT_MAIN);
        toUnit.setBounds(140, 120, 200, 25);
        panel.add(toUnit);

        
        JButton convertButton = createButton("Конвертувати", ACCENT);
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBounds(140, 165, 200, 30);
        panel.add(convertButton);

       
        JLabel resultLabel = new JLabel("Результат: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(TEXT_MAIN);
        resultLabel.setBounds(30, 215, 350, 25);
        panel.add(resultLabel);

        convertButton.addActionListener(e -> {
            try {
                double value = Double.parseDouble(inputField.getText());
                String from = (String) fromUnit.getSelectedItem();
                String to = (String) toUnit.getSelectedItem();
                double converted = convertUnits(value, from, to);
                resultLabel.setText("Результат: " + converted + " " + to);
            } catch (NumberFormatException ex) {
                showError("Некоректне число!");
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });

        return panel;
    }

    private double convertUnits(double value, String from, String to) {
        Map<String, Double> unitToMeter = Map.of(
                "метри", 1.0,
                "сантиметри", 0.01,
                "кілометри", 1000.0
        );

        Map<String, Double> unitToKg = Map.of(
                "кілограми", 1.0,
                "грами", 0.001
        );

        if (unitToMeter.containsKey(from) && unitToMeter.containsKey(to)) {
            return value * unitToMeter.get(from) / unitToMeter.get(to);
        } else if (unitToKg.containsKey(from) && unitToKg.containsKey(to)) {
            return value * unitToKg.get(from) / unitToKg.get(to);
        } else {
            throw new IllegalArgumentException("Несумісні одиниці: " + from + " → " + to);
        }
    }

    private JButton createButton(String label, Color color) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(color.darker(), 1));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        return button;
    }

    private void handleButton(String label) {
        switch (label) {
            case "Clear" -> {
                currentArray.clear();
                updateHistory("🧹 Масив очищено!");
                flashColor(ACCENT);
            }
            case "C" -> {
                displayField.setText("0");
                currentArray.clear();
                updateHistory("Очищено");
                displayField.setForeground(TEXT_MAIN);
            }
            case "←" -> {
                String text = displayField.getText();
                if (text.length() > 1) displayField.setText(text.substring(0, text.length() - 1));
                else displayField.setText("0");
                displayField.setForeground(TEXT_MAIN);
            }
            case "±" -> {
                String text = displayField.getText();
                if (text.startsWith("-")) displayField.setText(text.substring(1));
                else if (!text.equals("0")) displayField.setText("-" + text);
                displayField.setForeground(TEXT_MAIN);
            }
            case "+", "-", "*", "/", "%" -> {
                try {
                    firstNumber = Double.parseDouble(displayField.getText());
                    currentOperation = label;
                    displayField.setText("0");
                    displayField.setForeground(TEXT_MAIN);
                } catch (NumberFormatException ex) {
                    showError("Помилка вводу!");
                }
            }
            case "=" -> calculateResult();
            case "Undo" -> undoLastEntry();
            default -> {
                if (label.matches("[0-9.]")) {
                    String current = displayField.getText();
                    if (label.equals(".") && current.contains(".")) return;
                    if (current.equals("0") || displayField.getForeground().equals(SUCCESS)) {
                        displayField.setText(label);
                    } else {
                        displayField.setText(current + label);
                    }
                    displayField.setForeground(TEXT_MAIN);
                }
            }
        }
    }

    private void addNumberToArray() {
        try {
            double number = Double.parseDouble(displayField.getText());
            currentArray.add(number);
            updateHistory("Додано: " + number + " (всього: " + currentArray.size() + ")");
            displayField.setText("0");
            displayField.setForeground(SUCCESS);
            flashColor(SUCCESS);
        } catch (NumberFormatException ex) {
            showError("Невірне число!");
        }
    }

    private void calculateResult() {
        try {
            double second = Double.parseDouble(displayField.getText());
            double result = switch (currentOperation) {
                case "+" -> firstNumber + second;
                case "-" -> firstNumber - second;
                case "*" -> firstNumber * second;
                case "/" -> {
                    if (second == 0) throw new ArithmeticException();
                    yield firstNumber / second;
                }
                case "%" -> firstNumber % second;
                default -> 0;
            };
            displayField.setText(String.valueOf(result));
            currentArray.add(result);
            updateHistory("Результат: " + result);
            displayField.setForeground(SUCCESS);
            flashColor(SUCCESS);
        } catch (NumberFormatException ex) {
            showError("Помилка обчислення!");
        } catch (ArithmeticException e) {
            showError("Ділення на нуль!");
        }
    }

    private void handleScientificFunction(String func) {
        try {
            double degrees = Double.parseDouble(displayField.getText());
            double radians = Math.toRadians(degrees);
            double result = switch (func) {
                case "sin" -> Math.sin(radians);
                case "cos" -> Math.cos(radians);
                case "tan" -> Math.tan(radians);
                case "ctg" -> {
                    double tan = Math.tan(radians);
                    if (tan == 0) throw new ArithmeticException();
                    yield 1.0 / tan;
                }
                default -> throw new IllegalArgumentException("Unknown function: " + func);
            };
            displayField.setText(String.valueOf(result));
            currentArray.add(result);
            updateHistory(func + "(" + degrees + ") = " + result);
            displayField.setForeground(SUCCESS);
            flashColor(SUCCESS);
        } catch (NumberFormatException ex) {
            showError("Невірне число!");
        } catch (ArithmeticException ex) {
            showError("Недопустима операція!");
        }
    }

    private void updateHistory(String msg) {
        history.addFirst(msg);
        if (history.size() > 10) history.removeLast();
        historyArea.setText(String.join("\n", history));
    }

    private void showError(String msg) {
        displayField.setForeground(ERROR);
        flashColor(ERROR);
        JOptionPane.showMessageDialog(this, msg, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    private void showAdvancedStatistics() {
        if (currentArray.isEmpty()) {
            showError("Масив порожній!");
            return;
        }

        List<Double> sorted = new ArrayList<>(currentArray);
        Collections.sort(sorted);

        double sum = sorted.stream().mapToDouble(Double::doubleValue).sum();
        double average = sum / sorted.size();
        double median = calculateMedian(sorted);
        double q1 = calculateMedian(sorted.subList(0, sorted.size() / 2));
        double q3 = calculateMedian(sorted.subList((sorted.size() + 1) / 2, sorted.size()));
        double max = Collections.max(sorted);
        double min = Collections.min(sorted);
        long positives = sorted.stream().filter(n -> n > 0).count();
        long negatives = sorted.stream().filter(n -> n < 0).count();
        double mode = findMode(sorted);

        StringBuilder sb = new StringBuilder("📊 Статистика:\n");
        sb.append("🔢 Кількість: ").append(sorted.size()).append("\n");
        sb.append("➕ Сума: ").append(sum).append("\n");
        sb.append("📈 Середнє: ").append(average).append("\n");
        sb.append("📌 Медіана: ").append(median).append("\n");
        sb.append("🟰 Q1: ").append(q1).append("\n");
        sb.append("🟰 Q3: ").append(q3).append("\n");
        sb.append("🔼 Макс: ").append(max).append("\n");
        sb.append("🔽 Мін: ").append(min).append("\n");
        sb.append("➕ Додатних: ").append(positives).append("\n");
        sb.append("➖ Від’ємних: ").append(negatives).append("\n");
        sb.append("🔁 Найчастіше: ").append(mode).append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Розширена статистика", JOptionPane.INFORMATION_MESSAGE);
    }

    private double calculateMedian(List<Double> list) {
        int size = list.size();
        if (size % 2 == 0)
            return (list.get(size / 2 - 1) + list.get(size / 2)) / 2.0;
        else
            return list.get(size / 2);
    }

    private double findMode(List<Double> list) {
        Map<Double, Long> freq = list.stream()
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(Map.entry(0.0, 0L))
                .getKey();
    }

    private void undoLastEntry() {
        if (!currentArray.isEmpty()) {
            double removed = currentArray.remove(currentArray.size() - 1);
            updateHistory("Скасовано: " + removed);
            flashColor(ACCENT);
        } else {
            showError("Немає що скасовувати!");
        }
    }

    private void flashColor(Color color) {
        Color original = displayField.getBackground();
        displayField.setBackground(color);
        javax.swing.Timer timer = new javax.swing.Timer(300, e -> displayField.setBackground(original));
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuperArrayCalculatorGUI().setVisible(true));
    }
}