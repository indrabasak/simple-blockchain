package com.basaki.blockchain.core;

import com.basaki.blockchain.util.BlockChainConstants;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import static com.basaki.blockchain.util.BlockChainConstants.GENESIS_PREVIOUS_HASH;

@Slf4j
public class BlockChain {

    private List<Block> blocks = new ArrayList<>();

    public BlockChain() {
        blocks.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        return new Block(GENESIS_PREVIOUS_HASH, new ArrayList<>());
    }

    public void mineBlock(PublicKey minerAddress,
            List<Transaction> transactions) {
        Block block = new Block(blocks.get(blocks.size() - 1).getHash(),
                transactions);
        block.mineBlock(BlockChainConstants.DIFFICULTY);
        blocks.add(block);

    }

    public boolean isChainValid() {
        // you should always have the genesis block
        if (blocks.size() < 1) {
            log.debug("Chain contains 0 blocks.");
            return false;
        }

        // check genesis block
        Block genesis = blocks.get(0);
        if (!GENESIS_PREVIOUS_HASH.equals(genesis.getPreviousHash())) {
            log.debug("Chain is missing genesis block.");
            return false;
        }

        for (int i = 1; i < blocks.size(); i++) {
            Block current = blocks.get(i);
            Block previous = blocks.get(i - 1);

            if (!current.getHash().equals(current.calculateHash())) {
                log.debug("Hash at {} doesn't match calculated hash.", i);
                return false;
            }

            if (!current.getPreviousHash().equals(previous.getHash())) {
                log.debug("Previous hash at {} doesn't match.", i);
                return false;
            }
        }

        return true;
    }

    public List<Block> getBlocks() {
        return blocks;
    }
}
