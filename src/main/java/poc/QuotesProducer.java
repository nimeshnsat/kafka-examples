package poc;

import  com.dataart.poc.Messages.SIQuote;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class QuotesProducer implements Runnable {

    public static SIQuote generateSIQuote(){
        SIQuote.Builder quote = SIQuote.newBuilder();
        quote.setSecurityId("ABC");
        quote.setQuotePackagePriceType(SIQuote.EnumQuotePackagePriceType.QPPT_BASIS_POINTS);
        return quote.build();
    }


    public void run(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        Producer<String, byte[]> producer = new KafkaProducer<>(props);

        while (true){

            SIQuote quote = generateSIQuote();
            producer.send(new ProducerRecord<String, byte[]>("streams-plaintext-input", quote.getSecurityId(), quote.toByteArray()));
            System.out.printf("[INPUT] value = %s, asString = %s%n", Arrays.toString(quote.toByteArray()), quote.toString());
            try {
                TimeUnit.SECONDS.sleep(1);
            }catch(Throwable e) {
                System.exit(1);
            }
         }

        //producer.close();
    }

}
