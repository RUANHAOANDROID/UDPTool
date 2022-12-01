package com.hao.gatetool.net;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @date: 2022/12/1
 * @author: 锅得铁
 * #
 */

public class UDPReceiver implements Runnable {
    private String TAG = "UDPReceiver";

    /**
     * The datagram socket used for receiving messages.
     */
    private DatagramSocket udpSocket;

    /**
     * The listener getting all the messages received here.
     */
    private ReceiverListener listener;

    /**
     * If connected to the network or not.
     */
    private boolean connected;


    public UDPReceiver() {

    }

    /**
     * The run() method of this thread. Checks for new packets,
     * extracts the message and IP address, and notifies the listener.
     */
    public void run() {
        while (connected) {
            try {
                final DatagramPacket packet = new DatagramPacket(
                        new byte[Constants.NETWORK_PACKET_SIZE], Constants.NETWORK_PACKET_SIZE);
                udpSocket.receive(packet);
                final String ip = packet.getAddress().getHostAddress();
                final int port = packet.getPort();
                final String message = new String(packet.getData(), Constants.MESSAGE_CHARSET).trim();

                if (listener != null) {
                    listener.messageArrived(message, ip, port);
                }
            }

            // Happens when socket is closed, or network is down
            catch (final IOException e) {
                if (connected) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a new UDP socket, and starts a thread listening
     * on the UDP port. If the UDP port is in use, a new port will be
     * tried instead.
     */
    public void startReceiver() {

        Log.d(TAG, "startReceiver: ");
        if (connected) {
            Log.d(TAG, "Already connected ");
        } else {
            int port = Constants.NETWORK_UDP_PORT;
            int counter = 0;

            while (counter < 50 && !connected) {
                try {
                    udpSocket = new DatagramSocket(port);
                    connected = true;

                    // The background thread watching for messages from the network.
                    final Thread worker = new Thread(this, "UDPReceiverWorker");
                    worker.start();

                    Log.d(TAG, "Connected to port: " + port);
                } catch (final IOException e) {
                    e.printStackTrace();
                    counter++;
                    port++;
                }
            }

            if (!connected) {
                final String error = "Failed to initialize udp network:" +
                        "\nNo available listening port between " + Constants.NETWORK_PRIVCHAT_PORT +
                        " and " + (port - 1) + "." +
                        "\n\nYou will not be able to receive private messages!";

                Log.d(TAG, error);
            }
        }
    }

    /**
     * Closes the UDP socket, and stops the thread.
     */
    public void stopReceiver() {
        if (!connected) {
            Log.d(TAG, "stopReceiver: " + connected);
        } else {
            connected = false;

            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }

            Log.d(TAG, "stopReceiver: " + connected);
        }
    }

    /**
     * Sets the listener who will receive all the messages
     * from the UDP packets.
     *
     * @param listener The object to register as a listener.
     */
    public void registerReceiverListener(final ReceiverListener listener) {
        this.listener = listener;
    }

}
