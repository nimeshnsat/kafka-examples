//protoc --java_out=target\generated-sources\protobuf src\main\Protobuf\Messages.proto

package poc;

import  com.dataart.poc.Messages.SIQuote;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class MyProducer {

    public static SIQuote generateSIQuote() throws Exception {
        SIQuote.Builder quote = SIQuote.newBuilder();
        quote.setSecurityId("ABC");
        quote.setQuotePackagePriceType(SIQuote.EnumQuotePackagePriceType.QPPT_BASIS_POINTS);
        return quote.build();
    }


    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        Producer<String, byte[]> producer = new KafkaProducer<>(props);
//        for (int i = 0; i < 100; i++)
//            producer.send(new ProducerRecord<String, Byte[]>("streams-plaintext-input", Integer.toString(i), Integer.toString(i)));

        SIQuote quote = generateSIQuote();
        producer.send(new ProducerRecord<String, byte[]>("streams-plaintext-input", quote.getSecurityId(), quote.toByteArray()));

        producer.close();

    }
}
