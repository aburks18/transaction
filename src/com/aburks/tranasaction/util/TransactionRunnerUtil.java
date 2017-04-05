package com.aburks.tranasaction.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.aburks.tranasaction.constants.Endpoints;
import com.aburks.tranasaction.entity.Transaction;

public class TransactionRunnerUtil {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	
	public static final String BALANCE_PATTERN = "\\{\"day\":\\{\"year\":[0-9]{4},\"month\":[0-9]{1,2},\"day\":[0-9]{1,2}\\},\"balance\":[0-9]+\\}";
	
	public static String sendRequest(String method, String payload) {
		StringBuffer response = new StringBuffer();
		URL url;
		HttpsURLConnection conn = null;
		try {
			url = new URL(Endpoints.BASE_URL+method);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept-Language","application/json");
			conn.setRequestProperty("Content-Type","application/json");
			
			// Send post request
			conn.setDoOutput(true);
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(payload);
			dos.flush();
			dos.close();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line=br.readLine()) != null) {
				response.append(line);
			}
			br.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn!=null) {
			 conn.disconnect();
			}
		}
		
		return response.toString();
	}
	
	public static List<Transaction> convertTransactions(String transactionsResponse) {
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		String[] responseElements = transactionsResponse.split(",",2);
		String transactionStringList = responseElements[1].substring(17, responseElements[1].length()-3);
		String[] transactionAsStrings = transactionStringList.split("}");
		for (String xaction : transactionAsStrings) {
			if (xaction.startsWith(",{")) {
				xaction = xaction.substring(2);
			}
			
			Transaction transaction = new Transaction();
			
			for (String xactionElement : xaction.split(",")) {
				String[] keyVal = xactionElement.split(":",2);
				String key = keyVal[0].replaceAll("\"", "");
				String value = keyVal[1].replaceAll("\"", "");
				if (key.contains("amount")) {
					transaction.setAmount(Integer.valueOf(value).intValue()/10000);
				} else if (key.contains("is-pending")) {
					transaction.setPending(Boolean.valueOf(value).booleanValue());
				} else if (key.contains("account-id")) {
					transaction.setAccountId(value);
				} else if (key.contains("clear-date")) {
					Calendar cal = new GregorianCalendar();
					cal.setTimeInMillis(Long.valueOf(value).longValue());
					transaction.setClearDate(cal);
				} else if (key.contains("transaction-id")) {
					transaction.setTransactionId(value);
				} else if (key.equals("raw-merchant")) {
					transaction.setRawMerchant(value);
				} else if (key.contains("categorization")) {
					transaction.setCategorization(value);
				} else if (key.equals("merchant")) {
					transaction.setMerchant(value);
				} else if (key.contains("transaction-time")) {
					try {
						DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
						Calendar cal = new GregorianCalendar();
						cal.setTime(sdf.parse(value));
						transaction.setTransactionTime(cal);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			transactions.add(transaction);
		}
		
		return transactions;
	}

	public static Map<String, Integer> convertHistoricalBalances(String balancesResponse) {
		Map<String,Integer> balances = new HashMap<String,Integer>();
		String[] responseElements = balancesResponse.split(",",2);
		String balancesStringList = responseElements[1].substring(8, responseElements[1].length()-2);
		Pattern pattern = Pattern.compile(BALANCE_PATTERN);
		Matcher matcher = pattern.matcher(balancesStringList);
		while (matcher.find()) {
		    String match = matcher.group();
		    String year = match.substring(match.indexOf("year")+6,match.indexOf("year")+10);
		    String month = match.substring(match.indexOf("month")+7).split(",",2)[0];
		    String[] days = match.split("day",2);
		    String day = days[1].substring(days[1].indexOf("day")+5).split("}",2)[0];
		    String balance = match.substring(match.indexOf("balance")+9).split("}",2)[0];
		    
		    StringBuilder key = new StringBuilder(year);
		    key.append("-").append(month).append("-").append(day);
		    
		    Integer.valueOf(balance).intValue();
		    Integer value = Integer.valueOf(Integer.valueOf(balance).intValue()/10000);
		    balances.put(key.toString(), value);
		}
		return balances;
	}
}
