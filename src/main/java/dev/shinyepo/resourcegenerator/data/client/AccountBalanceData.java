package dev.shinyepo.resourcegenerator.data.client;

public class AccountBalanceData {
    private static Long balance = 0L;

    public static void setBalance(Long balance) {
        AccountBalanceData.balance = balance;
    }

    public static Long getBalance() {
        return balance;
    }
}
