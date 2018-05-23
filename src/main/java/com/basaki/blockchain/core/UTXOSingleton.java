package com.basaki.blockchain.core;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UTXOSingleton {

    private static UTXOSingleton instance;

    private Map<String, TransactionOutput> UTXOs;

    private UTXOSingleton() {
        UTXOs = new ConcurrentHashMap<>();
    }

    public static synchronized UTXOSingleton getInstance() {
        if (instance == null) {
            instance = new UTXOSingleton();
        }

        return instance;
    }

    public List<TransactionOutput> getUTXOs(PublicKey publicKey) {
        return UTXOs.values().stream()
                .filter(t -> t.isMine(publicKey))
                .collect(Collectors.toList());
    }

    public boolean containUTXOs(List<TransactionInput> transactions) {
        return transactions.stream().anyMatch(
                t -> UTXOs.containsKey(t.getId()));
    }

    public void addUTXO(TransactionOutput transaction) {
        UTXOs.put(transaction.getId(), transaction);
    }

    public void addUTXOs(List<TransactionOutput> transactions) {
        for (TransactionOutput txn : transactions) {
            UTXOs.put(txn.getId(), txn);
        }
    }

    public void removeUTXO(TransactionOutput transaction) {
        UTXOs.remove(transaction.getId());
    }

    public void removeUTXO(List<TransactionOutput> transactions) {
        for (TransactionOutput txn : transactions) {
            UTXOs.remove(txn.getId());
        }
    }
}
