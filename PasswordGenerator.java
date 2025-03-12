import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.security.SecureRandom;
import javax.swing.*;

public class PasswordGenerator extends JFrame {
    private JTextField passwordField;
    private JSpinner lengthSpinner;
    private JCheckBox numbersCheck, symbolsCheck, upperCaseCheck, lowerCaseCheck;
    private JLabel strengthLabel, charCountLabel;
    private JTextField customSymbolsField;

    public PasswordGenerator() {
        setTitle("Advanced Password Generator");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 1));
        setLocationRelativeTo(null);

        // Apply Dark Mode UI
        getContentPane().setBackground(new Color(30, 30, 30));
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("CheckBox.foreground", Color.WHITE);
        UIManager.put("CheckBox.background", new Color(30, 30, 30));
        UIManager.put("Button.background", new Color(50, 50, 50));
        UIManager.put("Button.foreground", Color.WHITE);

        // Password Field (Non-editable)
        passwordField = new JTextField();
        passwordField.setEditable(false);
        passwordField.setFont(new Font("Arial", Font.BOLD, 16));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        add(passwordField);

        // Character Count Label
        charCountLabel = new JLabel("Character Count: 12");
        add(charCountLabel);

        // Length Selector
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.add(new JLabel("Length:"));
        lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 32, 1));
        lengthSpinner.addChangeListener(e -> charCountLabel.setText("Character Count: " + lengthSpinner.getValue()));
        panel.add(lengthSpinner);
        add(panel);

        // Options
        numbersCheck = new JCheckBox("Include Numbers", true);
        symbolsCheck = new JCheckBox("Include Symbols", true);
        upperCaseCheck = new JCheckBox("Include Uppercase", true);
        lowerCaseCheck = new JCheckBox("Include Lowercase", true);

        add(numbersCheck);
        add(symbolsCheck);
        add(upperCaseCheck);
        add(lowerCaseCheck);

        // Custom Symbols Input
        JPanel customPanel = new JPanel();
        customPanel.setBackground(new Color(30, 30, 30));
        customPanel.add(new JLabel("Custom Symbols:"));
        customSymbolsField = new JTextField(10);
        customPanel.add(customSymbolsField);
        add(customPanel);

        // Strength Label
        strengthLabel = new JLabel("Strength: Unknown");
        add(strengthLabel);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(30, 30, 30));
        JButton generateButton = new JButton("Generate Password");
        JButton copyButton = new JButton("Copy to Clipboard");

        generateButton.addActionListener(this::generatePassword);
        copyButton.addActionListener(e -> copyToClipboard());

        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        add(buttonPanel);

        setVisible(true);
    }

    private void generatePassword(ActionEvent e) {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*()_+";
        String customSymbols = customSymbolsField.getText().trim();

        StringBuilder charSet = new StringBuilder();
        if (upperCaseCheck.isSelected()) charSet.append(upperCase);
        if (lowerCaseCheck.isSelected()) charSet.append(lowerCase);
        if (numbersCheck.isSelected()) charSet.append(numbers);
        if (symbolsCheck.isSelected()) charSet.append(symbols);
        if (!customSymbols.isEmpty()) charSet.append(customSymbols);

        int length = (int) lengthSpinner.getValue();

        if (charSet.length() == 0) {
            passwordField.setText("Select at least one option!");
            strengthLabel.setText("Strength: Invalid");
            return;
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each selected category
        if (upperCaseCheck.isSelected()) password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        if (lowerCaseCheck.isSelected()) password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        if (numbersCheck.isSelected()) password.append(numbers.charAt(random.nextInt(numbers.length())));
        if (symbolsCheck.isSelected()) password.append(symbols.charAt(random.nextInt(symbols.length())));
        if (!customSymbols.isEmpty()) password.append(customSymbols.charAt(random.nextInt(customSymbols.length())));

        // Fill the rest randomly
        while (password.length() < length) {
            password.append(charSet.charAt(random.nextInt(charSet.length())));
        }

        // Shuffle the password to mix the mandatory characters
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        String finalPassword = new String(passwordArray);
        passwordField.setText(finalPassword);
        updateStrength(finalPassword);
    }

    private void updateStrength(String password) {
        int score = 0;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+].*")) score++;
        if (password.length() >= 12) score++;

        String strength;
        switch (score) {
            case 5: strength = "Very Strong"; break;
            case 4: strength = "Strong"; break;
            case 3: strength = "Medium"; break;
            case 2: strength = "Weak"; break;
            default: strength = "Very Weak"; break;
        }
        strengthLabel.setText("Strength: " + strength);
    }

    private void copyToClipboard() {
        String password = passwordField.getText();
        if (!password.isEmpty() && !password.equals("Select at least one option!")) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            JOptionPane.showMessageDialog(this, "Password copied to clipboard!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordGenerator::new);
    }
}
