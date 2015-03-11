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

public class MulticastMessageReceiverService extends IntentService {

    public static final String TAG = MulticastMessageReceiverService.class.getSimpleName();
    public static final String ACTION_LISTEN_FOR_MULTICAST = "ACTION_LISTEN_FOR_MULTICAST";
    public static final String EXTRA_MESSENGER = "EXTRA_MESSENGER";
    public static boolean isRunning = false;

    public MulticastMessageReceiverService() {
        super(MulticastMessageReceiverService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (action.equals(ACTION_LISTEN_FOR_MULTICAST)) {
            try {
                isRunning = true;
                Messenger messenger = getMessenger(intent);
                MulticastSocket multicastSocket = createMulticastSocket();
                byte[] buffer = new byte[1024];
                while (isRunning) {
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    multicastSocket.receive(datagramPacket);
                    String receivedMessage = new String(buffer, 0, datagramPacket.getLength());
                    String senderIpAddress = datagramPacket.getAddress().getHostAddress();
                    messenger.send(createMessage(receivedMessage, senderIpAddress));
                }
            } catch (IOException | RemoteException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private MulticastSocket createMulticastSocket() throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(getPort());
        multicastSocket.setNetworkInterface(getNetworkInterface());
        multicastSocket.joinGroup(new InetSocketAddress(getMulticastGroupAddress(), getPort()), getNetworkInterface());
        return multicastSocket;
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

    private NetworkInterface getNetworkInterface() throws SocketException {
        return MulticastConnectionInfoHelper.getNetworkInterface();
    }

    private InetAddress getMulticastGroupAddress() throws UnknownHostException {
        return MulticastConnectionInfoHelper.getMulticastGroupAddress();
    }

    private int getPort() {
        return MulticastConnectionInfoHelper.getPort();
    }

}