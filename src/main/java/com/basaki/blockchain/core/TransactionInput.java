package com.basaki.blockchain.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class TransactionInput {

    private TransactionOutput utxo;

    public TransactionInput(TransactionOutput utxo) {
        this.utxo = utxo;
    }

    // for deserialization
    private TransactionInput() {
    }

    @JsonIgnore
    public String getId() {
        return utxo.getId();
    }
}
