package com.ethoca.cart.controller;

import com.ethoca.cart.exception.AvailabilityException;
import com.ethoca.cart.exception.EmptyCartException;
import com.ethoca.cart.exception.ProductNotFoundException;
import com.ethoca.cart.model.CartProduct;
import com.ethoca.cart.model.OrderProduct;
import com.ethoca.cart.model.db.OrderConfirmation;
import com.ethoca.cart.model.db.Product;
import com.ethoca.cart.service.ProdService;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

@RestController
@Validated
@CrossOrigin
public class CartController {

    private static final String MY_SESSION_NOTES_CONSTANT = "MY_SESSION_NOTES";
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    ProdService prodService;

    //List all the products in the website containing the keyword
    @GetMapping("/list/{name}")
    public List<Product> getProd(@PathVariable String name) {
        System.out.println("result = " + prodService.getProdList(name));
        return prodService.getProdList(name);
    }

    //Find the exact product in the market
    @GetMapping("/find/{name}")
    public Product findProd(@PathVariable String name) {
        System.out.println("result = " + prodService.getProd(name));
        return prodService.getProd(name);
    }

    //Retrieve all the products in the market
    @GetMapping("/retrieve")
    public List<Product> getProd() {
        return prodService.getAll();
    }

    //Place the order from your cart
    @PostMapping("/order")
    public ResponseEntity<OrderConfirmation> placeOrder(final HttpServletRequest request) throws Exception {
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);

        //check if the cart is empty
        if (CollectionUtils.isEmpty(initialCart)) {
            throw new EmptyCartException("Cart is empty");
        }

        //check if items are still available during confirmation of order
        List<OrderProduct> orderProducts  = new ArrayList<>();
        for(String key : initialCart.keySet())
            orderProducts.add(CartResponseBuilder.parse(initialCart.get(key)));
        List<String> chckQuantity = prodService.checkQuantityList(orderProducts);
        if(chckQuantity != null){
            throw new AvailabilityException(chckQuantity);
        }

        //COnfirm order and update tables
        OrderConfirmation orderConfirmation = prodService.confirmOrder(initialCart);
        request.getSession().invalidate();
        return ResponseEntity.status(HttpStatus.OK).body(orderConfirmation);
    }


    //Update your cart
    @PostMapping("/updatecart")
    public ResponseEntity<Map<String, CartProduct>> updateCart(@RequestBody @Valid List<OrderProduct> orderProducts, final HttpServletRequest request) {
        //Retrieve initial cart data stored in the session
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);
        if (CollectionUtils.isEmpty(initialCart)) {
            throw new EmptyCartException("Cart is empty");
        }

        //Check if the cart can be updated and update them
        List<String> chckQuantity = prodService.checkQuantityList(orderProducts);
        if (chckQuantity == null) {
            CartResponseBuilder.updateCart(initialCart, orderProducts);
            request.getSession().setAttribute(MY_SESSION_NOTES_CONSTANT, initialCart);
            return ResponseEntity.status(HttpStatus.OK).body(initialCart);
        } else {
            throw new AvailabilityException(chckQuantity);
        }
    }

    //Add an item to your cart
    @PostMapping(value = "/addcart")
    public ResponseEntity<String> addCart(@RequestBody @Valid OrderProduct orderProduct, final HttpServletRequest request) throws Exception {

        //Get the initial cart
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);

        int quantity = orderProduct.getQuantity();

        //Check if cart is empty and instantiate it, if product is already there in the cart, update them
        if (CollectionUtils.isEmpty(initialCart)) {
            log.info("No Items added yet");
            initialCart = new HashMap<>();
        } else {
            if (initialCart.containsKey(orderProduct.getProductName()))
                quantity = quantity + initialCart.get(orderProduct.getProductName()).getQuantity();
        }

        //check if the store has the available quantity and update the cart
        Product product = prodService.getProd(orderProduct.getProductName());
        if (product.getQuantity() >= quantity) {
            CartProduct cartProduct = CartResponseBuilder.buildCartProduct(product, quantity);
            initialCart.put(orderProduct.getProductName(), cartProduct);
            request.getSession().setAttribute(MY_SESSION_NOTES_CONSTANT, initialCart);
            return ResponseEntity.status(HttpStatus.OK).body("Product " + orderProduct.getProductName() + " added to the cart");
        } else {
            throw new AvailabilityException(orderProduct.getProductName());
        }

    }

    //View your current cart
    @GetMapping(value = "/viewcart")
    public List<CartProduct> viewCart(final HttpServletRequest request) {
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);
        if (CollectionUtils.isEmpty(initialCart))
            throw new EmptyCartException("Cart is empty");
        List<CartProduct> result = new ArrayList();

        for (String key : initialCart.keySet()) {
            result.add(initialCart.get(key));
        }
        return result;
    }


    @DeleteMapping(value = "/deletecart")
    public ResponseEntity<String> deleteCart(final HttpServletRequest request) {
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);

        //check if the cart is empty
        if (CollectionUtils.isEmpty(initialCart)) {
            throw new EmptyCartException("Cart is empty");
        }
        request.getSession().invalidate();
        return ResponseEntity.status(HttpStatus.OK).body("Cart removed");
    }

    @DeleteMapping(value = "/deleteproduct")
    public ResponseEntity<String> deleteProduct(@RequestBody String productName, final HttpServletRequest request) {
        Map<String, CartProduct> initialCart = (Map<String, CartProduct>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);

        //check if the cart is empty
        if (CollectionUtils.isEmpty(initialCart))
            throw new EmptyCartException("Cart is empty");
        if (!initialCart.containsKey(productName)) {
            throw new ProductNotFoundException(productName);
        }
        initialCart.remove(productName);
        request.getSession().setAttribute(MY_SESSION_NOTES_CONSTANT, initialCart);
        return ResponseEntity.status(HttpStatus.OK).body("Product " + productName + " removed");
    }


}
