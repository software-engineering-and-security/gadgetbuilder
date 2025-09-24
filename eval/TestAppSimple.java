import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class TestAppSimple {

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: java Main <file>");
            System.exit(1);
        }

        FileInputStream in = new FileInputStream(args[0]);
        ObjectInputStream ois = new ObjectInputStream(in);
        ois.readObject();
    }

}
