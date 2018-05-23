package com.basaki.blockchain.util;

import com.basaki.blockchain.exception.InvalidCryptoException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CryptoFactoryTest {

    private CryptoFactory factory;

    @Before
    public void startUp() {
        factory = CryptoFactory.getInstance();
    }

    @Test
    public void testGetMessageDigest() {
        MessageDigest msgDigest =
                factory.getMessageDigest(CryptoFactory.ALGO_HASH_SHA_256);
        byte[] digestOne = msgDigest.digest("indra".getBytes());
        assertNotNull(digestOne);

        byte[] digestTwo = msgDigest.digest("indra".getBytes());
        assertNotNull(digestTwo);

        assertArrayEquals(digestOne, digestTwo);
    }

    @Test(expected = InvalidCryptoException.class)
    public void testInvalidGetMessageDigest() {
        factory.getMessageDigest("Invalid");
    }

    @Test
    public void testToHexString() {
        String hash = factory.toHexString("indra".getBytes());
        assertNotNull(hash);
    }

    @Test
    public void testGetEncodedString() {
        assertNotNull(factory.encodeByteToString("indra".getBytes()));
    }

    @Test
    public void testGenerateECDSAKeyPair() {
        KeyPair pair = factory.generateKeyPair();
        assertNotNull(pair);
        assertNotNull(pair.getPublic());
        assertNotNull(pair.getPrivate());
    }

    @Test
    public void testCreateSignature() {
        KeyPair pair = factory.generateKeyPair();
        assertNotNull(pair);

        byte[] signature =
                factory.createSignature(pair.getPrivate(), "Just a test");
        assertNotNull(signature);
        assertTrue(signature.length > 0);
    }

    @Test(expected = InvalidCryptoException.class)
    public void testCreateInvalidSignature() {
        KeyPair pair = factory.generateKeyPair();
        assertNotNull(pair);

        factory.createSignature(pair.getPrivate(), null);
    }

    @Test
    public void testVerifySignature() {
        KeyPair pair = factory.generateKeyPair();
        assertNotNull(pair);

        String input = "Just a signature";
        byte[] signature =
                factory.createSignature(pair.getPrivate(), input);
        assertNotNull(signature);
        assertTrue(signature.length > 0);

        assertTrue(factory.verifySignature(pair.getPublic(), input, signature));
    }

    @Test
    public void testVerifyInvalidSignature() {
        KeyPair pair = factory.generateKeyPair();
        assertNotNull(pair);

        String input = "Just an invalid signature";
        byte[] signature =
                factory.createSignature(pair.getPrivate(), input);
        assertNotNull(signature);
        assertTrue(signature.length > 0);

        assertFalse(factory.verifySignature(pair.getPublic(), input,
                Arrays.copyOf(signature, signature.length - 1)));
    }

    @Test
    public void testGetMerkleRoot() {
        assertNotNull(factory.getMerkleRoot(Arrays.asList("1", "2", "3")));
        assertNotNull(factory.getMerkleRoot(Arrays.asList("1", "2", "3", "4")));
    }
}
