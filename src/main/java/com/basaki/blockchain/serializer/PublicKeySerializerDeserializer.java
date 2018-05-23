package com.basaki.blockchain.serializer;

import com.basaki.blockchain.util.CryptoFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.security.PublicKey;

public class PublicKeySerializerDeserializer {

    public static class PublicKeySerializer extends JsonSerializer<PublicKey> {

        @Override
        public void serialize(PublicKey key, JsonGenerator generator,
                SerializerProvider provider) throws IOException {
            if (key != null) {
                generator.writeObject(
                        CryptoFactory.getInstance().encodeByteToString(
                                key.getEncoded()));
            }
        }
    }

    public static class PublicKeyDeserializer extends JsonDeserializer<PublicKey> {

        @Override
        public PublicKey deserialize(JsonParser parser,
                DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            String encoded = node.asText();
            if (encoded != null) {
                CryptoFactory factory = CryptoFactory.getInstance();
                return factory.getPublicKey(
                        factory.decodeStringToByte(encoded));
            }

            return null;
        }
    }
}
