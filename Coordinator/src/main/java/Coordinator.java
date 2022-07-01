import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Coordinator implements CrudPcaService.Iface {

    @Override
    public void create(Dataset newDataset) throws TException, UnknownHostException {

        int numberOfDatabases = Integer.parseInt(System.getenv("numberOfDatabases"));

        for(int i = 0; i < numberOfDatabases; i++){

        }

        TTransport transport;
        transport = new TSocket(InetAddress.getByName("db").toString().split("/")[1], 9090);
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        CrudPcaService.Client client = new CrudPcaService.Client(protocol);

        boolean db1 = client.prepare(newDataset);

        client.create(newDataset);
    }

    @Override
    public Dataset read(int primaryKey) throws TException {
        return null;
    }

    @Override
    public void update(Dataset updatedDataSet) throws TException {

    }

    @Override
    public void remove(int primaryKey) throws TException {

    }

    @Override
    public boolean prepare(Dataset newDataset) throws TException {
        return false;
    }

    @Override
    public void commit(int primaryKey) throws TException {

    }

    @Override
    public void abort(int primaryKey) throws TException {

    }
}
