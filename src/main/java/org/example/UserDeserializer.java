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

        // Check if the JSON contains the "accessLevel" field to identify an admin
        if (node.has("accessLevel")) {
            return new Admin (
                node.get("userID").asText(),
                node.get("username").asText(),
                node.get("email").asText(),
                node.get("password").asText(),
                node.get("accessLevel").asText()
            );
        } else {
            return new Customer (
                node.get("userID").asText(),
                node.get("username").asText(),
                node.get("email").asText(),
                node.get("password").asText(),
                node.get("address").asText(),
                node.get("phoneNum").asText()
            );
        }
    }
}
