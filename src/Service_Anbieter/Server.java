package Service_Anbieter;
import java.io.*;
import java.net.*;

public class Server {
    public void run() {
        try {
            int serverPort = 1337;
            InetAddress host = InetAddress.getByName("iotgateway");
            System.out.println("Connecting to server on port " + serverPort);

            Socket socket = new Socket(host,serverPort);
            socket.setSoTimeout(1000);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());
            PrintWriter toServer =
                    new PrintWriter(socket.getOutputStream(),true);
            BufferedReader fromServer =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            toServer.println("Hello from " + socket.getLocalSocketAddress());
            String line = fromServer.readLine();
            System.out.println("Client received: " + line + " from Server");
            toServer.close();
            fromServer.close();
            socket.close();
        }
        catch(UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Server client = new Server();
        while (true) {
            client.run();
            Thread.sleep(10000);
        }
    }
}