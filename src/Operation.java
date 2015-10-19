/**
 * Created by jenny on 10/17/15.
 */
public class Operation {

    public enum Type {
        read, write
    }

    final Type type;
    final long address;
    final long length;

    public Operation(long address, long length, Type type){
        this.address = address;
        this.length = length;
        this.type = type;
    }

    @Override
    public String toString(){
        return "0x" + Long.toHexString(address);
    }
}
