package poc;


import java.util.concurrent.CountDownLatch;

public class QuotesStreamingSimulator {

    public static void main(String[] args) throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                latch.countDown();
            }
        });


        QuoteStreamProcessor processor1 = new QuoteStreamProcessor();
        QuotesProducer producer1 = new QuotesProducer();
        OutputStreamConsumer consumer1 = new OutputStreamConsumer();

        try {
            processor1.run();
            consumer1.run();
            producer1.run();

            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }

        System.exit(0);
    }



}
