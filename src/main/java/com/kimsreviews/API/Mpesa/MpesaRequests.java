package com.kimsreviews.API.Mpesa;

import com.google.gson.Gson;

public class MpesaRequests {

    private int BusinessShortCode;
    private String Password;
    private String Timestamp;
    private String TransactionType;
    private int Amount;
    private long PartyA;
    private int PartyB;
    private long PhoneNumber;
    private String CallBackURL;

    public int getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(int businessShortCode) {
        BusinessShortCode = businessShortCode;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public long getPartyA() {
        return PartyA;
    }

    public void setPartyA(long partyA) {
        PartyA = partyA;
    }

    public int getPartyB() {
        return PartyB;
    }

    public void setPartyB(int partyB) {
        PartyB = partyB;
    }

    public String getCallBackURL() {
        return CallBackURL;
    }

    public void setCallBackURL(String callBackURL) {
        CallBackURL = callBackURL;
    }

    public long getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getAccountReference() {
        return AccountReference;
    }

    public void setAccountReference(String accountReference) {
        AccountReference = accountReference;
    }

    public String getTransactionDesc() {
        return TransactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        TransactionDesc = transactionDesc;
    }

    private String AccountReference;
    private String TransactionDesc;

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
