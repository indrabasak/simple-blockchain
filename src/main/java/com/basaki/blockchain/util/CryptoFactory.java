package com.basaki.blockchain.util;

import com.basaki.blockchain.exception.InvalidCryptoException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Slf4j
public class CryptoFactory {

    public static final String ALGO_HASH_SHA_256 = "SHA-256";

    private static final String ALGO_ECDSA = "ECDSA";

    private static CryptoFactory instance;

    private CryptoFactory() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static synchronized CryptoFactory getInstance() {
        if (instance == null) {
            instance = new CryptoFactory();
        }

        return instance;
    }

    public MessageDigest getMessageDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            String msg =
                    "Failed to create a message digest for algortithm "
                            + algorithm;
            log.error(msg, e);
            throw new InvalidCryptoException(msg, e);
        }
    }

    public String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }


    public String getSha256Hash(String input) {
        MessageDigest msgDigest =
                getMessageDigest(CryptoFactory.ALGO_HASH_SHA_256);

        return toHexString(msgDigest.digest(input.getBytes()));
    }

    public String encodeByteToString(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    public byte[] decodeStringToByte(String input) {
        return Base64.getDecoder().decode(input);
    }

    /**
     * ECDSA algorithm is not supported by out-of-the-box JCA implementation.
     *
     * @return
     */
    public KeyPair generateKeyPair() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            KeyPairGenerator keyGen =
                    KeyPairGenerator.getInstance(ALGO_ECDSA, "BC");
            keyGen.initialize(ecSpec, random);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            String msg =
                    "Failed to create a key pair for algortithm " + ALGO_ECDSA;
            log.error(msg, e);
            throw new InvalidCryptoException(msg, e);
        }
    }

    public PublicKey getPublicKey(byte[] encoded) {
        try {
            KeyFactory factory = KeyFactory.getInstance(ALGO_ECDSA, "BC");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
            return factory.generatePublic(spec);
        } catch (Exception e) {
            String msg = "Failed to generate public key.";
            log.error(msg, e);
            throw new InvalidCryptoException(msg, e);
        }
    }

    /**
     * Creates a signature from the private key and the input string.
     *
     * @param privateKey the private key used in signing
     * @param input      the input string to be used in the signature
     * @return the ECDSA signature
     */
    public byte[] createSignature(PrivateKey privateKey, String input) {
        try {
            Signature digitalSignature =
                    Signature.getInstance(ALGO_ECDSA, "BC");
            digitalSignature.initSign(privateKey);
            digitalSignature.update(input.getBytes());

            return digitalSignature.sign();
        } catch (Exception e) {
            String msg = "Failed to create signature";
            log.error(msg, e);
            throw new InvalidCryptoException(msg, e);
        }
    }

    /**
     * Verify a signature.
     *
     * @param publicKey the corresponding public key of the private key used to
     *                  create the signature
     * @param input     data which is part of the signature
     * @param signature the signature to be verified
     * @return true if the signature is valid
     */
    public boolean verifySignature(PublicKey publicKey, String input,
            byte[] signature) {
        try {
            Signature digitalSignature = Signature.getInstance("ECDSA", "BC");
            digitalSignature.initVerify(publicKey);
            digitalSignature.update(input.getBytes());

            return digitalSignature.verify(signature);
        } catch (Exception e) {
            String msg = "Failed to process signature.";
            log.error(msg, e);
        }

        return false;
    }

    /**
     * A {@code Merkle tree} is constructed by pairing the transaction ids and
     * hashing them and hashing the results until a single hash remains. The
     * remaining single hash is called the {@code Merkle Root}.
     *
     * @param ids all transactions within a block
     * @return a Merkle root
     */
    public String getMerkleRoot(List<String> ids) {
        CryptoFactory factory = CryptoFactory.getInstance();

        List<String> tier = ids;
        while (tier.size() > 1) {
            List<String> tmp = new ArrayList<>();
            for (int i = 1; i < tier.size(); i += 2) {
                tmp.add(factory.getSha256Hash(tier.get(i - 1) + tier.get(i)));
            }

            if (tier.size() % 2 != 0) {
                String hash = factory.getSha256Hash(tier.get(tier.size() - 1));
                tmp.add(hash + hash);
            }

            tier = tmp;
        }

        return (tier.size() == 1) ? tier.get(0) : "0";
    }
}
