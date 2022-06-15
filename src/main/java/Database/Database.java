package Database;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Database implements crudService.Iface{
    //class for storing one db entry

    public static  Dataset dataSetToProcess;

    static crudService.Processor processor = new crudService.Processor(new Database());

    public  int nextFreeId = 0;

    //Table for Datasets from sensors
    private ArrayList<Dataset> DBContent = new ArrayList();


    //API functions CRUD
    @Override
    public void create(Dataset newDataset) throws TException{
        //if is true when the given dataset does not exist already!
        if(read(newDataset.primaryKey) == null){
            DBContent.add(newDataset);
            System.out.print("New Dataset was persisted successfully!");
            return;
        }
        System.out.println("Dataset already exists!");
    }

    @Override
    public Dataset read(int primaryKey) throws TException{
        Dataset wantedDataset = null;
        for(int i = 0; i < DBContent.size(); i++){
            if(DBContent.get(i).primaryKey == primaryKey){
                wantedDataset = DBContent.get(i);
            }
        }
        return wantedDataset;
    }

    @Override
    public void update(Dataset updatedDataset) throws TException{
        if(read(updatedDataset.primaryKey) == null) {
            System.out.println("Dataset does not exist!");
            return;
        }
        DBContent.get(updatedDataset.primaryKey).sensorID = updatedDataset.sensorID;
        DBContent.get(updatedDataset.primaryKey).valueType = updatedDataset.valueType;
        DBContent.get(updatedDataset.primaryKey).sensorValue = updatedDataset.sensorValue;
        DBContent.get(updatedDataset.primaryKey).timestamp = updatedDataset.timestamp;
        System.out.println("Dataset was updated successfully");
    }

    @Override
    public void remove(int primaryKey) throws TException {
        if(read(primaryKey) == null){
            System.out.println("Dataset does not exist!");
            return;
        }
        DBContent.remove(read(primaryKey));
        System.out.println("Dataset was removed successfully!");
    }

    public void run(ServerSocket serverSocket) throws IOException {

        //int myPort_isServer = 6788;
        int Service_AnbieterPort_isClient = 5829;

        /// wait for orders

        while(true) {
            InetAddress serverAdress = InetAddress.getByName("server");
            System.out.println("Trying to " + Service_AnbieterPort_isClient);
            Socket socketToService_Anbieter = serverSocket.accept();

            socketToService_Anbieter.setSoTimeout(1000);
            PrintWriter toService_Anbieter =
                    new PrintWriter(socketToService_Anbieter.getOutputStream(), true);
            BufferedReader fromService_Anbieter =
                    new BufferedReader(
                            new InputStreamReader(socketToService_Anbieter.getInputStream()));

            String theOrder = fromService_Anbieter.readLine();
            System.out.println(theOrder);
            //execute orders and Respond
            /*
            String[] theOrderPartitioned = theOrder.split(",");
            if (theOrderPartitioned[0].equals("C")) {
                create(new Dataset(nextFreeId++, 1, 2, Integer.parseInt(theOrderPartitioned[1]), theOrderPartitioned[2]));
                toService_Anbieter.println("success_created");
            } else if (theOrderPartitioned[0].equals("R")) {
                Dataset foundData = read(Integer.parseInt(theOrderPartitioned[1]));
                toService_Anbieter.println("success_read_" + foundData.primaryKey + "_" + foundData.sensorID + "_" + foundData.valueType + "_" + foundData.sensorValue + "_" + foundData.timestamp);
            } else if (theOrderPartitioned[0].equals("U")) {
                update(new Dataset(Integer.parseInt(theOrderPartitioned[5]), Integer.parseInt(theOrderPartitioned[1]), Integer.parseInt(theOrderPartitioned[2]), Integer.parseInt(theOrderPartitioned[3]), theOrderPartitioned[4]));
                toService_Anbieter.println("success_updated");
            } else if (theOrderPartitioned[0].equals("D")) {
                remove(Integer.parseInt(theOrderPartitioned[1]));
                toService_Anbieter.println("success_deleated");
            } else {
                toService_Anbieter.println("failed_TaskNotUnderstood");
            }
             */
        }
    }


    public static void main(String[] args) throws InterruptedException, IOException, TTransportException {
        TServer server;
        TServerTransport serverTransport;
        try {
            serverTransport = new TServerSocket(9090);
            server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            server.serve();
        }catch(TTransportException e){
            e.printStackTrace();
        }

        /*
        Database thisDatabase = new Database();
        ServerSocket servSocket = new ServerSocket(5829);
        thisDatabase.run(servSocket);
        */
    }
}





