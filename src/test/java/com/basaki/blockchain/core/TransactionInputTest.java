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

public class TransactionInputTest {

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

        TransactionInput txnInput = new TransactionInput(txnOutput);
        assertNotNull(txnInput);

        assertNotNull(txnInput.getUtxo());
        assertEquals(txnOutput.getId(), txnInput.getId());
        assertEquals(txnOutput.getParentId(), txnInput.getUtxo().getParentId());
        assertEquals(txnOutput.getPublicKey(), txnInput.getUtxo().getPublicKey());
        assertEquals(txnOutput.getValue(), txnInput.getUtxo().getValue(), 0);
        assertTrue(txnInput.getUtxo().isMine(publicKey));
    }

    @Test
    public void testSerialization() throws IOException {
        TransactionOutput txnOutput =
                new TransactionOutput("12345", publicKey, 30.45);
        TransactionInput txnInput = new TransactionInput(txnOutput);
        assertNotNull(txnInput);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(txnInput);
        TransactionInput txnInputCopy =
                objectMapper.readValue(json, TransactionInput.class);
        assertNotNull(txnInputCopy);
        assertEquals(txnInput, txnInputCopy);
    }
}
