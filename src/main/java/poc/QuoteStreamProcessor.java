package poc;

import  com.dataart.poc.Messages.SIQuote;

import com.google.protobuf.Timestamp;
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

    SIQuote processQuote(SIQuote quote) {
        SIQuote.Builder outQuote = SIQuote.newBuilder();

        //business logic here

        //valid values +   direct copy
        if (quote.getCurrency() == "GBP") {
            outQuote.setCurrency("AUD");
        }
        if (quote.getNotionalCurrency() == "USD"){
            outQuote.setNotionalCurrency("CNY");
        }
        if (quote.getCountryOfIssue() == "UK"){
            outQuote.setCountryOfIssue("JP");
        }

        //range checks + conversions
        if (quote.getBidPrice() > 200F && quote.getBidPrice() < 300.33F ){
            outQuote.setBidPrice(quote.getBidPrice() + 123.45F);
        }

        if (quote.getOfferPrice() > 100F && quote.getOfferPrice() < 400.33F ){
            outQuote.setOfferPrice(quote.getOfferPrice() + 456.78F);
        }

        if (quote.getBidNotionalAmount() > 50 && quote.getBidNotionalAmount() < 100){
            outQuote.setBidNotionalAmount(quote.getBidNotionalAmount() * 3);
        }


        //null checks + conversions
        if (quote.getTargetAPA() != null){
            outQuote.setTargetAPA("ECEU");
        }

        if (outQuote.getQuotePackageId() != null){
            outQuote.setQuotePackagePrice(987.65F);
        }

        //direct copy
        outQuote.setQuoteRequestId(quote.getQuoteRequestId());
        outQuote.setSecurityId(quote.getSecurityId());
        outQuote.setPriceType(quote.getPriceType());
        outQuote.setQuotePackagePriceType(quote.getQuotePackagePriceType());
        outQuote.setQuoteResponseLevel(quote.getQuoteResponseLevel());
        outQuote.setQuotePackagePriceType(quote.getQuotePackagePriceType());



        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();

        outQuote.setTransactionTime(timestamp);
        outQuote.setValidUntilTime(timestamp);


        return  outQuote.build();

    }



    public void run(){

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "quotes-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "1000");

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, byte[]> source = builder.stream("streams-plaintext-input");

         source.mapValues(value -> {
            try{
                com.dataart.poc.Messages.SIQuote inQuote = com.dataart.poc.Messages.SIQuote.parseFrom(value);
                System.out.printf("[PROCESS-IN] value = %s, asString = %s%n", Arrays.toString(value), inQuote.toString());

                SIQuote outQuote = processQuote(inQuote);

                System.out.printf("[PROCESS-OUT] value = %s, asString = %s%n", Arrays.toString(outQuote.toByteArray()), outQuote.toString());

                return  outQuote.toByteArray();
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
