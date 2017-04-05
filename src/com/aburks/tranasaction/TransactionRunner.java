package com.aburks.tranasaction;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.aburks.tranasaction.constants.Endpoints;
import com.aburks.tranasaction.constants.Requests;
import com.aburks.tranasaction.entity.Transaction;
import com.aburks.tranasaction.util.TransactionRunnerUtil;

public class TransactionRunner {

	public static final String UID = "1110590645";
	public static final String AUTH_TOKEN = "4415E2CACEDCA3A5941F35A092FD668E";
	public static final String API_TOKEN = "AppTokenForInterview";
	
	public static void main(String[] args) {
		// Load users transactions
		String t_response = TransactionRunnerUtil.sendRequest(Endpoints.METHOD_GET_ALL_TRANS, MessageFormat.format(Requests.REQ_GET_TRANS,UID,AUTH_TOKEN,API_TOKEN));
		List<Transaction> transactions = null;
		if (t_response.contains("no-error")) {
			transactions = TransactionRunnerUtil.convertTransactions(t_response);
		}
		
		// Load historical balances
		String h_response = TransactionRunnerUtil.sendRequest(Endpoints.METHOD_BALANCES, MessageFormat.format(Requests.REQ_BAL,UID,AUTH_TOKEN,API_TOKEN));
		//System.out.println(h_response);
		Map<String,Integer> balances = null;
		if (h_response.contains("no-error")) {
			balances = TransactionRunnerUtil.convertHistoricalBalances(h_response);
		}
		
		// Load account balances
		String a_response = TransactionRunnerUtil.sendRequest(Endpoints.METHOD_ACCOUNTS, MessageFormat.format(Requests.REQ_BAL,UID,AUTH_TOKEN,API_TOKEN));
		
		Integer currentBalance = Integer.valueOf(a_response.substring(a_response.indexOf("balance")+9).split(",",2)[0]);
		//System.out.println(currentBalance);
		
		if (transactions!=null && !transactions.isEmpty() && balances!=null && !balances.isEmpty()) {
			List<String> output = new ArrayList<String>();
			ListIterator<Transaction> itr = transactions.listIterator(transactions.size());
			String output_format = "\"{0}\": '{'\"spent\": \"${1}\", \"income\": \"${2}\"'}'";
			int total_spent = 0;
			int total_earned = 0;
			int total_months = 0;
			int monthly_spent = 0;
			int monthly_earned = 0;
			int prev_month = 0;
			while (itr.hasPrevious()) {
				Transaction t = (Transaction) itr.previous();
				if (prev_month==0) {
					prev_month = t.getTransactionTime().get(Calendar.MONTH)+1;
					if (t.getAmount()<0) {
						monthly_spent = t.getAmount();
					} else {
						monthly_earned = t.getAmount();
					}
					continue;
				}
				
				int current_month = t.getTransactionTime().get(Calendar.MONTH)+1;
				if (prev_month!=current_month) {
					int year = t.getTransactionTime().get(Calendar.YEAR);
					if (prev_month==1) {
						year = year + 1;
					}
					String month_date = year+"-"+prev_month;
					String monthly_output = MessageFormat.format(output_format,month_date,-1*monthly_spent,monthly_earned);
					output.add(monthly_output);
					total_spent = total_spent + monthly_spent;
					total_earned = total_earned + monthly_earned;
					if (t.getAmount()<0) {
						monthly_spent = t.getAmount();
					} else {
						monthly_earned = t.getAmount();
					}
					total_months++;
					prev_month = current_month;
				} else {
					if (t.getAmount()<0) {
						monthly_spent = monthly_spent + t.getAmount();
					} else {
						monthly_earned = monthly_earned + t.getAmount();
					}
				}
				
				if (itr.hasPrevious()==false && prev_month==current_month) {
					int year = t.getTransactionTime().get(Calendar.YEAR);
					if (prev_month==1) {
						year = year + 1;
					}
					String month_date = year+"-"+prev_month;
					String monthly_output = MessageFormat.format(output_format,month_date,-1*monthly_spent,monthly_earned);
					output.add(monthly_output);
					if (t.getAmount()<0) {
						total_spent = total_spent + monthly_spent;
						monthly_spent = t.getAmount();
					} else {
						total_earned = total_earned + monthly_earned;
						monthly_earned = t.getAmount();
					}
					total_months++;
				}
			}
			String avg_output = MessageFormat.format(output_format,"average",-1*(total_spent/total_months),total_earned/total_months);
			output.add(0,avg_output);
			ListIterator<String> itr2 = output.listIterator(output.size());
			System.out.print("{");
			while (itr2.hasPrevious()) {
				String element = (String) itr2.previous();
				if (!itr2.hasPrevious()) {
					System.out.println(element+"}");
				} else {
					System.out.println(element);
				}
			}
		}
	}
}
