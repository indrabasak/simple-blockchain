package com.basaki.blockchain.core;

import com.basaki.blockchain.util.CryptoFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static com.basaki.blockchain.util.BlockChainConstants.MINIMUM_TRANSACTION_VALUE;

@Data
@Slf4j
public class Transaction {

    private String id;

    private PublicKey fromAddress;

    private PublicKey toAddress;

    private double value;

    private List<TransactionInput> inputs;

    private List<TransactionOutput> outputs;

    private byte[] signature;

    public Transaction(PublicKey fromAddress, PublicKey toAddress,
            float value, List<TransactionInput> inputs) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.value = value;
        this.inputs = inputs;
        this.outputs = new ArrayList<>();
        this.id = calculateHash();
    }

    // for deserialization
    private Transaction() {

    }

    public void sign(PrivateKey privateKey) {
        CryptoFactory factory = CryptoFactory.getInstance();

        String input = factory.encodeByteToString(fromAddress.getEncoded()) +
                factory.encodeByteToString(toAddress.getEncoded()) +
                value;

        this.signature = factory.createSignature(privateKey, input);
    }

    public boolean verify() {
        if (signature == null) {
            return false;
        }

        CryptoFactory factory = CryptoFactory.getInstance();

        String input = factory.encodeByteToString(fromAddress.getEncoded()) +
                factory.encodeByteToString(toAddress.getEncoded()) +
                value;

        return factory.verifySignature(fromAddress, input, signature);
    }

    public boolean isValid() {
        if (!verify()) {
            log.error("Signature verification failed.");
            return false;
        }

        // already processed
        if (!inputs.isEmpty() && !outputs.isEmpty()) {
            if (getInputSum() == getOutputSum()) {
                return true;
            } else {
                return false;
            }
        }

        // checks if input transactions are still unspent
        if (UTXOSingleton.getInstance().containUTXOs(inputs)) {
            log.error("Transaction failed as UTXOs doesn't exist anymore");
            return false;
        }

        double inputSum = getInputSum();

        // check if unspent transaction amount is greater than minimum amount
        if (inputSum < MINIMUM_TRANSACTION_VALUE) {
            log.error("Transaction input, {}, is less than minimum amount {}.",
                    inputSum, MINIMUM_TRANSACTION_VALUE);
            return false;
        }

        if (inputSum < value) {
            log.error(
                    "Transaction input, {}, is less than transaction value {}.",
                    inputSum, value);
            return false;
        }

        return true;
    }

    public boolean process() {
        if (!verify()) {
            log.error("Signature verification failed.");
            return false;
        }

        // checks if input transactions are still unspent
        if (UTXOSingleton.getInstance().containUTXOs(inputs)) {
            log.error("Transaction failed as UTXOs doesn't exist anymore");
            return false;
        }

        double inputSum = getInputSum();

        // check if unspent transaction amount is greater than minimum amount
        if (inputSum < MINIMUM_TRANSACTION_VALUE) {
            log.error("Transaction input, {}, is less than minimum amount {}.",
                    inputSum, MINIMUM_TRANSACTION_VALUE);
            return false;
        }

        if (inputSum < value) {
            log.error("Transaction input, {}, is less than value {}.",
                    inputSum, value);
            return false;
        }

        // check for left over
        double leftOver = getInputSum() - value;

        //send value to the recipient
        outputs.add(new TransactionOutput(id, toAddress, value));

        // send the remaining amount back to the sender
        outputs.add(new TransactionOutput(id, fromAddress, leftOver));

        // add unspent outputs to global UTXOs
        UTXOSingleton.getInstance().addUTXOs(outputs);

        // remove spent transaction outputs from global UTXOs
        UTXOSingleton.getInstance().removeUTXO(
                inputs.stream().map(i -> i.getUtxo()).collect(
                        Collectors.toList()));

        return true;
    }

    public double getInputSum() {
        return inputs.stream()
                .collect(Collectors.summingDouble(t -> t.getUtxo().getValue()));
    }

    public double getOutputSum() {
        return outputs.stream()
                .collect(Collectors.summingDouble(t -> t.getValue()));
    }

    private String calculateHash() {
        CryptoFactory factory = CryptoFactory.getInstance();

        return factory.getSha256Hash(
                factory.encodeByteToString(fromAddress.getEncoded()) +
                        factory.encodeByteToString(toAddress.getEncoded()) +
                        value);
    }
}
