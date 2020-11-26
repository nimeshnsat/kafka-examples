package poc;

import com.dataart.poc.Messages;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Arrays;
import java.util.Properties;

public class OutputStreamConsumer implements Runnable {

    public void run(){

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "output-consumer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "1000");

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, byte[]> source = builder.stream("streams-plaintext-output");

        source.foreach((key, value) -> {

            try {
                Messages.SIQuote quote = Messages.SIQuote.parseFrom(value);
                System.out.printf("[OUT] key = %s, value = %s, asString = %s%n",
                        key,Arrays.toString(value), quote.toString());
            }catch (Throwable e){

            }

        });


        final Topology topology = builder.build();
        System.out.println(topology.describe());
        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }
}