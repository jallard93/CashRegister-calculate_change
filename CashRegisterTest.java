/*
TODO:
    - Install Junit
    - Clean up repeated code here
*/
import java.util.Arrays;
import java.util.ArrayList;

public class CashRegisterTest {
    
    public static ArrayList<Double> bills = new ArrayList<>();
    public static ArrayList<Integer> billCounts = new ArrayList<>();
    public static int totalBills = 0;
    public static CashRegister cashRegister = new CashRegister(bills, billCounts, totalBills);

    // @Test
    public void testPurchaseGreaterThanPayment() {
        // expected output equal to empty ArrayList
        ArrayList<Double> expectedResult = new ArrayList<>();

        // initialize purchase > payment
        double purchaseAmount = 15.00;
        double paymentAmount = 10.00;

        ArrayList<Double> returnedChange = cashRegister.createChange(purchaseAmount, paymentAmount);

        assert(returnedChange == expectedResult);
    }

    // @Test
    public void testInsufficientBillAmounts() {
        // expected output equal to ArrayList with some coins missing...
        ArrayList<Double> expectedResult = new ArrayList<>(Arrays.asList(1.00, 1.00, 1.00, 1.00, 0.1, 0.1, 0.01, 0.01, 0.01));

        // initialize purchase and payment
        double purchaseAmount = 4.41;
        double paymentAmount = 5.00;
        // set bills + counts equal to low amounts
        ArrayList<Double> bills = new ArrayList<>(Arrays.asList(0.01, 0.05, 0.1, 0.25, 0.5, 1.00, 5.00, 10.00, 20.00));
        ArrayList<Integer> billCounts = new ArrayList<>(Arrays.asList(3, 0, 2, 0, 0, 4, 0, 0, 0));
        int totalBills = 0;
        for (int count : billCounts) {
            totalBills = totalBills + count;
        }
        
        // set bills, counts, and total
        cashRegister.setBills(bills);
        cashRegister.setBillCounts(billCounts);
        cashRegister.setTotalBills(totalBills);

        // get change from transaction
        ArrayList<Double> returnedChange = cashRegister.createChange(purchaseAmount, paymentAmount);

        assert(returnedChange == expectedResult);
    }

    // @Test
    public void testTransactions() {
        // expected output equal to ArrayList exactly
        ArrayList<Double> expectedResult = new ArrayList<>(Arrays.asList(10.00, 5.00, 1.00, 0.25, 0.10, 0.01, 0.01, 0.01));

        // initialize purchase and payment
        double purchaseAmount = 3.62;
        double paymentAmount = 20.00;
        // set bills + counts equal to low amounts
        ArrayList<Double> bills = new ArrayList<>(Arrays.asList(0.01, 0.05, 0.1, 0.25, 0.5, 1.00, 5.00, 10.00, 20.00));
        ArrayList<Integer> billCounts = new ArrayList<>(Arrays.asList(5, 5, 5, 5, 5, 5, 5, 5, 5));
        int totalBills = 0;
        for (int count : billCounts) {
            totalBills = totalBills + count;
        }
        
        // set bills, counts, and total
        cashRegister.setBills(bills);
        cashRegister.setBillCounts(billCounts);
        cashRegister.setTotalBills(totalBills);

        // get change from transaction
        ArrayList<Double> returnedChange = cashRegister.createChange(purchaseAmount, paymentAmount);

        assert(returnedChange == expectedResult);
    }

    // @Test
    public void testReplenishedAmount() {
        // expected output equal to ArrayList exactly
        ArrayList<Double> expectedResult = new ArrayList<>(Arrays.asList(6, 1, 1, 2, 1, 2, 1, 1, 0));

        // initialize purchase and payment
        double purchaseAmount = 12.62;
        double paymentAmount = 17.89;
        // set bills + counts equal to low amounts
        ArrayList<Double> bills = new ArrayList<>(Arrays.asList(0.01, 0.05, 0.1, 0.25, 0.5, 1.00, 5.00, 10.00, 20.00));
        ArrayList<Integer> billCounts = new ArrayList<>(Arrays.asList(4, 1, 0, 2, 0, 0, 1, 0, 0));
        
        int totalBills = 0;
        for (int count : billCounts) {
            totalBills = totalBills + count;
        }
        
        // set bills, counts, and total
        cashRegister.setBills(bills);
        cashRegister.setBillCounts(billCounts);
        cashRegister.setTotalBills(totalBills);

        // run the transaction, no need to store the change
        cashRegister.createChange(purchaseAmount, paymentAmount);
        // get the bill counts to ensure replenishing worked as expected
        ArrayList<Integer> replenishedBills = cashRegister.getBillCounts();

        assert(replenishedBills == expectedResult);
    }

    public static void main(String[] args) {
        CashRegisterTest registerTest = new CashRegisterTest();
        registerTest.testPurchaseGreaterThanPayment();
        registerTest.testInsufficientBillAmounts();
        registerTest.testTransactions();
        registerTest.testReplenishedAmount();
    }
}
