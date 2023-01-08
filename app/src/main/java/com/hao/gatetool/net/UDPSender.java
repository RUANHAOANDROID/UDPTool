/***************************************************************************
 *   Copyright 2006-2016 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package com.hao.gatetool.net;


import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Sends UDP packets directly to a user. Useful for private chat,
 * where not everyone should get the packets.
 *
 * @author Christian Ihle
 */
public class UDPSender {

    /**
     * The datagram socket used for sending messages.
     */
    private DatagramSocket udpSocket;
    /**
     * If connected to the network or not.
     */
    private boolean connected;
    private String TAG = "UDPSender";

    public UDPSender() {

    }

    /**
     * Sends a packet with a message to a user.
     *
     * @param message The message to send.
     * @param ip      The ip address of the user.
     * @param port    The port to send the message to.
     * @return If the message was sent or not.
     */
    public boolean send(final String message, final String ip, final int port) {
        //Log.d(TAG, "Sent message: " + message + " to " + ip + ":" + port);
        if (connected) {
            try {
                final InetAddress address = InetAddress.getByName(ip);
                final byte[] encodedMsg = message.getBytes();
                final int size = encodedMsg.length;

                final DatagramPacket packet = new DatagramPacket(encodedMsg, size, address, port);
                udpSocket.send(packet);
                Log.d(TAG, "Sent message->:"+ ip + ":" + port+"\t"+ message );
                return true;
            } catch (final IOException e) {
                Log.d(TAG, "Could not send message: " + message, e);
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Creates a new UDP socket.
     */
    public void startSender() {

        Log.d(TAG, "startSender: " + "Connecting...");
        if (connected) {
            Log.d(TAG, "startSender: " + "Already connected.");
        } else {
            try {
                udpSocket = new DatagramSocket();
                connected = true;
                Log.d(TAG, "startSender: " + "Connected.");
            } catch (final IOException e) {
                Log.d(TAG, "startSender: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * Closes the UDP socket.
     */
    public void stopSender() {
        Log.d(TAG, "stopSender: Disconnecting...");

        if (!connected) {
            Log.d(TAG, "stopSender: Not connected.");
        } else {
            connected = false;

            if (udpSocket != null && !udpSocket.isClosed()) {
                udpSocket.close();
            }
            Log.d(TAG, "stopSender: Disconnected...");
        }
    }
}