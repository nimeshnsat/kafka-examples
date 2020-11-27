package poc;

import  com.dataart.poc.Messages.SIQuote;

import com.google.protobuf.Timestamp;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class QuotesProducer implements Runnable {

    static String getSecurityId()
    {
        int number = randomSecurityId.nextInt(100000);
        String formatted = String.format("%012d", number);
        return  formatted;
    }

    public static SIQuote generateSIQuote(){

        long millis = System.currentTimeMillis();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)
                .setNanos((int) ((millis % 1000) * 1000000)).build();

        SIQuote.Builder quote = SIQuote.newBuilder();

        quote.setQuoteId(UUID.randomUUID().toString());

        quote.setQuoteRequestId("KgdQpZiPeyDi");
        quote.setTransactionTime(timestamp);
        quote.setQuoteResponseLevel(SIQuote.EnumQuoteResponseLevel.ACK_EACH_QUOTE);
        quote.setQuotePublishMode(SIQuote.EnumQuotePublishMode.ALWAYS_WITH_TE_CALC_WAIVERS);
        quote.setTargetAPA("ECHO");
        quote.setSecurityId(getSecurityId());
        quote.setSecurityIdSource("4");
        quote.setCountryOfIssue("UK");
        quote.setValidUntilTime(timestamp);
        quote.setPriceType(SIQuote.EnumPriceType.PT_BASIS_POINTS);
        quote.setCurrency("GBP");
        quote.setBidPrice(222.22F);
        quote.setOfferPrice(333.33F);
        quote.setBidSize(44);
        quote.setOfferSize(55);
        quote.setQuoteTier(2);
        quote.setBidNotionalAmount(66);
        quote.setOfferNotionalAmount(77);
        quote.setNotionalCurrency("USD");
        quote.setUnderlyingSecurityId("BASEOFALL");
        quote.setQuotePackageId("RaMkq8NPZbv8");
        quote.setQuotePackagePrice(88.88F);
        quote.setQuotePackagePriceType(SIQuote.EnumQuotePackagePriceType.QPPT_PER_UNIT);
        quote.setQuotePackageCurrency("EUR");

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

    static Random randomSecurityId = new Random();

}
