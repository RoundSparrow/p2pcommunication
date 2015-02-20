package no.bouvet.p2pcommunication.multicast;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import no.bouvet.p2pcommunication.P2PCommunicationActivity;

public class MulticastAsyncTask extends AsyncTask<Void, String, Boolean> {

    private String networkInterfaceName;
    private String multicastAddress;
    private int port;


    public MulticastAsyncTask(String networkInterfaceName, String multicastAddress, int port) {
        this.networkInterfaceName = networkInterfaceName;
        this.multicastAddress = multicastAddress;
        this.port = port;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            NetworkInterface networkInterface = getNetworkInterface(networkInterfaceName);
            InetAddress multicastGroupAddress = getMulticastGroupAddress(multicastAddress);
            MulticastSocket multicastSocket = createMulticastSocket(networkInterface, multicastGroupAddress, port);
            String string = "Hello world from multicast 2.0";
            DatagramPacket datagramPacket = new DatagramPacket(string.getBytes(), string.length(), multicastGroupAddress, port);
            multicastSocket.send(datagramPacket);
            Log.i(P2PCommunicationActivity.TAG, "Sent: " + string + " to " + multicastAddress + ":" + port + ":" + networkInterfaceName);
        } catch (IOException e) {
            Log.e(P2PCommunicationActivity.TAG, e.toString());
        }
        return false;
    }

    private MulticastSocket createMulticastSocket(NetworkInterface networkInterface, InetAddress multicastGroupAddress, int port) throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket();
        multicastSocket.setNetworkInterface(networkInterface);
        multicastSocket.joinGroup(new InetSocketAddress(multicastGroupAddress, port), networkInterface);
        return multicastSocket;
    }

    private NetworkInterface getNetworkInterface(String networkInterfaceName) throws SocketException {
        return NetworkInterface.getByName(networkInterfaceName);
    }

    private InetAddress getMulticastGroupAddress(String multicastGroupAddress) throws UnknownHostException {
        return InetAddress.getByName(multicastGroupAddress);
    }



}
