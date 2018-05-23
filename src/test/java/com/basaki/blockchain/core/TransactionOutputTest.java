package com.basaki.blockchain.core;

import com.basaki.blockchain.util.CryptoFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TransactionOutputTest {

    private PublicKey publicKey;

    @Before
    public void startUp() {
        KeyPair pair = CryptoFactory.getInstance().generateKeyPair();
        publicKey = pair.getPublic();
    }

    @Test
    public void testCreation() {
        TransactionOutput txnOutput =
                new TransactionOutput("1234", publicKey, 50.65);

        assertNotNull(txnOutput);
        assertNotNull(txnOutput.getId());
        assertEquals("1234", txnOutput.getParentId());
        assertEquals(publicKey, txnOutput.getPublicKey());
        assertEquals(50.65, txnOutput.getValue(), 0);
        assertTrue(txnOutput.isMine(publicKey));
    }

    @Test
    public void testSerialization() throws IOException {
        TransactionOutput txn =
                new TransactionOutput("12345", publicKey, 30.45);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(txn);
        TransactionOutput txnCopy =
                objectMapper.readValue(json, TransactionOutput.class);
        assertNotNull(txnCopy);
        assertEquals(txn, txnCopy);
    }
}
