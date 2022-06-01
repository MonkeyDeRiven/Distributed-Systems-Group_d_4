package Service_Anbieter;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {

    public static void main(String args[]) throws IOException {
        ServerSocket weclomeSocket = new ServerSocket(1337);
        byte[] buffer = new byte[2];
        String sentence = new String("");
        try (FileWriter myWriter = new FileWriter("StoredData.txt")) {

            while (true) {
                Socket connectionSocket = weclomeSocket.accept();
                BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                sentence += inputFromClient.readLine();
                if (sentence.length() < 2) {
                    continue;
                }
                    String sizeOfSentenceString = "" + sentence.charAt(0) + sentence.charAt(1);

                    buffer = sizeOfSentenceString.getBytes();
                    ByteBuffer wrapped = ByteBuffer.wrap(buffer);
                    short sizeOfMessage = wrapped.getShort();

                    if (sentence.length() > 2 + sizeOfMessage) {
                        sentence = sentence.substring(2);
                        String[] sentenceArray = sentence.substring(0, sizeOfMessage).split(",");
                        String comepleteMessage = "";
                        String messageID = "";
                        for(int i = 4; i<sentenceArray.length; i++){
                            comepleteMessage += sentenceArray[i];
                            if(i == 3)
                                messageID = sentenceArray[i];
                        }
                        myWriter.write(comepleteMessage + "\n");
                        System.out.println(comepleteMessage +"\n");
                        sentence = sentence.substring(sizeOfMessage);

                        String ack = messageID.length() + messageID;
                        outToClient.write(ack.getBytes());
                    }
            }
        }
        catch (Exception e){e.getMessage();}
    }
}