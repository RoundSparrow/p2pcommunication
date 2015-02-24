package no.bouvet.p2pcommunication.multicast;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import no.bouvet.p2pcommunication.R;

public class MulticastMessageReceiverService extends IntentService {

    public static final String TAG = "MulticastMessageReceiverService";
    public static final String ACTION_LISTEN_FOR_MULTICAST = "no.bouvet.p2pcommunication.multicast.action.ACTION_LISTEN_FOR_MULTICAST";
    public static final String EXTRA_MULTICAST_GROUP_ADDRESS = "no.bouvet.p2pcommunication.multicast.extra.EXTRA_MULTICAST_GROUP_ADDRESS";
    public static final String EXTRA_MULTICAST_PORT = "no.bouvet.p2pcommunication.multicast.extra.EXTRA_MULTICAST_PORT";
    public static final String EXTRA_NETWORK_INTERFACE = "no.bouvet.p2pcommunication.multicast.extra.EXTRA_NETWORK_INTERFACE";
    public static final String EXTRA_MESSENGER = "no.bouvet.p2pcommunication.multicast.extra.EXTRA_MESSENGER";
    private static final int DEFAULT_PORT = 0;
    private boolean shouldBeRunning = false;


    public MulticastMessageReceiverService() {
        super("MulticastListenerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final String action = intent.getAction();
        if (action.equals(ACTION_LISTEN_FOR_MULTICAST)) {
            try {
                shouldBeRunning = true;
                Messenger messenger = getMessenger(intent);
                MulticastSocket multicastSocket = createMulticastSocket(intent);
                byte[] buffer = new byte[1024];
                Log.i(MulticastMessageReceiverService.TAG, getString(R.string.listening));
                while (shouldBeRunning) {
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(datagramPacket);
                    String receivedMessage = new String(buffer, 0, datagramPacket.getLength());
                    String senderIpAddress = datagramPacket.getAddress().getHostAddress();
                    Message message = createMessage(receivedMessage, senderIpAddress);
                    messenger.send(message);
                }
            } catch (IOException | RemoteException e) {
                Log.e(MulticastMessageReceiverService.TAG, e.toString());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shouldBeRunning = false;
        Log.i(MulticastMessageReceiverService.TAG, getString(R.string.stopped_listening));
    }

    private MulticastSocket createMulticastSocket(Intent intent) throws IOException {
        NetworkInterface networkInterface = getNetworkInterface(intent);
        InetAddress multicastGroupAddress = getMulticastGroupAddress(intent);
        int port = getPort(intent);
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

    private Messenger getMessenger(Intent intent) {
        return (Messenger) intent.getExtras().get(EXTRA_MESSENGER);
    }

    private Message createMessage(String receivedMessage, String senderIpAddress) {
        Bundle receivedData = new Bundle();
        receivedData.putString(MulticastMessageReceivedHandler.RECEIVED_MESSAGE, receivedMessage);
        receivedData.putString(MulticastMessageReceivedHandler.SENDER_IP_ADDRESS, senderIpAddress);
        Message message = new Message();
        message.setData(receivedData);
        return message;
    }

}