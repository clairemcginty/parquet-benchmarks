# Parquet-Bench

Configuration benchmarks for Parquet reads and writes.

Docs:

- [Parquet Read and Write Configuration Options](https://github.com/apache/parquet-mr/blob/master/parquet-hadoop/README.md)
- [Parquet Column Encodings](https://github.com/apache/parquet-format/blob/master/Encodings.md)

## Run instructions

First, clone parquet-mr and run `mvn install` locally (this build runs on Parquet 0.14.0-SNAPSHOT). (If you don't want to build Parquet locally, you can use stable release version 0.13.1, but note that adapative bloom filters aren't supported in this version.)

Then, write out test data:

```shell
% sbt "runMain data.WriteBenchmark"
```

Benchmark results will be written as a formatted Markdown table to `write_results.md`.

Finally, run the read benchmarks:

```shell
$ sbt "Jmh/run -i 10 -wi 5 -f1 -t1"
```
Benchmark results will be printed to STDOUT and written as a formatted Markdown table to read_results.md.

## Results

See [write_results.md](write_results.md) and [read_results.md](read_results.md).
