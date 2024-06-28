package com.jean.productauthentifier.model;

public class ProductAuthentifier {

    private int productId;
    private String productReference;
    private String uid_hash;
    private String requestedBy;
    private String productName;
    private String manufacturer;
    private String madeIn;
    private int yearOfRelease;
    private String result;

    public ProductAuthentifier(int productId, String productReference, String uid_hash, String requestedBy) {
        this.productId = productId;
        this.productReference = productReference;
        this.uid_hash = uid_hash;
        this.requestedBy = requestedBy;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductReference() {
        return productReference;
    }

    public void setProductReference(String productReference) {
        this.productReference = productReference;
    }

    public String getUid_hash() {
        return uid_hash;
    }

    public void setUid_hash(String uid_hash) {
        this.uid_hash = uid_hash;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMadeIn() {
        return madeIn;
    }

    public void setMadeIn(String madeIn) {
        this.madeIn = madeIn;
    }

    public int getYearOfRelease() {
        return yearOfRelease;
    }

    public void setYearOfRelease(int yearOfRelease) {
        this.yearOfRelease = yearOfRelease;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ProductAuthentifier{" +
                "productId=" + productId +
                ", productReference='" + productReference + '\'' +
                ", uid_hash='" + uid_hash + '\'' +
                ", requestedBy='" + requestedBy + '\'' +
                ", productName='" + productName + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", madeIn='" + madeIn + '\'' +
                ", yearOfRelease=" + yearOfRelease +
                ", result='" + result + '\'' +
                '}';
    }
}
