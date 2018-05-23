package com.basaki.blockchain.node;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.StartupSettings;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class NodeService extends GossipService {

    public NodeService(int port, int logLevel,
            ArrayList<GossipMember> gossipMembers,
            GossipSettings settings) throws InterruptedException, UnknownHostException {
        super(port, logLevel, gossipMembers, settings);
    }
}
