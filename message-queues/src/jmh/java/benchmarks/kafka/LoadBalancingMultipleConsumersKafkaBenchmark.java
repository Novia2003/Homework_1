package benchmarks.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import utils.KafkaUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2g", "-Xmx2g"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LoadBalancingMultipleConsumersKafkaBenchmark {

    private static final String TOPIC = "topic1";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "benchmark-group";

    private List<KafkaProducer<String, String>> producers;
    private List<KafkaConsumer<String, String>> consumers;

    @Setup(Level.Trial)
    public void setup() {
        producers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            producers.add(KafkaUtils.createProducer(BOOTSTRAP_SERVERS));
        }

        consumers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            consumers.add(KafkaUtils.createConsumer(BOOTSTRAP_SERVERS, GROUP_ID, TOPIC));
        }
    }

    @Benchmark
    public void sendMessage() {
        for (KafkaProducer<String, String> producer : producers) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "key", "Kafka - top!");
            producer.send(record);
        }
    }

    @Benchmark
    public void consumeMessage() {
        for (KafkaConsumer<String, String> consumer : consumers) {
            consumer.poll(Duration.ofMillis(100));
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        for (KafkaProducer<String, String> producer : producers) {
            producer.close();
        }

        for (KafkaConsumer<String, String> consumer : consumers) {
            consumer.close();
        }
    }
}

