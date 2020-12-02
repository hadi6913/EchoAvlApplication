package com.ar.echoafcavlapplication.Models;

import com.ar.echoafcavlapplication.Enums.QomCardType;

public class Card {
    private byte contractType;
    private byte exactCT;
    private Long uId;
    private byte extraUId;
    private Integer tagType;
    private Long depositValue;
    private byte validation;
    private Integer city;
    private QomCardType qomCardType;
    private Integer cardVersion;
    private Long transactionSectorPointer;
    private Long decrementalCounter;
    private byte lastTransactionLocation;
    private Boolean isBlocked;
    private TransactionInfo newTransactionInfo;
    private TransactionInfo lastTransactionInfo;

    public Card(byte[] statBlockData) {
        if (statBlockData != null){
            this.newTransactionInfo = new TransactionInfo();
            this.lastTransactionInfo = new TransactionInfo();
        }
    }

    public byte getContractType() {
        return contractType;
    }

    public void setContractType(byte contractType) {
        this.contractType = contractType;
    }

    public byte getExactCT() {
        return exactCT;
    }

    public void setExactCT(byte exactCT) {
        this.exactCT = exactCT;
    }

    public Long getuId() {
        return uId;
    }

    public void setuId(Long uId) {
        this.uId = uId;
    }

    public byte getExtraUId() {
        return extraUId;
    }

    public void setExtraUId(byte extraUId) {
        this.extraUId = extraUId;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

    public Long getDepositValue() {
        return depositValue;
    }

    public void setDepositValue(Long depositValue) {
        this.depositValue = depositValue;
    }

    public byte getValidation() {
        return validation;
    }

    public void setValidation(byte validation) {
        this.validation = validation;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Long getTransactionSectorPointer() {
        return transactionSectorPointer;
    }

    public void setTransactionSectorPointer(Long transactionSectorPointer) {
        this.transactionSectorPointer = transactionSectorPointer;
    }

    public Long getDecrementalCounter() {
        return decrementalCounter;
    }

    public void setDecrementalCounter(Long decrementalCounter) {
        this.decrementalCounter = decrementalCounter;
    }

    public byte getLastTransactionLocation() {
        return lastTransactionLocation;
    }

    public void setLastTransactionLocation(byte lastTransactionLocation) {
        this.lastTransactionLocation = lastTransactionLocation;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public TransactionInfo getNewTransactionInfo() {
        return newTransactionInfo;
    }

    public void setNewTransactionInfo(TransactionInfo newTransactionInfo) {
        this.newTransactionInfo = newTransactionInfo;
    }

    public TransactionInfo getLastTransactionInfo() {
        return lastTransactionInfo;
    }

    public void setLastTransactionInfo(TransactionInfo lastTransactionInfo) {
        this.lastTransactionInfo = lastTransactionInfo;
    }

    public Integer getCardVersion() {
        return cardVersion;
    }

    public void setCardVersion(Integer cardVersion) {
        this.cardVersion = cardVersion;
    }

    public QomCardType getQomCardType() {
        return qomCardType;
    }

    public void setQomCardType(QomCardType qomCardType) {
        this.qomCardType = qomCardType;
    }
}
