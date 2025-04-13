package org.example;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {
    public UserDeserializer() {
        super(User.class);
    }

    @Override
    public User deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);

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
            return new Customer(userID, username, email, password, address, phoneNum);
        }
    }
}
