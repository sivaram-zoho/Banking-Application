package BankingApplication.Banking.accountdetails;

import BankingApplication.Banking.BankApp;
import BankingApplication.Banking.exceptions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountsManagement {

    public static void createAccount() throws InvalidAgeException, InvalidAddressException, InvalidNameException, InvalidPhoneNumberException, InvalidInputException, InvalidInputException, SQLException {
        Scanner scan = new Scanner(System.in);
        int option = 0;
        System.out.printf("%22s", "Select to Create Account type");
        System.out.printf("%20s ", "\n---------------------------");
        System.out.println();
        System.out.println("1.Savings Account.");
        System.out.println("2.Current Account.");
        System.out.println();
        System.out.print("Enter Number to select option : ");
        option = scan.nextInt();
        switch (option) {
            case 1:
                AccountCreation.createAccount(AccountCreation.VariousAccount.SAVINGS);
                break;
            case 2:
                AccountCreation.createAccount(AccountCreation.VariousAccount.CURRENT);
                break;
            default:
                System.out.println("Invalid input");
                break;
        }
    }

    public static void deleteAccount() throws InputMismatchException, SQLException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter account number to delete: ");
        long accountNumber = scan.nextLong();
        Connection con = BankApp.con;

        String sql = "SELECT * FROM account WHERE account_id = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setLong(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String deleteSqlTrans = "DELETE FROM Transaction WHERE account_id=?";
            PreparedStatement deleteTransactions = con.prepareStatement(deleteSqlTrans);
            deleteTransactions.setLong(1, accountNumber);
            int rowsDeleteTrans = deleteTransactions.executeUpdate();

            if (rowsDeleteTrans > 0) {
                System.out.println("Account Transactions Deleted");
            } else {
                System.out.println("Account Transactions Not Deleted");
            }
            String deleteSql = "DELETE FROM account WHERE account_id=?";
            PreparedStatement delete = con.prepareStatement(deleteSql);
            delete.setLong(1, accountNumber);
            int rowsDelete = delete.executeUpdate();

            if (rowsDelete > 0) {
                System.out.println("Account Deleted");
            } else {
                System.out.println("Failed to Delete Account");
            }
            deleteTransactions.close();
            delete.close();

        } else {
            System.out.println("Account Number Doesn't Exists");
        }
    }

    public static void editAccount() throws InputMismatchException, SQLException, InvalidPhoneNumberException, InvalidNameException, InvalidAddressException {
        Scanner scan = new Scanner(System.in);
        int option;
        System.out.print("Enter account number to Edit: ");
        long accountNumber = scan.nextLong();
        scan.nextLine();

        Connection con = BankApp.con;
        String sql = "SELECT * FROM account WHERE account_id = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setLong(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            System.out.println("Select Account Details");
            System.out.println("-------------------");
            System.out.printf("Account Number: %d\n", resultSet.getLong("account_id"));
            System.out.printf("Name: %s\n", resultSet.getString("name"));
            System.out.printf("Age: %d\n", resultSet.getInt("age"));
            System.out.printf("Address: %s\n", resultSet.getString("address"));
            System.out.printf("PhoneNumber: %d\n", resultSet.getLong("phone"));
            System.out.printf("AccountType: %s\n", resultSet.getString("accounttype"));
            System.out.printf("Account Balance: %.2f\n", resultSet.getDouble("balance"));

            System.out.println("1.Edit name");
            System.out.println("2.Edit Address");
            System.out.println("3.Edit PhoneNumber");
            System.out.print("Enter your option: ");
            option = scan.nextInt();
            scan.nextLine();

            String updateSql = null;
            PreparedStatement updateStmt = null;

            switch (option) {
                case 1:
                    System.out.print("Enter New Name: ");
                    String newName = scan.nextLine();
                    if (!newName.matches("[a-zA-Z ]+")) {
                        throw new InvalidNameException("");
                    }
                    updateSql = "UPDATE account SET name =? WHERE account_id=?";
                    updateStmt = con.prepareStatement(updateSql);
                    updateStmt.setString(1, newName);
                    updateStmt.setLong(2, accountNumber);
                    updateStmt.executeUpdate();
                    System.out.println("Name Updated");
                    break;
                case 2:
                    System.out.print("Enter New Address: ");
                    String newAddress = scan.nextLine();
                    if (!newAddress.matches("^[0-9 a-zA-Z,.'-]+$")) {
                        throw new InvalidAddressException("");
                    }
                    updateSql = "UPDATE account SET address =? WHERE account_id=?";
                    updateStmt = con.prepareStatement(updateSql);
                    updateStmt.setString(1, newAddress);
                    updateStmt.setLong(2, accountNumber);
                    updateStmt.executeUpdate();
                    System.out.println("Address Updated");
                    break;
                case 3:
                    System.out.print("Enter New Phone Number: ");
                    long phoneNumber = scan.nextLong();
                    String phoneNumberString = String.valueOf(phoneNumber);
                    if (!phoneNumberString.matches("^[0-9]+$")) {
                        throw new InvalidPhoneNumberException("");
                    }
                    updateSql = "UPDATE account SET phone =? WHERE account_id=? ";
                    updateStmt = con.prepareStatement(updateSql);
                    updateStmt.setLong(1, phoneNumber);
                    updateStmt.setLong(2, accountNumber);
                    updateStmt.executeUpdate();
                    System.out.println("Phone Number Updated");
                    break;
                default:
                    System.out.println("Invalid option");
                    return;
            }

            updateStmt.close();

        } else {
            System.out.println("Account Number Doesn't Exists");
        }
        statement.close();
    }
}

