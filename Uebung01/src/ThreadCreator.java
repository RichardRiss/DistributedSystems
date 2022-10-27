
public class ThreadCreator implements Runnable{
    private Counter counter;
    private int num;

    public ThreadCreator(Counter counter, int num) {
        this.counter = counter;
        this.num = num;
    }

    public void run(){
        try {
            System.out.println("Thread " + this.num + " running.");
            Counter counter = new Counter();
            for (int i=0;i<this.num;i++) {
                this.counter.increment();
                System.out.println("Thread " + this.num + " incremented Counter.");
            }
            System.out.println("Thread " + this.num + " new Counter value is: " + this.counter.value());

        } catch (Exception e) {
            System.out.println("Thread " + this.num + " interference. Exception " + e.getClass());
        }
    }

}
