import java.io.Serializable;

public class DBResult implements Serializable {
    private String record;
    private int index;
    private int size;


    public String getRecord() {
        return this.record;
    }
     public void setRecord(String record) {
        this.record = record;
     }

     public int getIndex() {
        return this.index;
     }

     public void setIndex(int index) {
        this.index = index;
     }

     public int getSize() {
        return this.size;
     }

     public void setSize(int size) {
        this.size = size;
     }



}
