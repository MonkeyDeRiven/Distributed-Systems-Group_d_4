
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.*;
import java.net.*;

public class Server {
    int counter = 0;

    int primaryKey = 0;
    static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void run2() throws TTransportException, IOException, InterruptedException { //NEW METHOD


        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..." +" Zeile 98");
        Socket server = serverSocket.accept();
        System.out.println("Just connected to " + server.getRemoteSocketAddress());

        PrintWriter toClient =
                new PrintWriter(server.getOutputStream(), true);
        BufferedReader fromClient =
                new BufferedReader(
                        new InputStreamReader(server.getInputStream()));


        String messageBody = ""; // Here are the Data from the Sensor.
        String line = "";
        String wholeMessage = "";
        String responseMessage = "";
        boolean isBody = false;
        int i = 0;

        while(i<2) {
            line = fromClient.readLine();
            if(isBody){
                messageBody = line;
                isBody = false;
            }
            if(line.equals("")) {
                i++;
                if (i == 1)isBody = true;
            }
            wholeMessage += line;
        }


        responseMessage = createResponseMessage(messageBody != null);
        toClient.println(responseMessage);

        sendDataToDatabase(messageBody);

    }

    public String createResponseMessage(boolean messageRespondable){
        String response ="";
        if(messageRespondable) {
             response =
                    "HTTP/1.1 200 OK" + "\n" +
                            "Content-Type: text/plain\n" +
                            "Content-Length:" + response.length() +"\n\n" +
                            "";

        }
        else{
             response =
                    "HTTP/1.1 501 ERROR" + "\n" +
                            "Content-Type: text/plain\n" +
                            "Content-Length:" + response.length() +"\n\n" +
                            "";

        }

        return response;
    }
    private void sendDataToDatabase(String bodycontainsAsString) {

        System.out.println(bodycontainsAsString);
        String contentList[] = bodycontainsAsString.split(",");
        Dataset newDataset = new Dataset();
        newDataset.primaryKey = primaryKey++;
        System.out.println(primaryKey);
        newDataset.sensorID = Integer.parseInt(contentList[0]);
        newDataset.valueType = contentList[1];
        newDataset.sensorValue = Integer.parseInt(contentList[2]);
        newDataset.timestamp = contentList[3];

        try {
            TTransport transport;
            transport = new TSocket(InetAddress.getByName("db").toString().split("/")[1], 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            crudService.Client client = new crudService.Client(protocol);

            client.create(newDataset);
            counter++;
            System.out.println("counter: " + counter);
            if(counter == 5){
                System.out.println("Try to get Dataset");
                Dataset newData;
                newData = client.read(2);
                System.out.println(newData.sensorID + ", " + newData.timestamp);
                System.out.println("Dataset information should be one line above");
            }
            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException, TTransportException, IOException {
        Server client = new Server();

        while (true) {
            client.run2();
            Thread.sleep(10000);
        }
    }
}
