/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class ParserBenchmark {

    private DefaultParser parser;
    private Options options; 
    private String[] arguments;

    @Setup(Level.Trial)
    public void setup() {
        parser = new DefaultParser();
        options = new Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");
        options.addOption("A", "almost-all", false, "do not list implied . and ..");
        options.addOption("l", "long", false, "use a long listing format");
        options.addOption("h", "help", false, "display this help and exit");
        arguments = new String[]{"-a", "--almost-all", "-l", "file.txt"};
    }

    @Benchmark
    public CommandLine benchmarkDefaultParser() throws ParseException {
        return parser.parse(options, arguments);
    }

    @Benchmark
    public CommandLine benchmarkSimpleArgs() throws ParseException {
        return parser.parse(options, new String[]{"-a", "-l"});
    }

    @Benchmark
    public CommandLine benchmarkComplexArgs() throws ParseException {
        return parser.parse(options, new String[]{"-a", "--almost-all", "-l", "--help", "file1.txt", "file2.txt"});
    }
}