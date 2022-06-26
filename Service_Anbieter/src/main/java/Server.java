
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.*;
import java.net.*;

public class Server {

    int primaryKey = 0;


    private void run2() throws TTransportException, IOException, InterruptedException { //NEW METHOD
        Database.main();
        int serverPort = 1337;
        ServerSocket serverSocket = new ServerSocket(serverPort);

        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..." +" Zeile 98");
        serverSocket.setSoTimeout(5000);
        Socket server = serverSocket.accept();//Change to Host with normal Socket
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

        while((line = fromClient.readLine()) != null) {
            wholeMessage += line;
            if(isBody){
                messageBody = line;
            }
            if(line.equals("")){
                isBody = true;
            }
        }

        System.out.println(line +" Zeile 126");

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
        /*
        try {
            /// Database port
            int dataBasePort = 5829;
            InetAddress dataBaseID = InetAddress.getByName("db");
            System.out.println("Trying to " + dataBasePort);

            Socket ToDatabase = new Socket(dataBaseID,dataBasePort);
            ToDatabase.setSoTimeout(1000);
            System.out.println("Just connected to " + ToDatabase.getRemoteSocketAddress());
            PrintWriter toDatabase =
                    new PrintWriter(ToDatabase.getOutputStream(),true);
            BufferedReader fromDatabase =
                    new BufferedReader(
                            new InputStreamReader(ToDatabase.getInputStream()));
            toDatabase.println("C,"+bodycontainsAsString);
            String answear = fromDatabase.readLine();
            //String[] answearPartitioned = answear.split(",");
            System.out.println("Recieved Data: "+ answear);
            toDatabase.close();
            fromDatabase.close();
        }
        catch(UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    */
        //4: sensorID 5: messageType 6: value 7:timeStamp

        String contentList[] = bodycontainsAsString.split("/");
        Dataset newDataset = new Dataset();
        newDataset.primaryKey = primaryKey++;
        newDataset.sensorID = Integer.parseInt(contentList[1]);
        newDataset.valueType = contentList[2];
        newDataset.sensorValue = Integer.parseInt(contentList[3]);
        newDataset.timestamp = contentList[3];

        try {
            TTransport transport;

            transport = new TSocket(InetAddress.getByName("db").toString().split("/")[1], 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            crudService.Client client = new crudService.Client(protocol);


            client.create(newDataset);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        } catch (UnknownHostException exception) {
            throw new RuntimeException(exception);
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
