package no.bouvet.p2pcommunication.multicast;

import android.app.IntentService;
import android.content.Intent;
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

public class MulticastListenerIntentService extends IntentService {

    public static final String ACTION_LISTEN_FOR_MULTICAST = "no.bouvet.p2pcommunication.multicast.action.ACTION_LISTEN_FOR_MULTICAST";
    public static final String EXTRA_MULTICAST_GROUP_ADDRESS = "no.bouvet.p2pcommunication.multicast.extra.EXTRA_MULTICAST_GROUP_ADDRESS";
    public static final String EXTRA_MULTICAST_PORT = "no.bouvet.p2pcommunication.multicast.extra.EXTRA_MULTICAST_PORT";
    public static final String EXTRA_NETWORK_INTERFACE = "no.bouvet.p2pcommunication.multicast.extra.EXTRA_NETWORK_INTERFACE";
    private static final int DEFAULT_PORT = 0;
    private boolean running = false;


    public MulticastListenerIntentService() {
        super("MulticastListenerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ACTION_LISTEN_FOR_MULTICAST)) {
            try {
                running = true;
                NetworkInterface networkInterface = getNetworkInterface(intent);
                InetAddress multicastGroupAddress = getMulticastGroupAddress(intent);
                int port = getPort(intent);
                MulticastSocket multicastSocket = createMulticastSocket(networkInterface, multicastGroupAddress, port);
                //MulticastSocket multicastSocket = new MulticastSocket(port);
                //multicastSocket.joinGroup(multicastGroupAddress);
                byte[] buffer = new byte[1024];
                Log.i(P2PCommunicationActivity.TAG, "Listening");
                while (running) {
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(datagramPacket);
                    String receivedMessage = new String(buffer, 0, datagramPacket.getLength());
                    Log.i(P2PCommunicationActivity.TAG, "Received: " + receivedMessage + " from " + datagramPacket.getAddress().getHostAddress());
                }

            } catch (IOException e) {
                Log.e(P2PCommunicationActivity.TAG, e.toString());
            }
        }

    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private MulticastSocket createMulticastSocket(NetworkInterface networkInterface, InetAddress multicastGroupAddress, int port) throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.setNetworkInterface(networkInterface);
        multicastSocket.joinGroup(new InetSocketAddress(multicastGroupAddress, port), networkInterface);
        return multicastSocket;
    }

    private NetworkInterface getNetworkInterface(Intent intent) throws SocketException {
        return NetworkInterface.getByName(intent.getStringExtra(EXTRA_NETWORK_INTERFACE));
    }

    private InetAddress getMulticastGroupAddress(Intent intent) throws UnknownHostException {
        String multicastGroupAddress = intent.getStringExtra(EXTRA_MULTICAST_GROUP_ADDRESS);
        return InetAddress.getByName(multicastGroupAddress);
    }

    private int getPort(Intent intent) {
        return intent.getIntExtra(EXTRA_MULTICAST_PORT, DEFAULT_PORT);
    }


}