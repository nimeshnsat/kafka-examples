package poc;

import  com.dataart.poc.Messages.SIQuote;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class MyConsumer {
    public static void main(String[] args) throws Exception {

        Properties props2 = new Properties();
        props2.setProperty("bootstrap.servers", "localhost:9092");
        props2.setProperty("auto.offset.reset", "earliest");
        props2.setProperty("group.id", "nimesh123");
        props2.setProperty("enable.auto.commit", "false");
        props2.setProperty("auto.commit.interval.ms", "1000");
        props2.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props2.setProperty("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props2);
        consumer.subscribe(Arrays.asList("streams-plaintext-output"));
        while (true) {
            //System.out.printf("Here\n");
            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, byte[]> record : records){

                try{
                    SIQuote quote = SIQuote.parseFrom(record.value());
                    System.out.printf("offset = %d, key = %s, value = %s, asString = %s%n", record.offset(),
                            record.key(),Arrays.toString(record.value()), quote.toString());
                }
                catch (Throwable e){
                    continue;
                }



            }

        }

    }
}
