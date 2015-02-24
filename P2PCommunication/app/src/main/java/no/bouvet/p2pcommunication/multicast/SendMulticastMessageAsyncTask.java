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

import no.bouvet.p2pcommunication.listener.MulticastMessageSentListener;

public class SendMulticastMessageAsyncTask extends AsyncTask<Void, String, Boolean> {

    public static final String TAG = "SendMulticastMessageAsyncTask";
    private MulticastMessageSentListener multicastMessageSentListener;
    private String multicastMessage;
    private String networkInterfaceString;
    private String multicastGroupAddressString;
    private int port;


    public SendMulticastMessageAsyncTask(MulticastMessageSentListener multicastMessageSentListener) {
        this.multicastMessageSentListener = multicastMessageSentListener;
        this.networkInterfaceString = MulticastConnectionInfoHelper.NETWORK_INTERFACE;
        this.multicastGroupAddressString = MulticastConnectionInfoHelper.MULTICAST_GROUP_ADDRESS;
        this.port = MulticastConnectionInfoHelper.MULTICAST_PORT;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = false;
        try {
            NetworkInterface networkInterface = getNetworkInterface(networkInterfaceString);
            InetAddress multicastGroupAddress = getMulticastGroupAddress(multicastGroupAddressString);
            MulticastSocket multicastSocket = createMulticastSocket(networkInterface, multicastGroupAddress, port);
            multicastMessage = multicastMessageSentListener.getMulticastMessageFromInputEditText();
            DatagramPacket datagramPacket = new DatagramPacket(multicastMessage.getBytes(), multicastMessage.length(), multicastGroupAddress, port);
            multicastSocket.send(datagramPacket);
            success = true;
        } catch (IOException ioException) {
            Log.e(SendMulticastMessageAsyncTask.TAG, ioException.toString());
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (!success) {
            multicastMessageSentListener.onMessageFailedToBeMulticasted();
        }
    }

    private MulticastSocket createMulticastSocket(NetworkInterface networkInterface, InetAddress multicastGroupAddress, int port) throws IOException {
        MulticastSocket multicastSocket = new MulticastSocket(port);
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
