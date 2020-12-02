package com.ar.echoafcavlapplication.Models;

public class DiscountInfo {
    private Integer discountPercent = 0;
    private Integer discountTripCount = 0;
    private Long discountAmount = 0L;
    private Integer tripCount = 0;
    private Integer discountInitDate = 0;
    private Integer discountExpDate = 0;
    private byte srvCode1;
    private byte srvCode2;
    private byte srvCode3;
    private byte srvCode4;
    private byte srvCode5;
    private byte transactionType;

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getDiscountTripCount() {
        return discountTripCount;
    }

    public void setDiscountTripCount(Integer discountTripCount) {
        this.discountTripCount = discountTripCount;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getTripCount() {
        return tripCount;
    }

    public void setTripCount(Integer tripCount) {
        this.tripCount = tripCount;
    }

    public Integer getDiscountInitDate() {
        return discountInitDate;
    }

    public void setDiscountInitDate(Integer discountInitDate) {
        this.discountInitDate = discountInitDate;
    }

    public Integer getDiscountExpDate() {
        return discountExpDate;
    }

    public void setDiscountExpDate(Integer discountExpDate) {
        this.discountExpDate = discountExpDate;
    }

    public Byte getSrvCode1() {
        return srvCode1;
    }

    public void setSrvCode1(Byte srvCode1) {
        this.srvCode1 = srvCode1;
    }

    public Byte getSrvCode2() {
        return srvCode2;
    }

    public void setSrvCode2(Byte srvCode2) {
        this.srvCode2 = srvCode2;
    }

    public Byte getSrvCode3() {
        return srvCode3;
    }

    public void setSrvCode3(Byte srvCode3) {
        this.srvCode3 = srvCode3;
    }

    public Byte getSrvCode4() {
        return srvCode4;
    }

    public void setSrvCode4(Byte srvCode4) {
        this.srvCode4 = srvCode4;
    }

    public Byte getSrvCode5() {
        return srvCode5;
    }

    public void setSrvCode5(Byte srvCode5) {
        this.srvCode5 = srvCode5;
    }

    public Byte getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Byte transactionType) {
        this.transactionType = transactionType;
    }
}
