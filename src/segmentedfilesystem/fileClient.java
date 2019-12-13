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
import java.util.Collections;
import java.util.Comparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class fileClient {

    static int port = 6014;
    InetAddress address;
    static String stringAddress ="csci-4409.morris.umn.edu";
    DatagramSocket socket = null;
    DatagramPacket packet;


    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buf = new byte[1028];
        InetAddress address = InetAddress.getByName(stringAddress);
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                address, port);
        socket.send(packet);

        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 4, packet.getLength()-4);
        System.out.println("Pointless String should print: " + received);
        int i = 0;

        //FileOutputStream fos = new FileOutputStream("file2.txt");

        file file1 = new file();
        file file2 = new file();
        file file3 = new file();
        
        while ( !(file1.fileDone && file2.fileDone && file3.fileDone) ){
            socket.receive(packet);
            i++;
            byte[] rawData = packet.getData().clone();
            int status = ((byte)Array.get(rawData, 0));
            //byte[] fileID = Arrays.copyOfRange(rawData, 1,2);
            int fileID = rawData[1];
            int the = packet.getData()[1];
            byte[] packetNumber = Arrays.copyOfRange(rawData, 2,4).clone();
            byte[] data = Arrays.copyOfRange(rawData, 4, 1028).clone();
            ByteBuffer wrapped = ByteBuffer.wrap(packetNumber);

            //find the IDs of our three files so we can reassemble them later
            if (file1.noFileID() && !(fileID == file2.fileID) && !(fileID == file3.fileID)) {
                file1.fileID = fileID;
                System.out.println("FileID1 is: " + fileID);
            } else if (file2.noFileID() && !(fileID == file1.fileID) && !(fileID == file3.fileID)){
                file2.fileID = fileID;
                System.out.println("FileID2 is: " + fileID);
            } else if (file3.noFileID() && !(fileID == file1.fileID) && !(fileID == file2.fileID)) {
                file3.fileID = fileID;
                System.out.println("FileID3 is: " + fileID);
            }
            

            //1st bit is x, second is y 000000yx
        if ((status % 4) == 3) {
            //if 3 mod 4, then this is the last data packet for this file
            int num = wrapped.getShort();

            if (fileID == file1.fileID) {
                file1.numPackets = num;
                file1.fileArray.add(rawData);
                System.out.println("1tail packet number: " + file1.numPackets);
            } else if (fileID == file2.fileID) {
                file2.numPackets = num;
                file2.fileArray.add(rawData);
                System.out.println("2tail packet number: " + file2.numPackets);
            } else if (fileID == file3.fileID) {
                file3.numPackets = num;
                file3.fileArray.add(rawData);
                System.out.println("3tail packet number: " + file3.numPackets);
            }
        } else if ( (status % 2) == 0 ) {
                //if even, then its a header
                data = (Arrays.copyOfRange(rawData, 2, packet.getLength()).clone());
                if (fileID == file1.fileID) {
                    file1.fileName = new String(data);
                    System.out.println("File1 Name: " + file1.fileName);
                    //System.out.println("packet number: " + wrapped.getShort());
                } else if (fileID == file2.fileID ) {
                    file2.fileName= new String(data);
                    System.out.println("File2 Name: " + file2.fileName);
                    //System.out.println("packet number: " + wrapped.getShort());
                } else if (fileID == file3.fileID) {
                    file3.fileName = new String(data);
                    System.out.println("File3 Name: " + file3.fileName);
                    //System.out.println("packet number: " + wrapped.getShort());
                }
        } else {
            //if we get here, that means this is a regular data packet.
            if (fileID == file1.fileID) {
                file1.fileArray.add(rawData);
            } else if (fileID == file2.fileID) {
                file2.fileArray.add(rawData);
                //fos.write(data);
            } else if (fileID == file3.fileID) {
                file3.fileArray.add(rawData);
            }
        }

//            totalpackets = (file1.fileArray.size() + return1ifNotEmpty(file1) +
//                    + file2.fileArray.size() + return1ifNotEmpty(file2)
//                    + file3.fileArray.size() + return1ifNotEmpty(file3));
//            System.out.println("Total packets recorded:" + totalpackets);
            if(i % 20 == 0) {
                System.out.println("I: " + i);
            }

            if(!(file1.fileDone) && (file1.fileArray.size() >= file1.numPackets)) {
                file1.fileDone = true;
                System.out.println("file1 is done.");
            }
            if(!(file2 .fileDone) && (file2.fileArray.size() >= file2.numPackets)) {
                file2.fileDone = true;
                System.out.println("file2 is done.");
            }
            if(!(file3.fileDone) && (file3.fileArray.size() >= file3.numPackets)) {
                file3.fileDone = true;
                System.out.println("file3 is done.");
            }
        }
        System.out.println("Done receiving packets.");

        file1.sortFileArray();
        file2.sortFileArray();
        file3.sortFileArray();
        file1.writeFile();
        file2.writeFile();
        file3.writeFile();

        System.out.println("Done writing.");

    }

    public static class file{
        private boolean fileDone = false;
        private int fileID = Integer.MAX_VALUE;
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
        public void setFileID(int fileID) {
            this.fileID = fileID;
        }
        public int getFileID() {
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
            return (this.fileID == Integer.MAX_VALUE);
        }

        public void sortFileArray() {

//            int numSwitches = 1;
//            //I had troubles using a comparator class,
//            //so this sorting method uses numSwitches to determine if our last pass through changed any elements.
//            //hence if this is zero by the end of a loop, the array was sorted.
//            while (!(numSwitches == 0)) {
//                numSwitches = 0;
//            for (int i = 0; i < (this.fileArray.size() - 1) ; i++) {
//                ByteBuffer wrapped1 = ByteBuffer.wrap(Arrays.copyOfRange(this.fileArray.get(i), 2,4));
//                ByteBuffer wrapped2 = ByteBuffer.wrap(Arrays.copyOfRange(this.fileArray.get(i+1), 2,4));
//                if( wrapped1.getShort() > wrapped2.getShort()) {
//                    byte[] tempvar = (this.fileArray.get(i)).clone();
//                    this.fileArray.set(i, (this.fileArray.get(i + 1)).clone());
//                    this.fileArray.set(i + 1, tempvar);
//                    numSwitches++;
//                }
//                System.out.println(Arrays.toString(this.fileArray.get(i)));
//                }
//            }
            this.fileArray.sort(new sortByPacket());
            System.out.println("Done sorting: " + this.fileName);
        }

        public void writeFile() throws IOException {

            File f = new File(this.fileName);

            FileOutputStream FOStream = new FileOutputStream(f);

            try {

            for (int j = 0; j < this.fileArray.size(); j++){
                System.out.println("File array[" + j +"] length" + this.fileArray.get(j).length  );
                FOStream.write((Arrays.copyOfRange(this.fileArray.get(j), 4, (this.fileArray.get(j).length-1) ) ).clone());
            }
            } catch (FileNotFoundException e) {
                System.out.println("File not found" + e);
            } catch (IOException ioe) {
                System.out.println("Exception while writing file " + ioe);
            }
            finally {
                // close the streams using close method
                try {
                    if (FOStream != null) {
                    }
                    FOStream.close();
                } catch (IOException ioe) {
                    System.out.println("Error while closing stream: " + ioe);
                }
            }

        }

    }

    static class sortByPacket implements Comparator<byte[]> {
        public int compare(byte[] b1, byte[] b2) {
            ByteBuffer wrapped1 = ByteBuffer.wrap(Arrays.copyOfRange(b1, 2,4));
            ByteBuffer wrapped2 = ByteBuffer.wrap(Arrays.copyOfRange(b2, 2,4));
            return Integer.compare(wrapped1.getShort(), wrapped2.getShort());
        }
    }

    public static int return1ifNotEmpty(file ifile) {
      if (ifile.fileName.equals("")) {
          return 0;
      } else return 1;
    }

//Below are old methods I longer need, but might want to refer to for help later perhaps.

//    public boolean compareBytes(byte[] A1, byte[] A2) {
//        return A1[1] > A2[1];
//    }


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
