import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

public class Target implements Serializable, Map, Comparator {

    public static boolean FLAG = false;

    public static BigInteger compareInt1 = new BigInteger("1");
    public static BigInteger compareInt2 = new BigInteger("2");


    @Override
    public String toString() {
        Target.FLAG = true;
        return super.toString();
    }

    @Override
    public int hashCode() {
        Target.FLAG = true;
        return super.hashCode();
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (compareInt1.equals(o1)) {
            Target.FLAG = true;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        Target.FLAG = true;
        return super.equals(obj);
    }

    @Override
    public Object get(Object key) {
        Target.FLAG = true;
        return null;
    }



    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }



    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set keySet() {
        return Collections.emptySet();
    }

    @Override
    public Collection values() {
        return Collections.emptyList();
    }

    @Override
    public Set<Entry> entrySet() {
        return Collections.emptySet();
    }
}
