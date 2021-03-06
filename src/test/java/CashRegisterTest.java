import org.junit.Test;
import cashregister.CashRegister;
import java.util.Arrays;
import java.util.ArrayList;

public class CashRegisterTest {
    
    public static ArrayList<Double> bills = new ArrayList<>();
    public static ArrayList<Integer> billCounts = new ArrayList<>();
    public static int totalBills = 0;
    public static CashRegister cashRegister = new CashRegister(bills, billCounts, totalBills);

    // prices and payments for testing purchase > payment
    public static double[] purchasePricesGreater = new double[] {12.89, 19.99, 1.50, 0.54, 7.45};
    public static double[] paymentAmountsGreater = new double[] {10,    15.00, 0.84, 0.39, 5.00};

    // prices, payments, results for insufficient bill amounts in register
    public static double[] purchasePricesInsufficient = new double[] {2.68,  4.57,  14.31};
    public static double[] paymentAmountsInsufficient = new double[] {10.00, 10.00, 20.00};
    public static ArrayList<ArrayList<Integer>> billCountsInsufficient = new ArrayList<>();
    public static ArrayList<ArrayList<Double>> expectedResultsInsufficient = new ArrayList<>();
    public static int[] totalBillsInsufficient = new int[] {40, 40, 35};

    // prices, payments, results for regular transaction tests
    public static double[] purchasePricesTransaction = new double[] {15.42, 0.44, 9.64};
    public static double[] paymentAmountsTransaction = new double[] {16.00, 1.04, 20.00};
    public static ArrayList<ArrayList<Double>> expectedResultsTransactions = new ArrayList<>();
    public static ArrayList<ArrayList<Integer>> expectedReplenished = new ArrayList<>();
    

    public CashRegisterTest() {
        billCountsInsufficient.add(new ArrayList<Integer>(Arrays.asList(0, 5, 5, 5, 5, 5, 5, 5, 5)));
        billCountsInsufficient.add(new ArrayList<Integer>(Arrays.asList(0, 0, 0, 1, 0, 5, 5, 5, 5)));
        billCountsInsufficient.add(new ArrayList<Integer>(Arrays.asList(4, 0, 0, 5, 5, 5, 5, 5, 5)));

        expectedResultsInsufficient.add(new ArrayList<Double>(Arrays.asList(5.0, 1.00, 1.00, 0.25, 0.05)));
        expectedResultsInsufficient.add(new ArrayList<Double>(Arrays.asList(5.0, 0.25)));
        expectedResultsInsufficient.add(new ArrayList<Double>(Arrays.asList(5.0, 0.5, 0.01, 0.01, 0.01, 0.01)));

        expectedResultsTransactions.add(new ArrayList<Double>(Arrays.asList(0.5, 0.05, 0.01, 0.01, 0.01)));
        expectedResultsTransactions.add(new ArrayList<Double>(Arrays.asList(0.5, 0.1)));
        expectedResultsTransactions.add(new ArrayList<Double>(Arrays.asList(10.0, 0.25, 0.1, 0.01)));

        expectedReplenished.add(new ArrayList<Integer>(Arrays.asList(2, 4, 5, 5, 4, 6, 6, 6, 5)));
        expectedReplenished.add(new ArrayList<Integer>(Arrays.asList(9, 5, 4, 5, 4, 6, 5, 5, 5)));
        expectedReplenished.add(new ArrayList<Integer>(Arrays.asList(4, 5, 4, 4, 5, 5, 5, 4, 6)));
    }

    @Test
    public void testPurchaseGreaterThanPayment() {
        // expected output equal to empty ArrayList
        ArrayList<Double> expectedResult = new ArrayList<>();

        for (int i=0; i < this.purchasePricesGreater.length; i++) {
            double purchaseAmount = purchasePricesGreater[i];
            double paymentAmount = paymentAmountsGreater[i];

            ArrayList<Double> returnedChange = cashRegister.calculateChange(purchaseAmount, paymentAmount);

            if (!returnedChange.equals(expectedResult)) {
                System.out.println("Returned: " + returnedChange);
                System.out.println("Expected: " + expectedResult);
            }
            
            assert(returnedChange.equals(expectedResult));
        }
    }

    @Test
    public void testInsufficientBillAmounts() {
        // expected output equal to ArrayList with some coins missing...
        for (int i=0; i < this.purchasePricesInsufficient.length; i++) {
            ArrayList<Double> expectedResult = this.expectedResultsInsufficient.get(i);

            // initialize purchase and payment
            double purchaseAmount = purchasePricesInsufficient[i];
            double paymentAmount = this.paymentAmountsInsufficient[i];

            // get bills and counts
            ArrayList<Double> bills = new ArrayList<>(Arrays.asList(0.01, 0.05, 0.1, 0.25, 0.5, 1.00, 5.00, 10.00, 20.00));
            ArrayList<Integer> billCounts = billCountsInsufficient.get(i);
            int totalBills = totalBillsInsufficient[i];

            // set bills, counts, and total
            cashRegister.setBills(bills);
            cashRegister.setBillCounts(billCounts);
            cashRegister.setTotalBills(totalBills);

            // get change from transaction
            ArrayList<Double> returnedChange = cashRegister.calculateChange(purchaseAmount, paymentAmount);

            if (!returnedChange.equals(expectedResult)) {
                System.out.println("Results: " + returnedChange);
                System.out.println("Expected: " + expectedResult);
            }
            assert(returnedChange.equals(expectedResult));
        }
    }

    @Test
    public void testTransactions() {

        for (int i=0; i < purchasePricesTransaction.length; i++) {
            // expected output equal to ArrayList exactly
            ArrayList<Double> expectedResult = expectedResultsTransactions.get(i);
            ArrayList<Integer> replenished = expectedReplenished.get(i);

            // initialize purchase and payment
            double purchaseAmount = purchasePricesTransaction[i];
            double paymentAmount = paymentAmountsTransaction[i];
            
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
            ArrayList<Double> returnedChange = cashRegister.calculateChange(purchaseAmount, paymentAmount);
            // get replenished bills
            ArrayList<Integer> replenishedBills = cashRegister.getBillCounts();

            // assert we got the right amount of change
            assert(returnedChange.equals(expectedResult));
            // assert we replenished correctly
            assert(replenishedBills.equals(replenished));
        }
    }

    public static void main(String[] args) {
        CashRegisterTest registerTest = new CashRegisterTest();
        registerTest.testPurchaseGreaterThanPayment();
        registerTest.testInsufficientBillAmounts();
        registerTest.testTransactions();
    }
}
