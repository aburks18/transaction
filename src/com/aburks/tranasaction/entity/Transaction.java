package com.aburks.tranasaction.entity;

import java.util.Calendar;
import java.util.Date;

public class Transaction {
	
	String transactionId;
	String accountId;
	String rawMerchant;
	String merchant;
	boolean isPending;
	Calendar transactionTime;
	int amount;
	String previousTransactionId;
	String categorization;
	Calendar clearDate;
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getRawMerchant() {
		return rawMerchant;
	}
	public void setRawMerchant(String rawMerchant) {
		this.rawMerchant = rawMerchant;
	}
	public String getMerchant() {
		return merchant;
	}
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public boolean isPending() {
		return isPending;
	}
	public void setPending(boolean isPending) {
		this.isPending = isPending;
	}
	public Calendar getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Calendar transactionTime) {
		this.transactionTime = transactionTime;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getPreviousTransactionId() {
		return previousTransactionId;
	}
	public void setPreviousTransactionId(String previousTransactionId) {
		this.previousTransactionId = previousTransactionId;
	}
	public String getCategorization() {
		return categorization;
	}
	public void setCategorization(String categorization) {
		this.categorization = categorization;
	}
	public Calendar getClearDate() {
		return clearDate;
	}
	public void setClearDate(Calendar clearDate) {
		this.clearDate = clearDate;
	} 
}
