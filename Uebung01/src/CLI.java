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
        Map<String, String> dict = new HashMap<>();
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
                    for (int i = 0; i <1000; i++) {
                        counter.increment();
                        counter.decrement();
                    }
                    System.out.println("Thread " + this.getName() + ":  New value is: " + counter.value());
                }
            };
            tr.start();
            listthreads.add(tr);
        }


        // Wait for all Threads to finish
        for (Thread tr : listthreads) {
            try {
                tr.join();
            } catch (InterruptedException e) {
                System.out.println("Thread  got interrupted." + e.getClass());
            }
        }
        //Print final value in counter
        System.out.println("Final Value is: " + counter.value());
    }

}
