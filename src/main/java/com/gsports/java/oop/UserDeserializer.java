package com.gsports.java.oop;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDeserializer extends StdDeserializer<User> {
    public UserDeserializer() {
        super(User.class);
    }

    @Override
    public User deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();

        String userID = node.has("userID") ? node.get("userID").asText() : null;
        String username = node.has("username") ? node.get("username").asText() : null;
        String email = node.has("email") ? node.get("email").asText() : null;
        String password = node.has("password") ? node.get("password").asText() : null;

        // Check if the JSON contains the "accessLevel" field to identify an admin
        if (node.has("accessLevel")) {
            String accessLevel = node.get("accessLevel").asText();
            return new Admin(userID, username, email, password, accessLevel);
        } else {
            String address = node.has("address") ? node.get("address").asText() : "";
            String phoneNum = node.has("phoneNum") ? node.get("phoneNum").asText() : "";
            Customer customer = new Customer(true);
            customer.setUserID(userID);
            customer.setUsername(username);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setAddress(address);
            customer.setPhoneNum(phoneNum);
            
            // Deserialize cart if present
            if (node.has("cart")) {
                Cart cart = mapper.treeToValue(node.get("cart"), Cart.class);
                customer.setCart(cart);
            }
            
            // Deserialize wishlist if present
            if (node.has("wishlist")) {
                Wishlist wishlist = mapper.treeToValue(node.get("wishlist"), Wishlist.class);
                customer.setWishlist(wishlist);
            }
            
            // Deserialize order history if present
            if (node.has("orderHistory") && node.get("orderHistory").isArray()) {
                ArrayNode ordersNode = (ArrayNode) node.get("orderHistory");
                List<Order> orders = new ArrayList<>();
                
                for (JsonNode orderNode : ordersNode) {
                    Order order = mapper.treeToValue(orderNode, Order.class);
                    orders.add(order);
                }
                
                customer.setOrderHistory(orders);
            }
            
            return customer;
        }
    }
}

    

