package BankingApplication.Banking.accountdetails;

import BankingApplication.Banking.exceptions.*;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AccountsMenu {
    public static void accountManager() {
        Scanner scan = new Scanner(System.in);
        try {
            System.out.println("\nAccount Manager");
            System.out.println("1.Create Account.");
            System.out.println("2.Delete Account.");
            System.out.println("3.Edit Account.");
            System.out.print("Enter your option: ");
            int option = scan.nextInt();

            switch (option) {
                case 1:
                    AccountsManagement.createAccount();
                    break;
                case 2:
                    AccountsManagement.deleteAccount();
                    break;
                case 3:
                    AccountsManagement.editAccount();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (InputMismatchException | InvalidInputException e) {
            System.out.println("Invalid Input!");
        } catch (InvalidNameException e) {
            System.out.println("Invalid name!, Enter only alphabets");
        } catch (InvalidAddressException e) {
            System.out.println("Address is invalid,Please enter correct address format.");
        } catch (InvalidPhoneNumberException e) {
            System.out.println("Invalid phone number,Enter numbers only");
        } catch (InvalidAgeException e) {
            System.out.println("Invalid Age!");
        } catch (SQLException e) {
            System.out.println("Error on Database" + e);
        }
    }
}