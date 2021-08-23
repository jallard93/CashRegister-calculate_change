package cashregister;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

import software.amazon.awssdk.services.lambda.model.GetAccountSettingsRequest;
import software.amazon.awssdk.services.lambda.model.GetAccountSettingsResponse;
import software.amazon.awssdk.services.lambda.model.ServiceException;
import software.amazon.awssdk.services.lambda.LambdaAsyncClient;
import software.amazon.awssdk.services.lambda.model.AccountUsage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.math.BigDecimal;
import java.lang.*;


import cashregister.CashRegister;

public class Handler implements RequestHandler<Map<String,String>, String>{

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ArrayList<Double> bills = new ArrayList<>();
    private ArrayList<Integer> billCounts = new ArrayList<>();
    private int totalBills = 0;
    private CashRegister register = new CashRegister(bills, billCounts, totalBills);
  

    @Override
    public String handleRequest(Map<String,String> event, Context context)
    {
      String response = new String();
      // call Lambda API
      logger.info("Getting account settings");

      // initiate purchase price
      double purchasePrice = BigDecimal.valueOf(Double.parseDouble(event.get("purchasePrice"))).doubleValue();
      double paymentAmount = BigDecimal.valueOf(Double.parseDouble(event.get("paymentAmount"))).doubleValue();
      ArrayList<Integer> billCounts = this.parseBillCounts(event.get("billCounts"));
      int totalBills = Integer.parseInt(event.get("totalBills"));

      // update register values
      register.setBills(new ArrayList<>(Arrays.asList(0.01, 0.05, 0.1, 0.25, 0.5, 1.00, 5.00, 10.00, 20.00)));
      register.setBillCounts(billCounts);
      register.setTotalBills(totalBills);

      // calculate and get the change response
      ArrayList<Double> change = register.calculateChange(purchasePrice, paymentAmount);
      response = "{\"change\":" + change.toString() + ",\"bills\":"+ register.getBillCounts().toString() + "}"; 

      // process Lambda API response
      try {
        response = gson.toJson(response);
      } catch(Exception e) {
        e.getStackTrace();
      }
      return response;
    }

    public ArrayList<Integer> parseBillCounts(String billCounts) {
      // remove array character
      String[] items = billCounts.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
      ArrayList<Integer> results = new ArrayList<>();

      // parse each integer from the json
      for (int i = 0; i < items.length; i++) {
        results.add(Integer.parseInt(items[i]));
      }
      return results;
    }
}