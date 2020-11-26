package poc;

import  com.dataart.poc.Messages.SIQuote;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class QuoteStreamProcessor implements Runnable {
    public void run(){

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "quotes-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, byte[]> source = builder.stream("streams-plaintext-input");

         source.mapValues(value -> {
            try{
                com.dataart.poc.Messages.SIQuote inQuote = com.dataart.poc.Messages.SIQuote.parseFrom(value);
                System.out.printf("[PROCESS-IN] value = %s, asString = %s%n", Arrays.toString(value), inQuote.toString());
                com.dataart.poc.Messages.SIQuote.Builder outQuote = com.dataart.poc.Messages.SIQuote.newBuilder(inQuote);

                //business logic here
                outQuote.setBidPrice(100.52F);

                System.out.printf("[PROCESS-OUT] value = %s, asString = %s%n", Arrays.toString(outQuote.build().toByteArray()), outQuote.toString());

                return  outQuote.build().toByteArray();
            }
            catch (Throwable e){
                return  value;
            }

        }).to("streams-plaintext-output");

        final Topology topology = builder.build();
        System.out.println(topology.describe());
        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }
}
