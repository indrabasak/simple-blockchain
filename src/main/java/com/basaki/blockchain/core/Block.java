package com.basaki.blockchain.core;

import com.basaki.blockchain.exception.InvalidTransactionException;
import com.basaki.blockchain.util.BlockChainConstants;
import com.basaki.blockchain.util.CryptoFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Block {

    private String hash;

    private String previousHash;

    private List<Transaction> transactions;

    private long time;

    private int nonce;

    private String merkleRoot;

    public Block(String previousHash, List<Transaction> transactions) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        validateTransactions();
        this.time = Calendar.getInstance().getTimeInMillis();
        this.hash = calculateHash();
    }

    // for deserialization
    private Block() {
    }

    @JsonIgnore
    public boolean isGenesis() {
        return BlockChainConstants.GENESIS_PREVIOUS_HASH.equals(previousHash);
    }

    public String calculateHash() {
        return CryptoFactory.getInstance()
                .getSha256Hash(previousHash + time + nonce + merkleRoot);
    }

    public void mineBlock(int difficulty) {
        merkleRoot = CryptoFactory.getInstance().getMerkleRoot(
                transactions.stream().map(t -> t.getId()).collect(
                        Collectors.toList()));

        String target = new String(new char[difficulty])
                .replace('\0', '0');

        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }

        log.info("Block mined with hash - {}", hash);
    }

    private void validateTransactions() {
        for (Transaction txn : transactions) {
            if (!isGenesis() && !txn.process()) {
                String msg = String.format(
                        "Transaction %s - discarded as processing failed.",
                        txn.getId());
                log.error(msg);

                throw new InvalidTransactionException(msg);
            }

            log.info("Transaction {} successfully added to block", txn.getId());
        }
    }
}
