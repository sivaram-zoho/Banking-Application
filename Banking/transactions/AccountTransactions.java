package BankingApplication.Banking.transactions;

import BankingApplication.Banking.BankApp;
import BankingApplication.Banking.exceptions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.sql.*;

import com.opencsv.CSVWriter;

public class AccountTransactions {

    public static void setAccountTransactions(long accountNumber, String transaction) {

        try {
            Connection con = BankApp.con;

            String transactionType = "";
            double amount = 0.0;

            if (transaction.startsWith("Amount Deposited-")) {
                transactionType = "Deposit";
                amount = Double.parseDouble(transaction.replace("Amount Deposited- ", ""));
            } else if (transaction.startsWith("Amount Withdrawn-")) {
                transactionType = "Withdraw";
                amount = Double.parseDouble(transaction.replace("Amount Withdrawn- ", ""));
            } else if (transaction.startsWith("Amount-") && transaction.contains("Transferred To")) {
                transactionType = "Fund Transfer";
                amount = Double.parseDouble(transaction.split(" ")[1]);
            } else if (transaction.startsWith("Amount-") && transaction.contains("Received From")) {
                transactionType = "Fund Transfer";
                amount = Double.parseDouble(transaction.split(" ")[1]);
            } else if (transaction.startsWith("Interest Added -")) {
                transactionType = "Interest Added";
                amount = Double.parseDouble(transaction.replace("Interest Added - ", ""));
            }

            String sql = "INSERT INTO transaction (account_id, transactiontime, transactiontype, amount) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setLong(1, accountNumber);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(3, transactionType);
            statement.setDouble(4, amount);
            statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            System.out.println("Database Error " + e);
        }


    }

    public synchronized static void depositAmount() throws InvalidInputException, InputMismatchException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter AccountNumber to deposit: ");
        String accountNumberStr = scan.nextLine();

        if (!accountNumberStr.matches("[0-9]+")) {
            throw new InvalidInputException("");
        }
        long accountNumber = Long.parseLong(accountNumberStr);

        try {
            Connection con = BankApp.con;

            String checkQuery = "SELECT balance FROM account WHERE account_id = ?";
            PreparedStatement statement = con.prepareStatement(checkQuery);
            statement.setLong(1, accountNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                System.out.print("Enter amount to deposit: ");
                double depositAmount = scan.nextDouble();
                if (depositAmount <= 0) {
                    System.out.println("Please enter amount above 0.");
                    return;
                }
                double newBalance = currentBalance + depositAmount;

                String updateQuery = "UPDATE account SET balance = ? WHERE account_id = ?";
                PreparedStatement updateStatement = con.prepareStatement(updateQuery);
                updateStatement.setDouble(1, newBalance);
                updateStatement.setLong(2, accountNumber);
                updateStatement.executeUpdate();
                updateStatement.close();

                setAccountTransactions(accountNumber, "Amount Deposited- " + depositAmount);
                System.out.println("Balance updated.");
                System.out.println("New balance is: " + newBalance);

            } else {
                System.out.println("Account number doesn't Exist");
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    public synchronized static void withdrawAmount() throws
            InvalidInputException, MinimumBalanceException, InsufficientBalanceException, InputMismatchException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter AccountNumber for Withdraw : ");
        String accountNumberStr = scan.nextLine();

        if (!accountNumberStr.matches("[0-9]+")) {
            throw new InvalidInputException("");
        }
        long accountNumber = Long.parseLong(accountNumberStr);

        try {
            Connection con = BankApp.con;

            String checkQuery = "SELECT balance, accountType FROM account WHERE account_id = ?";
            PreparedStatement statement = con.prepareStatement(checkQuery);
            statement.setLong(1, accountNumber);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                String accountType = rs.getString("accountType");
                System.out.print("Enter amount to withdraw: ");
                double withdrawAmount = scan.nextDouble();
                if (withdrawAmount <= 0) {
                    System.out.println("Please enter amount above 0.");
                    return;
                }
                if (withdrawAmount > currentBalance) {
                    throw new InsufficientBalanceException("");
                }
                if (accountType.equals("SAVINGS") && (currentBalance - withdrawAmount < 500)) {
                    throw new MinimumBalanceException("");
                }
                double newBalance = currentBalance - withdrawAmount;
                String updateQuery = "UPDATE account SET balance = ? WHERE account_id = ?";
                PreparedStatement updateStatement = con.prepareStatement(updateQuery);
                updateStatement.setDouble(1, newBalance);
                updateStatement.setLong(2, accountNumber);
                updateStatement.executeUpdate();

                setAccountTransactions(accountNumber, "Amount Withdrawn- " + withdrawAmount);
                System.out.println("Balance updated.");
                System.out.println("New balance is: " + newBalance);

                updateStatement.close();
            } else {
                System.out.println("Account number doesn't Exist");
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }

    }

    public synchronized static void fundsTransfer() throws
            InvalidInputException, InsufficientBalanceException, InputMismatchException, MinimumBalanceException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter Account number transfer from : ");
        String accountNumberStr = scan.nextLine();

        if (!accountNumberStr.matches("[0-9]+")) {
            throw new InvalidInputException("");
        }
        long accountNumberFrom = Long.parseLong(accountNumberStr);

        try {
            Connection con = BankApp.con;

            String checkQuery = "SELECT balance, accountType FROM account WHERE account_id = ?";
            PreparedStatement fromStatement = con.prepareStatement(checkQuery);
            fromStatement.setLong(1, accountNumberFrom);
            ResultSet rsFrom = fromStatement.executeQuery();
            if (rsFrom.next()) {
                double fromBalance = rsFrom.getDouble("balance");
                String accountType = rsFrom.getString("accountType");
                System.out.print("Enter amount to transfer: ");
                double amount = scan.nextDouble();
                if (amount <= 0) {
                    System.out.println("Please enter amount above 0.");
                    return;
                }
                if (amount > fromBalance) {
                    throw new InsufficientBalanceException("");
                }
                if (accountType.equals("SAVINGS") && (fromBalance - amount < 500)) {
                    throw new MinimumBalanceException("");
                }

                System.out.print("Enter Account number transfer to: ");
                long accountNumberTo = scan.nextLong();

                PreparedStatement toStatement = con.prepareStatement("SELECT balance FROM account WHERE account_id=?");
                toStatement.setLong(1, accountNumberTo);
                ResultSet rsTo = toStatement.executeQuery();

                if (rsTo.next()) {
                    double toBalance = rsTo.getDouble("balance");
                    PreparedStatement updateFromAccount = con.prepareStatement("UPDATE account SET balance = ? WHERE account_id = ?");
                    updateFromAccount.setDouble(1, fromBalance - amount);
                    updateFromAccount.setLong(2, accountNumberFrom);
                    updateFromAccount.executeUpdate();

                    PreparedStatement updateToAccount = con.prepareStatement("UPDATE account SET balance = ? WHERE account_id = ?");
                    updateToAccount.setDouble(1, toBalance + amount);
                    updateToAccount.setLong(2, accountNumberTo);
                    updateToAccount.executeUpdate();

                    setAccountTransactions(accountNumberFrom, "Amount- " + amount + " Transferred To Account Number- " + accountNumberTo);
                    setAccountTransactions(accountNumberTo, "Amount- " + amount + " Received From Account Number- " + accountNumberFrom);
                    System.out.println("Account Transfer Updated");
                }

                toStatement.close();
            } else {
                System.out.println("Account number doesn't Exist");
            }
            fromStatement.close();
        } catch (SQLException e) {
            System.out.println("Error " + e);
        }
    }

    public static void addInterest() throws InvalidInputException, InputMismatchException, SQLException {
        System.out.println("Adding 3% interest to all SAVINGS accounts...");

        try {
            Connection con = BankApp.con;

            String selectQuery = "SELECT account_id, balance FROM account WHERE accounttype = 'SAVINGS'";
            PreparedStatement statement = con.prepareStatement(selectQuery);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                long accountId = rs.getLong("account_id");
                double balance = rs.getDouble("balance");
                double interest = balance * 0.03;
                double newBalance = balance + interest;

                PreparedStatement updateStmt = con.prepareStatement("UPDATE account SET balance = ? WHERE account_id = ?");
                updateStmt.setDouble(1, newBalance);
                updateStmt.setLong(2, accountId);
                updateStmt.executeUpdate();
                updateStmt.close();
                setAccountTransactions(accountId, "Interest Added - " + interest);

            }

            statement.close();
        } catch (SQLException e) {
            System.out.println("Error");
        }
    }

    public static void transactionsHistory() throws InvalidInputException, InputMismatchException, SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter Account Number for View Transactions: ");
        String accountNumberStr = scan.nextLine();

        if (!accountNumberStr.matches("[0-9]+")) {
            throw new InvalidInputException("");
        }
        long accountNumber = Long.parseLong(accountNumberStr);
        Connection con = BankApp.con;

        String checkSql = "SELECT COUNT(*) FROM account WHERE account_id = ?";
        PreparedStatement checkStatement = con.prepareStatement(checkSql);
        checkStatement.setLong(1, accountNumber);

        ResultSet rsCheck = checkStatement.executeQuery();
        rsCheck.next();
        int account = rsCheck.getInt(1);

        if (account == 0) {
            System.out.println("Account Number Doesn't Exists");
            checkStatement.close();
            return;
        }
        checkStatement.close();

        String sql = "SELECT transactionTime,transactionType,amount FROM Transaction WHERE account_id=? ORDER BY transactionTime DESC LIMIT 10";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setLong(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();

        System.out.println("Last 10 Transaction For Account Number - " + accountNumber);

        boolean trans = false;
        System.out.println("Transaction Time   -   Transaction Type   -   Transaction Amount ");

        while (resultSet.next()) {
            trans = true;
            Timestamp transactionTime = resultSet.getTimestamp("transactionTime");
            String transactionType = resultSet.getString("transactionType");
            double amount = resultSet.getDouble("amount");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm");
            String formateTime = dateFormat.format(transactionTime);
            System.out.printf("%s  %17s  %20.2f", formateTime, transactionType, amount);
            System.out.println();
        }
        if (!trans) {
            System.out.println("No Transactions");
        }
        resultSet.close();
        statement.close();
    }

    public static void exportCsv() throws InvalidInputException, SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter Account number to Download Transactions: ");
        String accountNumberStr = scan.nextLine();

        if (!accountNumberStr.matches("[0-9]+")) {
            throw new InvalidInputException("");
        }
        long accountNumber = Long.parseLong(accountNumberStr);

        Connection con = BankApp.con;
        String checkSql = "SELECT COUNT(*) FROM account WHERE account_id = ?";
        PreparedStatement checkStatement = con.prepareStatement(checkSql);
        checkStatement.setLong(1, accountNumber);

        ResultSet rsCheck = checkStatement.executeQuery();
        rsCheck.next();
        int account = rsCheck.getInt(1);

        if (account == 0) {
            System.out.println("Account Number Doesn't Exists");
            checkStatement.close();
            return;
        }
        checkStatement.close();

        String sql = "SELECT transactionTime,transactionType,amount FROM Transaction WHERE account_id=? ORDER BY transactionTime ";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setLong(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next()) {
            System.out.println("No Transactions");
        }


        String fileName = "Transactions" + accountNumber + ".csv";


        try (FileWriter writer = new FileWriter(fileName);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] header = {"AccountNumber", "TransactionTime", "TransactionType", "Amount"};
            csvWriter.writeNext(header);

            do {
                Timestamp transactionTime = resultSet.getTimestamp("transactionTime");
                String transactionType = resultSet.getString("transactionType");
                double amount = resultSet.getDouble("amount");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm");
                String formateTime = dateFormat.format(transactionTime);
                String[] data={
                        String.valueOf(accountNumber),
                        formateTime,
                        transactionType,
                        String.format("%.2f",amount)
                };
                csvWriter.writeNext(data);
            } while (resultSet.next());

            System.out.println("Download Successfully");
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

}