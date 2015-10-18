/**
 * Created by jenny on 10/17/15.
 */
public class Operation {

    public enum Type {
        read, write
    }

    Type type;
    int address;
    long length;

    @Override
    public String toString(){
        return Integer.toHexString(address);
    }
}
