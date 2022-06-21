import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

class TCPThread extends Thread {
    String udpDatagram = "";

    TCPThread(String udpDatagram) {
        this.udpDatagram = udpDatagram;
    }

    @Override
    public void run() {

        try {
            connectoToHostAndSendRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void connectoToHostAndSendRequest() throws IOException {
        InetAddress host = InetAddress.getByName("server");
        int port = 1337;
        Socket socket = new Socket(host, port);


        PrintWriter toServer =
                new PrintWriter(socket.getOutputStream(), true);
        BufferedReader fromServer =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

        String requestMessage = "";
        requestMessage = createMessage("server", this.udpDatagram);
        socket.setSoTimeout(5000);
        System.out.println("Just connected to " + socket.getRemoteSocketAddress());
        toServer.println(requestMessage);


        String httpRequest = "";
        while ((httpRequest += fromServer.readLine()) != null) {

        }

        toServer.close();
        fromServer.close();
        socket.close();

        this.stop();
    }

    public String createMessage(String host, String message) {
        String httpFormat =
                "POST / HTTP/1.1\n" +
                        "Host:" + host + "\n" +
                        "User-Agent: iot_gate_way_group_4\n" +
                        "Accept: Yes\n" +
                        "Content-Length:" + message.length() + "\n" +
                        "Content-Type:text/plain\n\n" +
                        message + "\n";

        return httpFormat;
    }
}
