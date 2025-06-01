import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Account {
    String name;
    String accNo;
    double bal;

    Account(String name, String accNo, double bal) {
        this.name = name;
        this.accNo = accNo;
        this.bal = bal;
    }

    void addMoney(double amt) {
        if (amt > 0) {
            bal += amt;
        }
    }

    boolean takeMoney(double amt) {
        if (amt > 0 && amt <= bal) {
            bal -= amt;
            return true;
        }
        return false;
    }

    boolean send(Account to, double amt) {
        if (amt > 0 && amt <= bal) {
            this.bal -= amt;
            to.bal += amt;
            return true;
        }
        return false;
    }

    String showBal() {
        return String.format("%.2f", bal);
    }
}

public class BankingApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankingApp::createMainFrame);
    }

    private static void createMainFrame() {
        Account a1 = new Account("Laksh", "ACC123", 10000);
        Account a2 = new Account("Kiran", "ACC456", 8000);
        Account a3 = new Account("Meena", "ACC789", 6000);

        Account[] accs = {a1, a2, a3};

        JFrame frame = new JFrame("Banking Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(5, 1));
        frame.add(panel);

        JLabel welcomeLabel = new JLabel("Select Account:", JLabel.CENTER);
        panel.add(welcomeLabel);

        for (Account curr : accs) {
            JButton accountButton = new JButton(curr.name);
            accountButton.addActionListener(e -> openAccountWindow(curr, accs));
            panel.add(accountButton);
        }

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton);

        frame.setVisible(true);
    }

    private static void openAccountWindow(Account curr, Account[] accs) {
        JFrame accountFrame = new JFrame("Account: " + curr.name);
        accountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        accountFrame.setSize(400, 400);

        JPanel panel = new JPanel(new GridLayout(6, 1));
        accountFrame.add(panel);

        JLabel balanceLabel = new JLabel("Balance: " + curr.showBal(), JLabel.CENTER);
        panel.add(balanceLabel);

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> {
            String amtStr = JOptionPane.showInputDialog(accountFrame, "Enter amount to deposit:");
            if (amtStr != null && !amtStr.isEmpty()) {
                try {
                    double amt = Double.parseDouble(amtStr);
                    curr.addMoney(amt);
                    balanceLabel.setText("Balance: " + curr.showBal());
                    saveTransaction(curr.name, "Deposit", amt, curr.bal);
                    JOptionPane.showMessageDialog(accountFrame, "Deposit successful.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(accountFrame, "Invalid amount. Please enter a number.");
                }
            }
        });
        panel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> {
            String amtStr = JOptionPane.showInputDialog(accountFrame, "Enter amount to withdraw:");
            if (amtStr != null && !amtStr.isEmpty()) {
                try {
                    double amt = Double.parseDouble(amtStr);
                    if (curr.takeMoney(amt)) {
                        balanceLabel.setText("Balance: " + curr.showBal());
                        saveTransaction(curr.name, "Withdraw", amt, curr.bal);
                        JOptionPane.showMessageDialog(accountFrame, "Withdrawal successful.");
                    } else {
                        JOptionPane.showMessageDialog(accountFrame, "Insufficient funds or invalid amount.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(accountFrame, "Invalid amount. Please enter a number.");
                }
            }
        });
        panel.add(withdrawButton);

        JButton transferButton = new JButton("Transfer");
        transferButton.addActionListener(e -> {
            String[] recipientNames = new String[accs.length - 1];
            Account[] recipients = new Account[accs.length - 1];
            int index = 0;
            for (Account acc : accs) {
                if (acc != curr) {
                    recipientNames[index] = acc.name;
                    recipients[index] = acc;
                    index++;
                }
            }

            String recipientName = (String) JOptionPane.showInputDialog(accountFrame, "Select recipient:", "Transfer",
                    JOptionPane.QUESTION_MESSAGE, null, recipientNames, recipientNames[0]);

            if (recipientName != null) {
                Account recipient = null;
                for (Account acc : recipients) {
                    if (acc.name.equals(recipientName)) {
                        recipient = acc;
                        break;
                    }
                }
                if (recipient != null) {
                    String amtStr = JOptionPane.showInputDialog(accountFrame, "Enter amount to transfer:");
                    if (amtStr != null && !amtStr.isEmpty()) {
                        try {
                            double amt = Double.parseDouble(amtStr);
                            if (curr.send(recipient, amt)) {
                                balanceLabel.setText("Balance: " + curr.showBal());
                                saveTransaction(curr.name, "Transfer Sent", amt, curr.bal);
                                saveTransaction(recipient.name, "Transfer Received", amt, recipient.bal);
                                JOptionPane.showMessageDialog(accountFrame, "Transfer successful to " + recipientName);
                            } else {
                                JOptionPane.showMessageDialog(accountFrame, "Insufficient funds or invalid amount.");
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(accountFrame, "Invalid amount. Please enter a number.");
                        }
                    }
                }
            }
        });
        panel.add(transferButton);

        JButton backButton = new JButton("Logout");
        backButton.addActionListener(e -> accountFrame.dispose());
        panel.add(backButton);

        accountFrame.setVisible(true);
    }

    private static Connection connectToDB() {
        try {
            String path = "BankingDB.accdb";
            String url = "jdbc:ucanaccess://" + path;
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed.");
            return null;
        }
    }

    private static void saveTransaction(String accName, String type, double amt, double balance) {
        try (Connection conn = connectToDB()) {
            if (conn == null) return;

            String query = "INSERT INTO Transactions (AccountName, Type, Amount, Balance, DateTime) VALUES (?, ?, ?, ?, NOW())";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, accName);
                pstmt.setString(2, type);
                pstmt.setDouble(3, amt);
                pstmt.setDouble(4, balance);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save transaction.");
        }
    }
}

