package com.basaki.blockchain.core;

import com.basaki.blockchain.serializer.PublicKeySerializerDeserializer;
import com.basaki.blockchain.util.CryptoFactory;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.security.MessageDigest;
import java.security.PublicKey;
import lombok.Data;

@Data
public class TransactionOutput {

    private String id;

    private String parentId;

    @JsonSerialize(using = PublicKeySerializerDeserializer.PublicKeySerializer.class)
    @JsonDeserialize(using = PublicKeySerializerDeserializer.PublicKeyDeserializer.class)
    private PublicKey publicKey;

    private double value;

    public TransactionOutput(String parentId, PublicKey publicKey,
            double value) {
        this.parentId = parentId;
        this.publicKey = publicKey;
        this.value = value;
        this.id = calculateHash();
    }

    // for deserialization
    private TransactionOutput() {
    }

    public boolean isMine(PublicKey mine) {
        return publicKey.equals(mine);
    }

    private String calculateHash() {
        CryptoFactory factory = CryptoFactory.getInstance();

        String input = parentId + factory.encodeByteToString(
                publicKey.getEncoded()) + value;

        MessageDigest
                msgDigest =
                factory.getMessageDigest(CryptoFactory.ALGO_HASH_SHA_256);

        return factory.toHexString(msgDigest.digest(input.getBytes()));
    }
}
