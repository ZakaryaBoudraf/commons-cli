package org.apache.commons.cli;

public class ManualBenchmark {
    public static void main(String[] args) throws Exception {
        System.out.println(">>> STARTING MANUAL BENCHMARK <<<");

        // Setup
        DefaultParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");
        options.addOption("A", "almost-all", false, "do not list implied . and ..");
        String[] arguments = new String[]{"-a", "--almost-all", "file.txt"};

        // 1. Warmup (Run 50,000 times to warm up the JVM)
        System.out.println("Warming up (50,000 iterations)...");
        for (int i = 0; i < 50000; i++) {
            try {
                parser.parse(options, arguments);
            } catch (Exception e) {}
        }

        // 2. Measurement
        int iterations = 100000;
        System.out.println("Measuring (" + iterations + " iterations)...");
        
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try {
                parser.parse(options, arguments);
            } catch (Exception e) {}
        }
        long end = System.nanoTime();

        // 3. Results
        double totalTimeMs = (end - start) / 1_000_000.0;
        double throughput = iterations / totalTimeMs; // ops per ms

        System.out.println("---------------------------------------------------");
        System.out.println("Total Time: " + String.format("%.2f", totalTimeMs) + " ms");
        System.out.println("Throughput: " + String.format("%.2f", throughput) + " ops/ms");
        System.out.println("---------------------------------------------------");
    }
}