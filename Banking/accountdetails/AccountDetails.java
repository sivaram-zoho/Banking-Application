package BankingApplication.Banking.accountdetails;

public abstract class AccountDetails {
    protected long accountNumber;
    String name;
    String address;
    long phoneNumber;
    int interestRate;
    int age;
    public VariousAccount accountType;

    public enum VariousAccount {
        SAVINGS,
        CURRENT;
    }

    protected double balance;

    public AccountDetails(long accountNumber, String name, String address, long phoneNumber, double balance, int age, VariousAccount accountType) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.age = age;
        this.accountType = accountType;
    }

    public String toString() {
        return "\nAccount Number: " + accountNumber + "," +
                "\nName: " + name + "," +
                "\nAge: " + age + "," +
                "\nAddress: " + address + "," +
                "\nPhone Number: " + phoneNumber + "," +
                "\nAccount balance is: " + balance + "," +
                "\nAccount Type: " + accountType;
    }
}
