package BankingApplication.Banking.transactions;

import BankingApplication.Banking.exceptions.InsufficientBalanceException;
import BankingApplication.Banking.exceptions.InvalidInputException;
import BankingApplication.Banking.exceptions.MinimumBalanceException;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TransactionMenu {

    public static void transactionOptions() {
        Scanner scan = new Scanner(System.in);

        int option;
        try {
            System.out.printf("%20s ", "\n---------------------------");
            System.out.println();
            System.out.println("1.Deposit.");
            System.out.println("2.Withdraw.");
            System.out.println("3.Fund Transfer.");
            System.out.println("4.Add Interest.");
            System.out.println("5.Exit.");
            System.out.println();
            System.out.print("Enter Number to option option : ");
            option = scan.nextInt();
            switch (option) {
                case 1:
                    AccountTransactions.depositAmount();
                    break;
                case 2:
                    AccountTransactions.withdrawAmount();
                    break;
                case 3:
                    AccountTransactions.fundsTransfer();
                    break;
                case 4:
                    AccountTransactions.addInterest();
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid option try again ");
                    break;
            }
        } catch (InvalidInputException e) {
            System.out.println("Invalid Input! Only numbers are allowed.");
        } catch (InsufficientBalanceException e) {
            System.out.println("Your Account have Insufficient Balance");
        } catch (InputMismatchException e) {
            System.out.println("Invalid Input!");
        } catch (MinimumBalanceException e) {
            System.out.println("Sorry Minimum balance must have Required 500");
        }catch (SQLException e){
            System.out.println("Error");
        }
    }
}
