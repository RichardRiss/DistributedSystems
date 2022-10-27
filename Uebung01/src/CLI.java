import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CLI {
    private int trnum;

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.create(args);
    }

    public void create(String[] args) {
        //print available CLI
        System.out.println("Hello World");
        System.out.println("Available CLI Arguments:");
        System.out.println("-trnum <int>    number of parallel threads to create");

        //parse available command-value combinations
        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0; i < args.length - 1; i += 2) {
            dict.put(args[i], args[i + 1]);
        }

        //get number of Threads to start
        this.trnum = 0;
        if (dict.get("-trnum") != null) {
            this.trnum = Integer.parseInt(dict.get("-trnum"));
        }

        //Start Threads and add to Arraylist
        List<Thread> listthreads = new ArrayList<>();
        Counter counter = new Counter();
        for (int j = 1; j <= this.trnum; j++) {

            //Create Threads, alternate incrementing and decrementing
            Thread tr = new Thread(String.valueOf(j)) {
                @Override
                public void run() {
                    if (Integer.parseInt(this.getName()) % 2 != 0) {
                        for (int i = 0; i <10; i++) {
                            counter.increment();
                            System.out.println("Thread " + this.getName() + " incremented Counter. New value is: " + counter.value());
                        }
                    }
                    else {
                        for (int i = 0; i <10; i++) {
                            counter.decrement();
                            System.out.println("Thread " + this.getName() + " decremented Counter. New value is: " + counter.value());
                        }
                    }
                }
            };
            listthreads.add(tr);
        }

        //Start all the created Threads
        for (Thread tr: listthreads) {
            tr.start();
            System.out.println("Thread " + tr.getName() + " started.");
        }

        // Wait for all Threads to finish
        //for (Thread tr : listthreads) {
            //try {
                //tr.join();
            //} catch (InterruptedException e) {
                //System.out.println("Thread  got interrupted." + e.getClass());
            //}
        //}
        //Print final value in counter
        //System.out.println("Final Value is: " + counter.value());
    }

}
