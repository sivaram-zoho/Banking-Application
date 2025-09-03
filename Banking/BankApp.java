package BankingApplication.Banking;

import java.util.*;

import BankingApplication.Banking.accountdetails.AccountsMenu;
import BankingApplication.Banking.accountdetails.ViewAccountDetails;
import BankingApplication.Banking.exceptions.InvalidInputException;
import BankingApplication.Banking.transactions.AccountTransactions;
import BankingApplication.Banking.transactions.TransactionMenu;

import java.sql.*;

public class BankApp {
    public static Connection con;

    public static void main(String[] args) {
        try {
            String url = "jdbc:postgresql://localhost:5432/banking";
            String user = "postgres";
            String password = "1234";
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected");
        } catch (SQLException e) {
            System.out.println("Error connection");
        }
        bankingOptions();
    }

    public static void bankingOptions() {
        Scanner scan = new Scanner(System.in);
        int option = 0;
        do {
            try {
                System.out.printf("%22s", "Welcome To MyBank");
                System.out.printf("%20s ", "\n---------------------------");
                System.out.println();
                System.out.println("1.Account Creation.");
                System.out.println("2.View Account Details.");
                System.out.println("3.Transactions.");
                System.out.println("4.Transaction History");
                System.out.println("5.Download Transaction.");
                System.out.println("6.Exit.");
                System.out.println();
                System.out.print("Enter Number to option option : ");
                option = scan.nextInt();
                switch (option) {
                    case 1:
                        AccountsMenu.accountManager();
                        break;
                    case 2:
                        ViewAccountDetails.AccountInfo();
                        break;
                    case 3:
                        TransactionMenu.transactionOptions();
                        break;
                    case 4:
                        AccountTransactions.transactionsHistory();
                        break;
                    case 5:
                        AccountTransactions.exportCsv();
                        break;
                    case 6:
                        System.out.println("Exit Successfully");
                        break;
                    default:
                        System.out.println("Invalid option try again ");
                        break;
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid Input!");
                scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input! Only numbers are allowed.");
                scan.nextLine();
            } catch (SQLException e) {
                System.out.println("Database Error");
                scan.nextLine();
            }
        }
        while (option != 6);
    }


}

