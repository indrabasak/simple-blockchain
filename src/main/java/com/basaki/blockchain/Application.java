package com.basaki.blockchain;

import com.google.code.gossip.GossipMember;
import com.google.code.gossip.GossipService;
import com.google.code.gossip.GossipSettings;
import com.google.code.gossip.RemoteGossipMember;
import com.google.code.gossip.examples.GossipExample;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;


public class Application extends Thread {

    private static final int NUMBER_OF_CLIENTS = 4;

    public static void main(String[] args) {
        new GossipExample();
    }

    public Application() {
        this.start();
    }

    public void run() {
        try {
            GossipSettings settings = new GossipSettings();
            ArrayList<GossipService> clients = new ArrayList();
            String myIpAddress = InetAddress.getLocalHost().getHostAddress();
            ArrayList<GossipMember> startupMembers = new ArrayList();

            for(int i = 0; i < 4; ++i) {
                startupMembers.add(new RemoteGossipMember(myIpAddress, 2000 + i));
            }

            Iterator var6 = startupMembers.iterator();

            while(var6.hasNext()) {
                GossipMember member = (GossipMember)var6.next();
                GossipService gossipService = new GossipService(member.getPort(), 3, startupMembers, settings);
                clients.add(gossipService);
                gossipService.start();
                sleep((long)(settings.getCleanupInterval() + 1000));
            }

            sleep(10000L);
            System.err.println("Going to shutdown all services...");
            ((GossipService)clients.get(0)).shutdown();
        } catch (UnknownHostException var8) {
            var8.printStackTrace();
        } catch (InterruptedException var9) {
            var9.printStackTrace();
        }

    }
}
