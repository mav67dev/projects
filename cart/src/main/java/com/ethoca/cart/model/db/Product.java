package com.ethoca.cart.model.db;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
//@Table(name="PRODUCT", schema="TESTDB")
@Table(name="PRODUCT")
public class Product implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private int productId;

    @Column(name = "product_name", unique = true)
    private String productName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "description")
    private String description;

    public Product(String productName, BigDecimal price, int quantity, String description) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }

    public Product(){

    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", description='" + description + '\'' +
                '}';
    }

}
