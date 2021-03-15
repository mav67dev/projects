package com.ethoca.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class CartProduct implements Serializable {

    private String productName;

    private int quantity;

    private BigDecimal cost;

    private BigDecimal totalCost;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartProduct that = (CartProduct) o;
        return quantity == that.quantity && Objects.equals(productName, that.productName) && Objects.equals(cost, that.cost) && Objects.equals(totalCost, that.totalCost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, quantity, cost, totalCost);
    }

    @Override
    public String toString() {
        return "CartProduct{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", cost=" + cost +
                ", totalCost=" + totalCost +
                '}';
    }
}
