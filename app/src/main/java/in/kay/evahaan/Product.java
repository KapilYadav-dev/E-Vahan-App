
package in.kay.evahaan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productPrice")
    @Expose
    private String productPrice;
    @SerializedName("productCurrency")
    @Expose
    private String productCurrency;
    @SerializedName("priceChange")
    @Expose
    private String priceChange;
    @SerializedName("priceChangeSign")
    @Expose
    private Object priceChangeSign;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductCurrency() {
        return productCurrency;
    }

    public void setProductCurrency(String productCurrency) {
        this.productCurrency = productCurrency;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }

    public Object getPriceChangeSign() {
        return priceChangeSign;
    }

    public void setPriceChangeSign(Object priceChangeSign) {
        this.priceChangeSign = priceChangeSign;
    }

}
