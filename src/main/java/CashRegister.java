/*
ASSUMPTIONS:
    - When replinishing the register, I assume that the customer paid with the minimal amount of change
    necessary to reach the paymentAmount

NOTES:
    - Used BigDecimal class as I was having precision problems when operating on doubles
*/
package cashregister;

import java.util.Arrays;
import java.util.ArrayList;
import java.math.BigDecimal;

public class CashRegister {
    private ArrayList<Double> bills;
    private ArrayList<Integer> billCounts;
    private int totalBills;

    public CashRegister(ArrayList<Double> bills, ArrayList<Integer> billCounts, int totalBills) {
        this.bills = bills;
        this.billCounts = billCounts;
        this.totalBills = totalBills;
    }

    public ArrayList<Double> getBills() {
        return this.bills;
    }

    public ArrayList<Integer> getBillCounts() {
        return this.billCounts;
    }

    public int getTotalBills() {
        return this.totalBills;
    }

    public void setBills(ArrayList<Double> newBills) {
        this.bills = newBills;
    }
    
    public void setBillCounts(ArrayList<Integer> newbillCounts) {
        this.billCounts = newbillCounts;
    }

    public void setTotalBills(int newTotalBills) {
        this.totalBills = newTotalBills;
    }

    public ArrayList<Double> calculateChange (double purchasePrice, double paymentAmount) {

        double totalToReplenish  = paymentAmount;               
        double totalChange       = BigDecimal.valueOf(paymentAmount).subtract(BigDecimal.valueOf(purchasePrice)).doubleValue();
        ArrayList<Double> change = new ArrayList<>();

        // check if payment < purchase price
        if (paymentAmount < purchasePrice) {return change;}
    
        // replenish the register as soon as we're handed a payment amount
        while (totalToReplenish > 0) {
            double largestBill = 0;
            // find the optimal bill for replenishing
            for (int i=0; i < this.bills.size(); i++) {
                double bill = this.bills.get(i);
                if (bill <= totalToReplenish) {
                    largestBill = bill;
                }
            }
            int indexOfBill = this.bills.indexOf(largestBill);
            this.billCounts.set(indexOfBill, this.billCounts.get(indexOfBill) + 1);
            totalToReplenish = BigDecimal.valueOf(totalToReplenish).subtract(BigDecimal.valueOf(largestBill)).doubleValue();
        }

        // use a greedy approach to just find the largest bill first
        // and then find next available largest bill, etc
        while (totalChange > 0 && this.totalBills > 0) {
            // initialize the largest bill
            double largestBill = 0;

            // find the largest bill just larger than the change amount
            for (int i=0; i < this.bills.size(); i++) {
                double bill = this.bills.get(i);
                int billCount = this.billCounts.get(i);
                // if the bill is still less than the current change
                // and we have more than 1 of them, use this bill for change
                if (bill <= totalChange && billCount > 0) {
                    largestBill = bill;
                }
            }

            // if no bills exist exit
            if (largestBill == 0) { 
                totalChange = 0; 
            } else {
                // update the bill counts, the bill values, and total change left
                this.totalBills--;
                int indexBill = this.bills.indexOf(largestBill);
                this.billCounts.set(this.bills.indexOf(largestBill), this.billCounts.get(indexBill) - 1);
                totalChange = BigDecimal.valueOf(totalChange).subtract(BigDecimal.valueOf(largestBill)).doubleValue();

                change.add(largestBill);
            }
        }

        // check that the change amount constructed is equal to the change amount expected
        double sumChange = 0;
        for (Double bill : change) {
            sumChange = BigDecimal.valueOf(sumChange).add(BigDecimal.valueOf(bill)).doubleValue();
        }
        double difference = (BigDecimal.valueOf(paymentAmount).subtract(BigDecimal.valueOf(purchasePrice))).subtract(BigDecimal.valueOf(sumChange)).doubleValue();
        if (difference != 0) {
            System.out.println("Not enough change to return correct amount...we owe you " + difference);
        }

        return change;
    }

    public void simulateTransaction(double price, double payment, int transactionNum) {
        System.out.println("Transaction " + transactionNum + ": ");
        System.out.println("Purchase is: " + price);
        System.out.println("Payment is: " + payment);
        ArrayList change = this.calculateChange(price, payment);
        System.out.println("Change: " + change + "\n- - - -");
    }

    public static void main(String[] args) {
        // SMALL SIMULATION OF TRANSACTIONS
        ArrayList<Double> bills = new ArrayList<>(Arrays.asList(0.01, 0.05, 0.1, 0.25, 0.5, 1.00, 5.00, 10.00, 20.00));
        ArrayList<Integer> billCounts = new ArrayList<>(Arrays.asList(5, 5, 0, 5, 5, 5, 5, 5, 1));
        // initiate total number of bills
        int totalBills = 0;
        for (int i=0; i < billCounts.size(); i++) {
            totalBills += billCounts.get(i);
        }

        // create a new CashRegister instance
        CashRegister register = new CashRegister(bills, billCounts, totalBills);

        // simulate transactions
        register.simulateTransaction(0.89, 1.00, 1);
        register.simulateTransaction(4.99, 50, 2);
        register.simulateTransaction(1.01, 2.00, 3);
        register.simulateTransaction(4.99, 3.00, 4);
        register.simulateTransaction(0.34, 100, 5);

    }
}