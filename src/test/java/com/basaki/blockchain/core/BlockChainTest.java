package com.basaki.blockchain.core;


import com.basaki.blockchain.util.CryptoFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static com.basaki.blockchain.util.BlockChainConstants.GENESIS_PREVIOUS_HASH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BlockChainTest {

    private PublicKey publicKey;

    @Before
    public void startUp() {
        KeyPair pair = CryptoFactory.getInstance().generateKeyPair();
        publicKey = pair.getPublic();
    }

    @Test
    public void testNewBlockChain() {
        BlockChain chain = new BlockChain();
        assertNotNull(chain);

        List<Block> blocks = chain.getBlocks();
        assertTrue(blocks.size() == 1);

        Block genesisBlock = blocks.get(0);
        assertNotNull(genesisBlock);
        assertEquals(GENESIS_PREVIOUS_HASH,
                genesisBlock.getPreviousHash());
    }

    @Test
    public void testAddBlock() {
        BlockChain chain = new BlockChain();
        assertNotNull(chain);

        chain.mineBlock(publicKey, new ArrayList<Transaction>());
        List<Block> blocks = chain.getBlocks();
        assertTrue(blocks.size() == 2);
    }

    @Test
    public void testIsChainValid() {
        BlockChain chain = new BlockChain();
        assertNotNull(chain);

        chain.mineBlock(publicKey, new ArrayList<Transaction>());
        assertTrue(chain.isChainValid());
    }
}
