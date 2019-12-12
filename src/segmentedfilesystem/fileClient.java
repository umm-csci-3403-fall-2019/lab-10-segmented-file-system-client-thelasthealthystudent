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
import java.lang.reflect.Array;

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
        int i = 1;
        
        file file1 = new file();
        file file2 = new file();
        file file3 = new file();
        
        while (!(file1.fileDone && file2.fileDone && file3.fileDone)){
            socket.receive(packet);
            byte[] rawData = packet.getData();
            byte[] status = Arrays.copyOfRange(rawData, 0, 1);
            byte[] fileID = Arrays.copyOfRange(rawData, 1,2);
            //byte[] fileID = Array.get(rawData, 1);
            byte[] packetNumber = Arrays.copyOfRange(rawData, 2,4);
            byte[] data = Arrays.copyOfRange(rawData, 4, packet.getLength());
            ByteBuffer wrapped = ByteBuffer.wrap(packetNumber);

            if (file1.noFileID() && !(Arrays.equals(fileID, file1.fileID))) {
                file1.fileID = fileID;
            } else if (file2.noFileID() && !(Arrays.equals(fileID, file2.fileID))) {
                file2.fileID = fileID;
            } else if (file3.noFileID()) {
                file3.fileID = fileID;
            }
            

            //1st bit is x, second is y 000000yx
            if ( (status[0] % 2) == 0 ) {
                //if even, then its a header
                data = Arrays.copyOfRange(rawData, 2, packet.getLength());
                if (Arrays.equals(fileID, file1.fileID)) {
                    file1.fileName = new String(data);
                    System.out.println(file1.fileName + "1packet number: " + wrapped.getShort());
                } else if (Arrays.equals(fileID, file2.fileID)) {
                    file2.fileName= Arrays.toString(data);
                    System.out.println(file2.fileName + "2packet number: " + wrapped.getShort());
                } else if (Arrays.equals(fileID, file3.fileID)) {
                    file3.fileName = Arrays.toString(data);
                    System.out.println(file3.fileName + "3packet number: " + wrapped.getShort());}

            } else if ((status[0] % 4) == 3) {
                //if 3 mod 4, then this is the last data packet for this file
                int num = wrapped.getShort();

                if (Arrays.equals(fileID, file1.fileID)) {
                    file1.numPackets = num;
                    System.out.println("1tail packet number: " + file1.numPackets);
                } else if (Arrays.equals(fileID, file2.fileID)) {
                    file2.numPackets = num;
                    System.out.println("2tail packet number: " + file2.numPackets);
                } else if (Arrays.equals(fileID, file3.fileID)) {
                    file3.numPackets = num;
                    System.out.println("3tail packet number: " + file3.numPackets);
                }
            } else {
                //if we get here, that means this is a regular data packet.
                if (Arrays.equals(fileID, file1.fileID)) {
                    file1.fileArray.add(data);
                } else if (Arrays.equals(fileID, file2.fileID)) {
                    file2.fileArray.add(data);
                } else if (Arrays.equals(fileID, file3.fileID)) {
                    file3.fileArray.add(data);
                    //OStream3.write(data);
                }
            }

            System.out.println("Packet: " + i);
            i++;


            if(file1.fileArray.size() >= file1.numPackets) {
                file1.fileDone = true;
                System.out.println("file1 is done.");
            }
            if(file2.fileArray.size() >= file2.numPackets) {
                file2.fileDone = true;
                System.out.println("file2 is done.");
            }
            if(file3.fileArray.size() >= file3.numPackets) {
                file3.fileDone = true;
                System.out.println("file3 is done.");
            }
        }
        System.out.println("Done writing.");

        sortBytes();


        for (i = 0; i < file1.fileArray.size() ; i++) {

        }
    }

    public static void sortBytes() {

    }

    public boolean compareBytes(byte[] A1, byte[] A2) {
        return A1[1] > A2[1];
    }

    public static class file{
        private boolean fileDone = false;
        private byte[] fileID = new byte[2];
        private String fileName = "";
        private int numPackets = Integer.MAX_VALUE;
        private ArrayList<byte[]> fileArray = new ArrayList<>();

        //fileDone methods
        public void setFileDone(boolean fileDone) {
            this.fileDone = fileDone;
        }
        public boolean getFileDone() {
            return fileDone;
        }

        //fileID methods
        public void setFileID(byte[] fileID) {
            this.fileID = fileID;
        }
        public byte[] getFileID() {
            return fileID;
        }

        //FileName methods
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        public String getFileName() {
            return fileName;
        }

        //numPacket Methods
        public void setNumPackets(int numPackets) {
            this.numPackets = numPackets;
        }
        public int getNumPackets() {
            return numPackets;
        }

        public void setFileArray(ArrayList<byte[]> fileArray) {
            this.fileArray = fileArray;
        }
        public ArrayList<byte[]> getFileArray() {
            return fileArray;
        }
        
        public boolean noFileName() {
            return (this.fileName.equals(""));
        }

        public boolean noFileID() {
            return (Arrays.equals(this.fileID, new byte[2]));
        }
        
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
//public static boolean emptyByte(byte[] barray) {
//    for (byte b : barray) {
//        if (b == 1) {
//            return false;
//        }
//    }
//        return true;
//}

}
