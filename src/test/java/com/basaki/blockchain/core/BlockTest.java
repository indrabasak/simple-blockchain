package com.basaki.blockchain.core;

import com.basaki.blockchain.util.BlockChainConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BlockTest {

    @Test
    public void testCreation() {
        String previousHash = "A1B2C3";

        List<Transaction> transactions = new ArrayList<>();

        Block block = new Block(previousHash, transactions);
        assertNotNull(block);
        assertNotNull(block.getHash());
        assertEquals(previousHash, block.getPreviousHash());
        System.out.println(block.getTime());
        System.out.println(Calendar.getInstance().getTime().getTime());
        assertTrue(block.getTime() > 0 && Long.compare(block.getTime(),
                Calendar.getInstance().getTime().getTime()) <= 0);
    }

    @Test
    public void testCalculateHash() {
        String previousHash = "A1B2C3";
        List<Transaction> transactions = new ArrayList<>();

        Block block = new Block(previousHash, transactions);
        assertNotNull(block);
        assertEquals(block.getHash(), block.calculateHash());
    }

    @Test
    public void testMineBlock() {
        String previousHash = "A1B2C3D4";
        List<Transaction> transactions = new ArrayList<>();

        Block block = new Block(previousHash, transactions);
        assertNotNull(block);

        block.mineBlock(BlockChainConstants.DIFFICULTY);
        assertTrue(block.getHash().startsWith(
                new String(new char[BlockChainConstants.DIFFICULTY])
                        .replace('\0', '0')));
    }

    @Test
    public void testSerialization() throws IOException {
        String previousHash = "A1B2C3D4";
        List<Transaction> transactions = new ArrayList<>();

        Block block = new Block(previousHash, transactions);
        assertNotNull(block);
        block.mineBlock(BlockChainConstants.DIFFICULTY);
        System.out.println(block);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(block);
        Block blockCopy = objectMapper.readValue(json, Block.class);
        assertNotNull(blockCopy);
        assertEquals(block, blockCopy);
    }
}
