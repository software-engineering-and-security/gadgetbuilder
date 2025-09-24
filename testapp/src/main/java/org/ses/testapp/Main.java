package org.ses.testapp;


import org.ses.testapp.filters.*;

import java.io.FileInputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.util.concurrent.*;

public class Main {

    public static final FilterProvider[] AvailableFilters = {
            new NotSoSerialFilter(), new MogwaiLabsFilter(), new SerialKillerFilter(),
            new ApacheDubboFilter(), new ApacheIgniteFilter(), new ApacheFuryFilter(), new ApacheKafkaFilter()
    };

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: java Main <file>");
            System.exit(1);
        }

        String fileName = args[0];

        if (args.length == 2) {
            for (FilterProvider filter : AvailableFilters) {
                System.out.print(filter.getClass().getSimpleName() + ": ");
                deserializeFromFile(fileName, filter.getFilter());
            }
        } else {
            deserializeFromFile(fileName);
        }


    }

    public static void deserializeFromFile(String file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(in);
        ois.readObject();
    }


    public static void deserializeFromFile(String file, ObjectInputFilter filter) throws Exception {

        boolean isFiltered = false;
        FileInputStream in = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(in);
        if (filter != null) ois.setObjectInputFilter(filter);

        // Stop execution in case of payloads that open ports and wait for input
        Callable<Boolean> task = () -> {
            try {
                ois.readObject();
            } catch (InvalidClassException e) {
                if (e.getMessage().contains("REJECTED"))
                    return true;
            }
            return false;
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(task);
        try {

            isFiltered = future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
        }
        catch (Exception ignored) { }
        finally {
            executor.shutdown();
        }

        if (isFiltered)
            System.out.println("BLOCKED");
        else
            System.out.println("EXEC");

    }
}
