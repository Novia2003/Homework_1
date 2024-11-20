package benchmarks.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
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
import utils.RabbitMQUtils;

import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2g", "-Xmx2g"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SimpleRabbitMQBenchmark {

    private static final String QUEUE_NAME = "queue1";
    private static final String HOST = "localhost";

    private Channel producer;
    private Channel consumer;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        Connection connection = RabbitMQUtils.createConnection(HOST);
        producer = RabbitMQUtils.createProducer(connection, QUEUE_NAME);
        consumer = RabbitMQUtils.createConsumer(connection, QUEUE_NAME);
    }

    @Benchmark
    public void sendMessage() throws Exception {
        String message = "RabbitMQ - top!";
        producer.basicPublish("", QUEUE_NAME, null, message.getBytes());
    }

    @Benchmark
    public void consumeMessage() throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        };
        consumer.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }

    @TearDown(Level.Trial)
    public void tearDown() throws Exception {
        producer.close();
        consumer.close();
    }
}

