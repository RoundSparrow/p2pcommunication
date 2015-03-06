package no.bouvet.p2pcommunication.multicast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MulticastConnectionInfoHelper {

    private static final int PORT = 40000;
    private static final String GROUP_IP = "239.255.1.1";
    private static final String NETWORK_INTERFACE_NAME = "p2p-wlan0-0";
    private static final String ALTERNATE_NETWORK_INTERFACE_NAME = "p2p-p2p0";

    public static int getPort() {
        return PORT;
    }

    public static InetAddress getMulticastGroupAddress() throws UnknownHostException {
       return InetAddress.getByName(GROUP_IP);
    }

    public static NetworkInterface getNetworkInterface() throws SocketException {
        NetworkInterface networkInterface = null;
        Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface tempNetworkInterface = networkInterfaceEnumeration.nextElement();
            if (tempNetworkInterface.isUp() && (tempNetworkInterface.getDisplayName().equals(NETWORK_INTERFACE_NAME) || tempNetworkInterface.getDisplayName().contains(ALTERNATE_NETWORK_INTERFACE_NAME))) {
                networkInterface = tempNetworkInterface;
            }
        }
        return networkInterface;
    }
}
