package BankingApplication.Banking.accountdetails;

import BankingApplication.Banking.BankApp;
import BankingApplication.Banking.exceptions.*;

import java.sql.*;
import java.util.Scanner;

public class AccountCreation extends AccountDetails {

    public AccountCreation(long accountNumber, String name, String address, long phoneNumber, double balance, int age, VariousAccount accountType) {
        super(accountNumber, name, address, phoneNumber, balance, age, accountType);
    }

    public static void createAccount(VariousAccount accountType) throws InvalidNameException, InvalidAddressException, InvalidAgeException, InvalidPhoneNumberException, SQLException {
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter your name : ");
        String name = scan.nextLine();
        if (!name.matches("[a-zA-Z ]+")) {
            throw new InvalidNameException("");
        }

        System.out.print("Enter your age : ");
        int age = scan.nextInt();
        if (age < 0 || age > 60) {
            throw new InvalidAgeException("");
        }
        scan.nextLine();

        System.out.print("Enter your address : ");
        String address = scan.nextLine();
        if (!address.matches("^[0-9 a-zA-Z,.'-]+$")) {
            throw new InvalidAddressException("");
        }

        System.out.print("Enter your phone number : ");
        long phoneNumber = scan.nextLong();
        String phoneNumberString = String.valueOf(phoneNumber);
        if (!phoneNumberString.matches("^[0-9]+$")) {
            throw new InvalidPhoneNumberException("");
        }


        double balance = accountType == VariousAccount.SAVINGS ? 500 : 0;

        Connection con = BankApp.con;

        String sql = "INSERT INTO account (name, age, address, phone, accounttype,balance) VALUES (?, ?, ?, ?, ?,?)";

        PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, name);
        statement.setInt(2, age);
        statement.setString(3, address);
        statement.setLong(4, phoneNumber);
        statement.setString(5, accountType.toString());
        statement.setDouble(6, balance);

        int rowsInserted = statement.executeUpdate();

        if (rowsInserted > 0) {
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                long accountID = rs.getLong(1);
                System.out.println("Account Number: " + accountID);

                String balanceQuery = "SELECT Balance FROM Account WHERE Account_id = ?";
                PreparedStatement balStmt = con.prepareStatement(balanceQuery);
                balStmt.setLong(1, accountID);
                ResultSet balRs = balStmt.executeQuery();

                if (balRs.next()) {
                    double balanceNew = balRs.getDouble("Balance");
                    System.out.println("Account Balance: " + balanceNew);
                }
                balStmt.close();
            }
        } else {
            System.out.println("Account creation failed.");
        }
        statement.close();
    }


}