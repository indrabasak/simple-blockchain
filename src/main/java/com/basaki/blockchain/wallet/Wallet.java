package com.basaki.blockchain.wallet;

import com.basaki.blockchain.util.CryptoFactory;
import com.basaki.blockchain.exception.InsufficientFundException;
import com.basaki.blockchain.core.Transaction;
import com.basaki.blockchain.core.TransactionInput;
import com.basaki.blockchain.core.TransactionOutput;
import com.basaki.blockchain.core.UTXOSingleton;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Wallet {

    private final PublicKey publicKey;

    private final PrivateKey privateKey;

    public Wallet() {
        KeyPair pair = CryptoFactory.getInstance().generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
    }

    public Wallet(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Returns the current balance of the wallet by summing all the unspent
     * transactions.
     *
     * @return the current wallet balance
     */
    public float getBalance() {
        List<TransactionOutput>
                UTXOs = UTXOSingleton.getInstance().getUTXOs(publicKey);

        return UTXOs.stream()
                .filter(t -> t.isMine(publicKey))
                .collect(Collectors.summingDouble(TransactionOutput::getValue))
                .floatValue();
    }

    public Transaction transferFunds(PublicKey toAddress, float value) {
        List<TransactionOutput>
                utxos = UTXOSingleton.getInstance().getUTXOs(publicKey);

        List<TransactionInput> inputs = new ArrayList<>();
        List<TransactionOutput> outputs = new ArrayList<>();

        float total = 0;
        for (TransactionOutput utxo : utxos) {
            total += utxo.getValue();
            inputs.add(new TransactionInput(utxo));
            outputs.add(utxo);
            if (total > value) {
                break;
            }
        }

        if (total < value) {
            CryptoFactory factory = CryptoFactory.getInstance();
            String msg =
                    "Insufficient fund at address: " + factory.encodeByteToString(
                            publicKey.getEncoded());

            log.error(msg);

            throw new InsufficientFundException(msg);
        }

        // the excess amount is taken care of in transaction
        Transaction txn = new Transaction(publicKey, toAddress, value, inputs);
        txn.sign(privateKey);

        return txn;
    }
}
