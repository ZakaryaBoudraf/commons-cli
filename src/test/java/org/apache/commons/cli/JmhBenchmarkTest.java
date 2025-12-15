package org.apache.commons.cli;

import org.junit.jupiter.api.Test; // Updated for JUnit 5
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class JmhBenchmarkTest {

    private DefaultParser parser;
    private org.apache.commons.cli.Options options; 
    private String[] arguments;

    @Setup(Level.Trial)
    public void setup() {
        parser = new DefaultParser();
        options = new org.apache.commons.cli.Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");
        options.addOption("A", "almost-all", false, "do not list implied . and ..");
        arguments = new String[]{"-a", "--almost-all", "file.txt"};
    }

    @Benchmark
    public CommandLine testParse() throws ParseException {
        return parser.parse(options, arguments);
    }

    @Test
    public void runJmhBenchmark() throws Exception {
        org.openjdk.jmh.runner.options.Options opt = new OptionsBuilder()
                .include(JmhBenchmarkTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}