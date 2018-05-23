package com.basaki.blockchain.core;

import com.basaki.blockchain.util.CryptoFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.junit.Before;
import org.junit.Test;

public class TransactionTest {

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Before
    public void startUp() {
        KeyPair pair = CryptoFactory.getInstance().generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }

    @Test
    public void testCreation() {

    }
}
