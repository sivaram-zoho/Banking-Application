package BankingApplication.Banking.accountdetails;

import BankingApplication.Banking.BankApp;

import java.sql.*;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ViewAccountDetails {

    public static void AccountInfo() throws InputMismatchException, SQLException {

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter AccountNumber : ");
        long accountNumber = scan.nextLong();

        Connection con = BankApp.con;
        String sql = "SELECT * FROM account WHERE account_id = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setLong(1, accountNumber);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            System.out.println("Account Details");
            System.out.println("-------------------");
            System.out.printf("Account Number: %d\n", resultSet.getLong("account_id"));
            System.out.printf("Name: %s\n", resultSet.getString("name"));
            System.out.printf("Age: %d\n", resultSet.getInt("age"));
            System.out.printf("Address: %s\n", resultSet.getString("address"));
            System.out.printf("PhoneNumber: %d\n", resultSet.getLong("phone"));
            System.out.printf("AccountType: %s\n", resultSet.getString("accounttype"));
            System.out.printf("Account Balance: %.2f\n", resultSet.getDouble("balance"));
        } else {
            System.out.println("Account Number Doesn't Exists");
        }
    }
}
