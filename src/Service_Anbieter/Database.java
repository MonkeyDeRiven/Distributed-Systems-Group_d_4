package Service_Anbieter;

import java.util.ArrayList;

public class Database {
    //class for storing one db entry
    private class Dataset {
        public int primaryKey;
        public int sensorID;
        public int valueType;
        public int sensorValue;
        public String timestamp;

        public boolean equals(Object compareDataset){
            if(this == compareDataset){
                return true;
            }
            if(!(compareDataset instanceof Dataset)){
                return false;
            }
            return this.primaryKey == ((Dataset) compareDataset).primaryKey;
        }

        public int hashCode(){
            return primaryKey;
        }
    }

    //Table for Datasets from sensors
    private ArrayList<Dataset> DBContent = new ArrayList();

    public static void main(String args[]){

    }

    //API functions CRUD
    public void create(Dataset newDataset){
        //if is true when the given dataset does not exist already!
        if(read(newDataset.primaryKey) == null){
            DBContent.add(newDataset);
            System.out.print("New Dataset was persisted successfully!");
            return;
        }
        System.out.println("Dataset already exists!");
    }

    public Dataset read(int primaryKey){
        Dataset wantedDataset = null;
        for(int i = 0; i < DBContent.size(); i++){
            if(DBContent.get(i).primaryKey == primaryKey){
                wantedDataset = DBContent.get(i);
            }
        }
        return wantedDataset;
    }

    public void update(){

    }

    public void delete(int primaryKey){
        if(read(primaryKey) == null){
            System.out.println("Dataset does not exist!");
            return;
        }
        DBContent.remove(read(primaryKey));
        System.out.println("Dataset was removed successfully!");
    }
}


