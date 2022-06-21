
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
    public void run() {
        try {
            Database.main();
            /// server ... means IoT gateway
            int serverPort = 1337;
            InetAddress host = InetAddress.getByName("iotgateway");
            System.out.println("Trying to " + serverPort);

            Socket socket = new Socket(host,serverPort);
            socket.setSoTimeout(1000);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());
            PrintWriter toServer =
                    new PrintWriter(socket.getOutputStream(),true);
            BufferedReader fromServer =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            toServer.println("POST sensordaten HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Accept: */*\r\n" +
                    "Accept-Language:de-de\r\n" +
                    "Accet-Encoding: gzip, deflate\r\n"+
                    "User-Agent: Mozilla/5.0\r\n" +
                    "Content-Length: 17\r\n" +
                    "\r\n"+
                    "Sende+Sensordaten\r\n");
            String line = fromServer.readLine();



            int bodyStart = 0;
            int bodyEnd = 0;
            char[] chars = line.toCharArray();
            boolean endit = false;
            if(line == "request not understood"){
                System.out.println("Error request not understood");
            }else{
                for(int i = 0;i<chars.length;i++){
                    if(endit){
                        break;
                    }
                    if(chars[i] == 'y' && chars[i+1] == '>'){
                        bodyStart = i+2;
                        for(int e = i+2;e<chars.length;e++){
                            if(chars[e] == '<' && chars[e+1] == '/'){
                                bodyEnd = e-1;
                                endit = true;
                                break;
                            }
                        }
                    }
                }
            }

            String bodycontainsAsString = "";
            for(int h = bodyStart;bodyStart<bodyEnd;bodyStart++){
                bodycontainsAsString = bodycontainsAsString + "" + chars[bodyStart];
            }
            System.out.println("Recieved Data: "+ bodycontainsAsString);
            toServer.close();
            fromServer.close();
            socket.close();

            sendDataToDatabase(bodycontainsAsString);

        }
        catch(UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void run2() throws TTransportException, IOException, InterruptedException { //NEW METHOD

        int serverPort = 1337;
        ServerSocket serverSocket = new ServerSocket(serverPort);
        serverSocket.setSoTimeout(10000);
        System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

        Socket server = serverSocket.accept();//Change to Host with normal Socket
        System.out.println("Just connected to " + server.getRemoteSocketAddress());

        PrintWriter toClient =
                new PrintWriter(server.getOutputStream(), true);
        BufferedReader fromClient =
                new BufferedReader(
                        new InputStreamReader(server.getInputStream()));


        String messageBody = ""; // Here are the Data from the Sensor.
        String line = "";
        String responseMessage = "";
        int i = 0;
        while((line += fromClient.readLine()) != null) {
            if(i == 7){
                messageBody = line;
            }
            i++;
        }

        System.out.println(line);

        responseMessage = createResponseMessage(messageBody != null);
        toClient.println(responseMessage);

        //sendDataToDatabase(bodycontainsAsString);

        //Database.main();
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
