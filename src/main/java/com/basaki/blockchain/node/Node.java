package com.basaki.blockchain.node;

import com.basaki.blockchain.core.BlockChain;
import com.google.code.gossip.RemoteGossipMember;

public class Node extends RemoteGossipMember {

    private BlockChain chain;

    public Node(String hostname, int port, int heartbeat) {
        super(hostname, port, heartbeat);

        chain = new BlockChain();
    }
}
