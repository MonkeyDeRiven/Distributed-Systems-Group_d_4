
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

    public static Dataset dataSetToProcess;

    static crudService.Processor processor = new crudService.Processor(new Database());

    public  int nextFreeId = 0;

    //Table for Datasets from sensors
    private ArrayList<Dataset> DBContent = new ArrayList();


    //API functions CRUD
    @Override
    public void create(Dataset newDataset) throws TException{
        //if is true when the given dataset does not exist already!
        System.out.println(" ");
        if(read(newDataset.primaryKey) == null){
            DBContent.add(newDataset);
            System.out.println("New Dataset was persisted successfully!");
        }else
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





