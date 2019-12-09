package segmentedfilesystem;

import java.io.*;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class fileClient {

    static int port = 0;
    InetAddress address;
    static String stringAddress ="";
    DatagramSocket socket = null;
    DatagramPacket packet;


    public static void main(String[] args) throws IOException {
        OutputStream OStream1 = new FileOutputStream("file1");
        OutputStream OStream2 = new FileOutputStream("file2");
        OutputStream OStream3 = new FileOutputStream("file3");
        DatagramSocket socket = new DatagramSocket();
        byte[] buf = new byte[1028];
        InetAddress address = InetAddress.getByName(stringAddress);
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                address, port);
        socket.send(packet);

        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Pointless String should print: " + received);
        System.out.println();
        int i = 0;
        boolean file1Done = false;
        boolean file2Done = false;
        boolean file3Done = false;
        byte[] empty = new byte[2];
        byte[] fileID1 = empty;
        byte[] fileID2 = empty;
        byte[] fileID3 = empty;
        while (true){
            socket.receive(packet);
            byte[] rawData = packet.getData();
            byte[] status = Arrays.copyOfRange(rawData, 0, 1);
            byte[] fileID = Arrays.copyOfRange(rawData, 1,2);
            byte[] packetNumber = Arrays.copyOfRange(rawData, 3,5);
            byte[] data = Arrays.copyOfRange(rawData, 5, rawData.length);
            if (Arrays.equals(fileID1, empty)) {
                fileID1 = fileID;
            }
            if (!(Arrays.equals(fileID1, empty)) && !(Arrays.equals(fileID, fileID1))) {
                fileID2 = fileID;
            }
            if (!(Arrays.equals(fileID2, empty)) && !(Arrays.equals(fileID, fileID1)) && !(Arrays.equals(fileID, fileID2))) {
                fileID3 = fileID;
            }

            if (Arrays.equals(fileID, fileID1)){
                OStream1.write(data);
            } else if(Arrays.equals(fileID, fileID2)) {
                OStream2.write(data);
            } else if(Arrays.equals(fileID, fileID3)) {
                OStream3.write(data);
            }

            System.out.println("i: " + i);
            i++;
            if(file1Done && file2Done && file3Done){
                break;
            }
        }
        System.out.println("Done writing.");
    }

//old function used for terminating while true loop
public static boolean emptyByte(byte[] barray) {
    for (byte b : barray) {
        if (b == 1) {
            return false;
        }
    }
        return true;
}

}
