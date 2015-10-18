/**
 * Created by jenny on 10/17/15.
 */
public class Operation {

    public enum Type {
        read, write
    }

    final Type type;
    final int address;
    final long length;

    public Operation(int address, long length, Type type){
        this.address = address;
        this.length = length;
        this.type = type;
    }

    @Override
    public String toString(){
        return "0x" + Integer.toHexString(address);
    }
}
