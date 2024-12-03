package benchmarks.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.openjdk.jmh.annotations.*;
import utils.RabbitMQUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2g", "-Xmx2g"})
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LoadBalancingRabbitMQBenchmark {

    private static final String QUEUE_NAME = "queue1";
    private static final String HOST = "localhost";

    private List<Channel> producers;
    private Channel consumer;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        Connection connection = RabbitMQUtils.createConnection(HOST);

        producers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            producers.add(RabbitMQUtils.createProducer(connection, QUEUE_NAME));
        }

        consumer = RabbitMQUtils.createConsumer(connection, QUEUE_NAME);
    }

    @Benchmark
    public void sendMessage() throws Exception {
        String message = "RabbitMQ - top!";

        for (Channel producer : producers) {
            producer.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }
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
        for (Channel producer : producers) {
            producer.close();
        }

        consumer.close();
    }
}

