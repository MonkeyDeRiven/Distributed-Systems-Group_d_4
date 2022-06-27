import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class HTTPHandler extends Thread{
    int primaryKey = 0;
    int counter = 0;
    Socket server;
    Semaphore mutex = new Semaphore(1);
    HTTPHandler(Socket server, Semaphore mutex, int primaryKey){
        this.mutex = mutex ;
        this.server = server;
        this.primaryKey = primaryKey;
    }

    @Override
    public void run()  {
        System.out.println("Just connected to " + server.getRemoteSocketAddress());

        PrintWriter toClient =
                null;
        try {
            toClient = new PrintWriter(server.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader fromClient =
                null;
        try {
            fromClient = new BufferedReader(
                    new InputStreamReader(server.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String messageBody = ""; // Here are the Data from the Sensor.
        String line = "";
        String wholeMessage = "";
        String responseMessage = "";
        boolean isBody = false;
        int i = 0;

        while(i<2) {
            try {
                line = fromClient.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        mutex.v();
        newDataset.primaryKey = primaryKey++;
        mutex.p();
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

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
