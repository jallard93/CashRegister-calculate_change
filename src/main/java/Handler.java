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
    private static final LambdaAsyncClient lambdaClient = LambdaAsyncClient.create();
    private ArrayList<Double> bills = new ArrayList<>();
    private ArrayList<Integer> billCounts = new ArrayList<>();
    private int totalBills = 0;
    private CashRegister register = new CashRegister(bills, billCounts, totalBills);
    
    public Handler(){
      CompletableFuture<GetAccountSettingsResponse> accountSettings = lambdaClient.getAccountSettings(GetAccountSettingsRequest.builder().build());
      try {
        GetAccountSettingsResponse settings = accountSettings.get();
      } catch(Exception e) {
        e.getStackTrace();
      }
    }

    @Override
    public String handleRequest(Map<String,String> event, Context context)
    {
      String response = new String();
      // call Lambda API
      logger.info("Getting account settings");
      CompletableFuture<GetAccountSettingsResponse> accountSettings = 
          lambdaClient.getAccountSettings(GetAccountSettingsRequest.builder().build());
      
      System.out.println(event);

      double purchasePrice = BigDecimal.valueOf(Double.parseDouble(event.get("purchasePrice"))).doubleValue();
      double paymentAmount = BigDecimal.valueOf(Double.parseDouble(event.get("paymentAmount"))).doubleValue();
      ArrayList<Integer> billCounts = this.parseBillCounts(event.get("billCounts"));
      int totalBills = Integer.parseInt(event.get("totalBills"));

      // update register values
      register.setBills(new ArrayList<>(Arrays.asList(0.01, 0.05, 0.1, 0.25, 0.5, 1.00, 5.00, 10.00, 20.00)));
      register.setBillCounts(billCounts);
      register.setTotalBills(totalBills);

      // get bill counts
      System.out.println("Initial Counts: " + register.getBillCounts());

      // calculate and get the change response
      ArrayList<Double> change = register.createChange(purchasePrice, paymentAmount);

      System.out.println("After Counts: " + register.getBillCounts());

      response = "{\"change\":" + change.toString() + "}"; 

      // process Lambda API response
      try {
        GetAccountSettingsResponse settings = accountSettings.get();
        response = gson.toJson(response);
        logger.info("Account usage: {}", response);
      } catch(Exception e) {
        e.getStackTrace();
      }
      return response;
    }

    public ArrayList<Integer> parseBillCounts(String billCounts) {
      String[] items = billCounts.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

      ArrayList<Integer> results = new ArrayList<>();

      for (int i = 0; i < items.length; i++) {
        results.add(Integer.parseInt(items[i]));
      }
      return results;
    }
}