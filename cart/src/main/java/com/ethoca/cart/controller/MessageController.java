package com.ethoca.cart.controller;

import com.ethoca.cart.model.CartProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class MessageController {


    private static final String MY_SESSION_NOTES_CONSTANT = "MY_SESSION_NOTES";
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @GetMapping(value = "/cart")
    public String cart(final Model model, final HttpSession session) {
        //session.setMaxInactiveInterval(60);
        final Map<String, CartProduct> orderProducts = (Map<String, CartProduct>) session.getAttribute(MY_SESSION_NOTES_CONSTANT);
        model.addAttribute("sessionNotes", !CollectionUtils.isEmpty(orderProducts) ? orderProducts : new HashMap<>());
        return "cart";      // Returning the page to be rendered on the browser.
    }


}
