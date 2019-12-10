package segmentedfilesystem;

import java.io.*;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math.*;

public class fileClient {

    static int port = 6014;
    InetAddress address;
    static String stringAddress ="csci-4409.morris.umn.edu";
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
        int i = 1;
        boolean file1Done = false;
        boolean file2Done = false;
        boolean file3Done = false;
        byte[] empty = new byte[2];
        byte[] fileID1 = empty;
        byte[] fileID2 = empty;
        byte[] fileID3 = empty;
        String fileName1 = "";
        String fileName2 = "";
        String fileName3 = "";
        int numPackets1 = 0;
        int numPackets2 = 0;
        int numPackets3 = 0;
        ArrayList<byte[]> fileArray1 = new ArrayList<byte[]>();
        ArrayList<byte[]> fileArray2 = new ArrayList<byte[]>();
        ArrayList<byte[]> fileArray3 = new ArrayList<byte[]>();


        while (true){
            socket.receive(packet);
            byte[] rawData = packet.getData();
            byte[] status = Arrays.copyOfRange(rawData, 0, 1);
            byte[] fileID = Arrays.copyOfRange(rawData, 1,2);
            byte[] packetNumber = Arrays.copyOfRange(rawData, 3,5);
            byte[] data = Arrays.copyOfRange(rawData, 5, rawData.length);

            ByteBuffer wrapped = ByteBuffer.wrap(packetNumber);

            if (Arrays.equals(fileID1, empty)) {
                fileID1 = fileID;
            }
            if (!(Arrays.equals(fileID2, empty)) && !(Arrays.equals(fileID, fileID1))) {
                fileID2 = fileID;
            }
            if (!(Arrays.equals(fileID3, empty)) && !(Arrays.equals(fileID, fileID1)) && !(Arrays.equals(fileID, fileID2))) {
                fileID3 = fileID;
            }



            //1st bit is x, second is y 000000yx
            if ( (status[0] % 2) == 0 ) {
                //if even, then its a header
                data = Arrays.copyOfRange(rawData, 3,rawData.length);
                if (Arrays.equals(fileID, fileID1)) {
                    fileName1 = new String(data);
                    System.out.println(fileName1);
                }
                if (Arrays.equals(fileID, fileID2)) {
                    fileName2 = Arrays.toString(data);
                    System.out.println(fileName2);
                }
                if (Arrays.equals(fileID, fileID3)) {
                    fileName3 = Arrays.toString(data);
                    System.out.println(fileName3);}

            } else if ((status[0] % 4) == 3) {
                //if 3 mod 4, then this is the last data packet for this file
                int num = wrapped.getShort();

                if (Arrays.equals(fileID, fileID1)) {
                    numPackets1 = num;
                    System.out.println(numPackets1);
                }
                if (Arrays.equals(fileID, fileID2)) {
                    numPackets2 = num;
                    System.out.println(numPackets2);
                }
                if (Arrays.equals(fileID, fileID3)) {
                    numPackets3 = num;
                    System.out.println(numPackets3);
                }
            } else {
                //if we get here, that means this is a regular data packet.
                if (Arrays.equals(fileID, fileID1)) {
                    fileArray1.add(data);
                } else if (Arrays.equals(fileID, fileID2)) {
                    fileArray2.add(data);
                } else if (Arrays.equals(fileID, fileID3)) {
                    fileArray3.add(data);
                    //OStream3.write(data);
                }
            }

            System.out.println("Packet: " + i);
            i++;
            if(file1Done && file2Done && file3Done){
                break;
            }
        }
        System.out.println("Done writing.");

        sortBytes();


        for (i = 0; i < fileArray1.size() ; i++) {

        }
    }

    public static void sortBytes() {

    }

    public boolean compareBytes(byte[] A1, byte[] A2) {
        return A1[1] > A2[1];
    }


//    public class byteSort {
//        private byte[] data;
//        private int packetID;
//
//        public byteSort(byte[] data, int packetID) {
//            this.data = data;
//            this.packetID = packetID;
//        }
//        // getters and setters...
//    }
//
//    public static class comparer implements Comparable<comparer> {
//
//        @Override
//        public int compareTo(comparer o) {
//            return (this.packetID > this.packetID);
//        }
//    }

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
