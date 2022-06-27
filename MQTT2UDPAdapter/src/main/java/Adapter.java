import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter implements MqttCallback {
    static int port = 6969;
   static int messageID = 0;
    static DatagramSocket clientSocket;

    static {
        try {
            clientSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    static ArrayList<DatagramPacket> datagramBuffer = new ArrayList<>();

    public Adapter() throws SocketException {
    }

    public void subscribeToMQTTChannel() throws MqttException {
        MqttAsyncClient client = new MqttAsyncClient("tcp://broker.hivemq.com:1883", "mqttTemperatureChannel");
        MqttConnectOptions connOptions = new MqttConnectOptions();
        connOptions.setCleanSession(true);

        connOptions.setCleanSession(true);
        connOptions.setAutomaticReconnect(true);

        IMqttToken token =  client.connect(connOptions);
        token.waitForCompletion();

        client.subscribe("mqttData",1);
    }
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        byte[] payload = mqttMessage.getPayload();
        String message = payload.toString();
        byte[] byteArray = new byte[512];
        DatagramPacket sendData;
        sendData = createDatagramPacket(message);

        datagramBuffer.add(sendData);

    }
    static public DatagramPacket createDatagramPacket(String message) throws UnknownHostException {
        String srcAdr, dstAdr;
        InetAddress src = InetAddress.getByName("adapter");
        srcAdr = src.toString().split("/")[1];

        InetAddress dst = InetAddress.getByName("IoT");
        dstAdr = dst.toString().split("/")[1];

        String sentence = srcAdr.toUpperCase() + "," + dstAdr.toUpperCase() + "," + port + "," +
                messageID++ + "," + message + ",";

        byte[] byteMessage = new byte[512];
        byteMessage = sentence.getBytes();

        DatagramPacket sendData = new DatagramPacket(byteMessage, byteMessage.length, dst, port);

        return sendData;

    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    static public void main(String[] args) throws IOException, MqttException {

        Adapter adapter = new Adapter();
        adapter.subscribeToMQTTChannel();
        byte[] byteArray = new byte[512];
        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length);
        clientSocket.receive(packet);

        clientSocket.send(datagramBuffer.get(0));
        datagramBuffer.remove(0);

    }
}
