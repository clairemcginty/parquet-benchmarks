# Write results

## Input file layouts

Test data specifications:

- Each configuration writes 5,000,000 records to a single file
- All file writes use 256MB block size and either 1MB or 10MB page size
- Different configurations use different distributions of column values. For example, one configuration has 1000 unique values for `stringField`/`doubleField`/etc, and another has 1,000,000.
- Dictionary encoding is impacted by # of unique column values (Cardinality). For example, even if dict encoding is enabled, once a certain threshold is passed, Parquet will abandon the attempt to create a dictionary encoding for that column. Lack of dictionary = ballooning file size
- In-memory representation is Parquet-Avro, with the following SpecificRecord class schema:
  ```avsc
      {
        "name": "TestRecord",
        "type": "record",
        "namespace": "testdata",
        "fields": [
            {
                "name": "stringField",
                "type": "string"
            },
            {
                "name": "doubleField",
                "type": "double"
            },
            {
                "name": "intField",
                "type": "int"
            }
        ]
      }
  ```

Notes:
- Parquet may fail to produce a dictionary encoding if column cardinality is too high (either at the page chunk level or at the file level). The "Dict-Encoded Cols" section of this table examines the file metadata after writing is complete, and checks which columns produced a successful dictionary encoding.
- "Normal" distribution values were produced using Java's `Random.nextGaussian()`
- Default Parquet behavior (1MB page size, uncompressed) is highlighted in bold.
- ZSTD (Level 3) uses a baseline compression level of 3 unless otherwise noted

## Baseline

**These tests were run using a 0.14.0-SNAPSHOT build of parquet-mr w/ head commit 9b5a962df3007009a227ef421600197531f970a5, on a 64 GB M1 MBP, using openJDK 17.**

| Cardinality | Dict-Encoded Cols                            | Compression      | Page Size | Dict Page Size | Data Distribution | Sorting      | File Size                  | Write Time   |
|-------------|----------------------------------------------|------------------|-----------|----------------|-------------------|--------------|----------------------------|--------------|
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Shuffled** | **23.47275161743164 MB**   | **25.373 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Shuffled     | 23.382027626037598 MB      | 25.488 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Shuffled     | 23.472515106201172 MB      | 25.369 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Shuffled     | 23.381916046142578 MB      | 25.441 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Sorted**   | **0.28974342346191406 MB** | **27.094 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Sorted       | 0.16922855377197266 MB     | 26.541 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Sorted       | 0.28966617584228516 MB     | 26.148 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Sorted       | 0.16945171356201172 MB     | 26.252 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Shuffled** | **17.970391273498535 MB**  | **24.296 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Shuffled     | 17.964359283447266 MB      | 23.733 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 17.970388412475586 MB      | 23.523 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 17.964329719543457 MB      | 23.528 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Sorted**   | **0.06650638580322266 MB** | **25.088 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Sorted       | 0.05811119079589844 MB     | 24.797 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Sorted       | 0.06650638580322266 MB     | 24.057 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Sorted       | 0.05811119079589844 MB     | 24.088 s     |
| **5000**    | **stringField<br/>doubleField**              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Shuffled** | **38.40328502655029 MB**   | **26.744 s** |
| 5000        | stringField<br/>doubleField                  | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Shuffled     | 30.59129238128662 MB       | 27.411 s     |
| 5000        | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Shuffled     | 38.40816783905029 MB       | 27.195 s     |
| 5000        | stringField<br/>doubleField                  | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Shuffled     | 30.593666076660156 MB      | 28.437 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Sorted**   | **1.202035903930664 MB**   | **26.158 s** |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Sorted       | 0.5516834259033203 MB      | 26.585 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Sorted       | 1.2015323638916016 MB      | 26.142 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Sorted       | 0.5419712066650391 MB      | 1MB          | 26.765 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Shuffled** | **23.41111660003662 MB**   | **25.8 s**   |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Shuffled     | 23.354849815368652 MB      | 25.379 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 23.411113739013672 MB      | 26.569 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 23.35483455657959 MB       | 25.015 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Sorted**   | **0.191162109375 MB**      | **23.623 s** |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Sorted       | 0.1070871353149414 MB      | 24.528 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Sorted       | 0.191162109375 MB          | 24.705 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Sorted       | 0.1070871353149414 MB      | 24.648 s     |
| **50000**   |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Shuffled** | **101.94068050384521 MB**  | 1MB          | **26.321 s** |
| 50000       |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Shuffled     | 45.421515464782715 MB      | 26.242 s     |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Shuffled     | 101.93698406219482 MB      | 25.356 s     |
| 50000       |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Shuffled     | 45.41885185241699 MB       | 25.461 s     |
| **50000**   | **stringField<br/>doubleField**              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Sorted**   | **81.1013126373291 MB**    | 1MB          | **27.107 s** |
| 50000       | stringField<br/>doubleField                  | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Sorted       | 1.997756004333496 MB       | 28.522 s     |
| 50000       | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Sorted       | 81.10037326812744 MB       | 27.236 s     |
| 50000       | stringField<br/>doubleField                  | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Sorted       | 2.002534866333008 MB       | 28.315 s     |
| **50000**   |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Shuffled** | **99.11835289001465 MB**   | **25.281 s** |
| 50000       |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Shuffled     | 40.00043487548828 MB       | 25.323 s     |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 99.1183500289917 MB        | 25.778 s     |
| 50000       |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 39.99959468841553 MB       | 25.06 s      |
| **50000**   | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Sorted**   | **1.6037378311157227 MB**  | **24.106 s** |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Sorted       | 0.5769920349121094 MB      | 24.056 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Sorted       | 1.6037378311157227 MB      | 24.517 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Sorted       | 0.5769920349121094 MB      | 24.15 s      |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Shuffled** | **103.65634059906006 MB**  | **25.649 s** |
| 100000      |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Shuffled     | 47.97103977203369 MB       | 26.517 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Shuffled     | 103.65502643585205 MB      | 24.773 s     |
| 100000      |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Shuffled     | 47.968990325927734 MB      | 25.357 s     |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Sorted**   | **103.658203125 MB**       | **27.779 s** |
| 100000      |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Sorted       | 2.636960029602051 MB       | 1MB          | 27.792 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Sorted       | 103.6509370803833 MB       | 27.257 s     |
| 100000      |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Sorted       | 2.638911247253418 MB       | 26.969 s     |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Shuffled** | **99.64859104156494 MB**   | **24.683 s** |
| 100000      |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Shuffled     | 41.86725425720215 MB       | 25.089 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 99.64855861663818 MB       | 24.675 s     |
| 100000      |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 41.866536140441895 MB      | 24.935 s     |
| **100000**  | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Sorted**   | **2.991497039794922 MB**   | **24.603 s** |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Sorted       | 1.015192985534668 MB       | 24.606 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Sorted       | 2.991497039794922 MB       | 24.417 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Sorted       | 1.015192985534668 MB       | 24.378 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Shuffled** | **108.42271995544434 MB**  | **25.71 s**  |
| 1000000     |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Shuffled     | 54.06161403656006 MB       | 25.54 s      |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Shuffled     | 108.42219161987305 MB      | 25.519 s     |
| 1000000     |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Shuffled     | 54.059226989746094 MB      | 26.409 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **Normal**        | **Sorted**   | **108.42190837860107 MB**  | **27.612 s** |
| 1000000     |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | Normal            | Sorted       | 14.103327751159668 MB      | 28.665 s     |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | Normal            | Sorted       | 108.4225664138794 MB       | 28.504 s     |
| 1000000     |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | Normal            | Sorted       | 14.101709365844727 MB      | 28.882 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Shuffled** | **104.41773414611816 MB**  | **25.022 s** |
| 1000000     |                                              | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Shuffled     | 49.24773025512695 MB       | 25.118 s     |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 104.41772556304932 MB      | 24.874 s     |
| 1000000     |                                              | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Shuffled     | 49.246949195861816 MB      | 25.058 s     |
| **1000000** | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | 1MB            | **RoundRobin**    | **Sorted**   | **97.6240291595459 MB**    | **23.763 s** |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 1 MB      | 1MB            | RoundRobin        | Sorted       | 6.713757514953613 MB       | 23.828 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1MB            | RoundRobin        | Sorted       | 97.6240291595459 MB        | 23.854 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD (Level 3)   | 10 MB     | 1MB            | RoundRobin        | Sorted       | 6.713757514953613 MB       | 24.185 s     |

##  Max Dictionary Compression Ratio Option

Test of a "max compression ratio" for dictionary encoding, which will produce a dictionary as long as the ratio of encoded data to raw data is below a certain threshold. (Basically, this is a ratio of repeated values : total values.)
You can see that with higher ratios, Parquet is more likely to produce dict-encoded columns, even for shuffled/high cardinality data.

Code reference: https://github.com/apache/parquet-mr/compare/master...clairemcginty:parquet-mr:test-compression

### parquet.max.dictionary.compression.ratio = 0.5

| Cardinality | Dict-Encoded Cols                        | Compression    | Page Size | Dict Page Size | Data Distribution | Sorting  | Extra Props                                    | File Size              | Write Time |
|-------------|------------------------------------------|----------------|-----------|----------------|-------------------|----------|------------------------------------------------|------------------------|------------|
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.472307205200195 MB  | 24.375 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.382023811340332 MB  | 24.723 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.472643852233887 MB  | 24.561 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381739616394043 MB  | 24.902 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.28960514068603516 MB | 25.869 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1696491241455078 MB  | 25.894 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.29080677032470703 MB | 25.767 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.17020606994628906 MB | 24.922 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970399856567383 MB  | 23.945 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.96436309814453 MB   | 24.16 s    |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970396995544434 MB  | 23.108 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964364051818848 MB  | 23.658 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 24.696 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 24.224 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 22.532 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 23.076 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.83526611328125 MB   | 25.887 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.706565856933594 MB  | 26.026 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.821514129638672 MB  | 25.997 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.705097198486328 MB  | 25.966 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.200881004333496 MB   | 25.263 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.548431396484375 MB   | 25.41 s    |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.2044181823730469 MB  | 25.188 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5492038726806641 MB  | 25.449 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.411110877990723 MB  | 24.09 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354830741882324 MB  | 24.291 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.411136627197266 MB  | 24.34 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.35489559173584 MB   | 23.931 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 22.828 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 23.041 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 23.129 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 22.724 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 98.45908832550049 MB   | 24.959 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.02756977081299 MB   | 25.749 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.063392639160156 MB  | 27.557 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 35.76667594909668 MB   | 27.531 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 64.57563591003418 MB   | 26.453 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.0139389038085938 MB  | 27.047 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.478863716125488 MB  | 26.418 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 4.118277549743652 MB   | 27.328 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.65815544128418 MB   | 26.078 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.070013999938965 MB  | 26.238 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.658205032348633 MB  | 25.99 s    |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.070083618164062 MB  | 26.885 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 23.058 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 23.035 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 23.393 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 23.21 s    |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 104.0337963104248 MB   | 25.255 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.77231311798096 MB   | 25.378 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 44.93536853790283 MB   | 28.547 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.048739433288574 MB  | 28.546 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 94.10730361938477 MB   | 26.6 s     |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.073780059814453 MB   | 27.244 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 20.546600341796875 MB  | 27.601 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.069963455200195 MB  | 27.19 s    |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.41953945159912 MB   | 27.353 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.26212215423584 MB   | 27.434 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.41954040527344 MB   | 26.773 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.26207733154297 MB   | 27.709 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 23.665 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.1 s     |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 23.799 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 23.922 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 109.28802585601807 MB  | 24.502 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.92999076843262 MB   | 25.61 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 114.85413265228271 MB  | 27.814 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 63.501312255859375 MB  | 28.926 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 109.1669454574585 MB   | 27.202 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.166365623474121 MB  | 27.559 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 107.40107250213623 MB  | 29.69 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 33.083008766174316 MB  | 30.325 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 105.22979164123535 MB  | 23.647 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.123799324035645 MB  | 24.073 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 56.29562568664551 MB   | 29.322 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.273104667663574 MB  | 29.405 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 97.6240291595459 MB    | 23.082 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 23.308 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 54.85803699493408 MB   | 25.343 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 23.03006362915039 MB   | 25.269 s   |


### parquet.max.dictionary.compression.ratio = 0.25

| Cardinality | Dict-Encoded Cols                        | Compression    | Page Size | Dict Page Size | Data Distribution | Sorting  | Extra Props                                     | File Size              | Write Time |
|-------------|------------------------------------------|----------------|-----------|----------------|-------------------|----------|-------------------------------------------------|------------------------|------------|
| 1000        | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 34.75708866119385 MB   | 23.085 s   |
| 1000        | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 25.82835102081299 MB   | 23.596 s   |
| 1000        | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 34.75664043426514 MB   | 23.793 s   |
| 1000        | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 25.83096408843994 MB   | 23.088 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.29090118408203125 MB | 25.436 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.17031192779541016 MB | 25.697 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.2896614074707031 MB  | 24.671 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.1700916290283203 MB  | 24.364 s   |
| 1000        | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 31.069785118103027 MB  | 22.446 s   |
| 1000        | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 20.26810073852539 MB   | 22.844 s   |
| 1000        | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 31.06977939605713 MB   | 21.921 s   |
| 1000        | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 20.269012451171875 MB  | 22.005 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.06650638580322266 MB | 23.218 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.05811119079589844 MB | 23.982 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.06650638580322266 MB | 22.346 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.05811119079589844 MB | 22.378 s   |
| 5000        | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 38.40274715423584 MB   | 26.074 s   |
| 5000        | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 30.59953022003174 MB   | 25.025 s   |
| 5000        | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 38.40358352661133 MB   | 24.56 s    |
| 5000        | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 30.587037086486816 MB  | 24.885 s   |
| 5000        | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 19.990897178649902 MB  | 24.645 s   |
| 5000        | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.42946720123291016 MB | 25.442 s   |
| 5000        | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 19.991039276123047 MB  | 24.133 s   |
| 5000        | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.42351722717285156 MB | 24.598 s   |
| 5000        | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 34.70712471008301 MB   | 23.238 s   |
| 5000        | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 25.383031845092773 MB  | 23.296 s   |
| 5000        | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 34.70711135864258 MB   | 22.96 s    |
| 5000        | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 25.38126277923584 MB   | 23.896 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.191162109375 MB      | 22.428 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.1070871353149414 MB  | 22.549 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.191162109375 MB      | 22.106 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.1070871353149414 MB  | 22.367 s   |
| 50000       | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 101.53642749786377 MB  | 23.958 s   |
| 50000       | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 45.71830368041992 MB   | 24.205 s   |
| 50000       | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 46.01221561431885 MB   | 25.933 s   |
| 50000       | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 37.53763675689697 MB   | 25.796 s   |
| 50000       | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 81.09554672241211 MB   | 25.574 s   |
| 50000       | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 1.9850893020629883 MB  | 26.321 s   |
| 50000       | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 26.970005989074707 MB  | 26.597 s   |
| 50000       | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 2.9478273391723633 MB  | 26.083 s   |
| 50000       | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 38.999199867248535 MB  | 24.804 s   |
| 50000       | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 31.200769424438477 MB  | 24.822 s   |
| 50000       | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 38.99918270111084 MB   | 25.434 s   |
| 50000       | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 31.20188617706299 MB   | 25.315 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 1.6037378311157227 MB  | 22.569 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.5769920349121094 MB  | 22.543 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 1.6037378311157227 MB  | 23.019 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 0.5769920349121094 MB  | 23.032 s   |
| 100000      | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 103.80619430541992 MB  | 23.852 s   |
| 100000      | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 48.36116409301758 MB   | 24.157 s   |
| 100000      | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 50.720895767211914 MB  | 27.019 s   |
| 100000      | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 40.59225273132324 MB   | 27.145 s   |
| 100000      | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 101.07352256774902 MB  | 25.111 s   |
| 100000      | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 3.6728391647338867 MB  | 27.294 s   |
| 100000      | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 34.47360324859619 MB   | 26.26 s    |
| 100000      | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 7.01685905456543 MB    | 26.416 s   |
| 100000      | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 40.98328971862793 MB   | 25.19 s    |
| 100000      | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 33.58849239349365 MB   | 25.3 s     |
| 100000      | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 40.983262062072754 MB  | 24.953 s   |
| 100000      | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 33.58964729309082 MB   | 25.307 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 2.991497039794922 MB   | 22.79 s    |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 1.015192985534668 MB   | 23.056 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 2.991497039794922 MB   | 22.708 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 1.015192985534668 MB   | 22.661 s   |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 108.7888879776001 MB   | 24.091 s   |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 54.438015937805176 MB  | 24.673 s   |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 110.77592277526855 MB  | 25.941 s   |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 58.61396503448486 MB   | 26.745 s   |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 108.7160997390747 MB   | 26.019 s   |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 14.520856857299805 MB  | 26.423 s   |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 103.99642276763916 MB  | 27.942 s   |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 21.037368774414062 MB  | 28.885 s   |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 104.76640892028809 MB  | 23.074 s   |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 49.64574337005615 MB   | 23.84 s    |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 59.761956214904785 MB  | 27.55 s    |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.25 | 45.50830364227295 MB   | 27.58 s    |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 98.94943332672119 MB   | 21.998 s   |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 4.196954727172852 MB   | 22.558 s   |
| 1000000     | stringField<br/>doubleField              | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 58.80358409881592 MB   | 23.758 s   |
| 1000000     | stringField<br/>doubleField              | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.25 | 15.024721145629883 MB  | 24.026 s   |


### parquet.max.dictionary.compression.ratio = 0.75

| Cardinality | Dict-Encoded Cols                        | Compression    | Page Size | Dict Page Size | Data Distribution | Sorting  | Extra Props                                     | File Size              | Write Time |
|-------------|------------------------------------------|----------------|-----------|----------------|-------------------|----------|-------------------------------------------------|------------------------|------------|
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.47217559814453 MB   | 25.968 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.381805419921875 MB  | 25.943 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.47274112701416 MB   | 25.176 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.382122039794922 MB  | 25.874 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.472723960876465 MB  | 25.781 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.381571769714355 MB  | 26.217 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.2910776138305664 MB  | 26.469 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.1704273223876953 MB  | 26.552 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.2897939682006836 MB  | 26.075 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.1696920394897461 MB  | 25.804 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.2895078659057617 MB  | 25.722 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.16924190521240234 MB | 26.029 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 17.97037124633789 MB   | 24.137 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 17.964348793029785 MB  | 24.261 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 17.970396995544434 MB  | 24.162 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 17.96434211730957 MB   | 23.996 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 17.970402717590332 MB  | 24.475 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 17.964357376098633 MB  | 24.4 s     |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.06650638580322266 MB | 24.072 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.05811119079589844 MB | 24.765 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.06650638580322266 MB | 23.49 s    |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.05811119079589844 MB | 23.772 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.06650638580322266 MB | 23.828 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.05811119079589844 MB | 23.428 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 28.813864707946777 MB  | 27.19 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 27.705208778381348 MB  | 27.294 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 28.820855140686035 MB  | 27.002 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 27.69752311706543 MB   | 27.527 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 28.826900482177734 MB  | 27.746 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 27.706446647644043 MB  | 27.463 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.2002553939819336 MB  | 26.059 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.544011116027832 MB   | 26.16 s    |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.202082633972168 MB   | 26.685 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.5395736694335938 MB  | 26.271 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.2011795043945312 MB  | 26.739 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.5425653457641602 MB  | 26.755 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.411110877990723 MB  | 25.032 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.35486888885498 MB   | 25.541 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.411113739013672 MB  | 27.0 s     |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.354857444763184 MB  | 25.944 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.411133766174316 MB  | 25.236 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 23.354854583740234 MB  | 24.932 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.191162109375 MB      | 23.442 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.1070871353149414 MB  | 23.434 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.191162109375 MB      | 23.515 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.1070871353149414 MB  | 23.818 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.191162109375 MB      | 23.448 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.1070871353149414 MB  | 24.414 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 98.4607162475586 MB    | 25.155 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 45.02469348907471 MB   | 25.818 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 39.04749393463135 MB   | 27.703 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 35.76466369628906 MB   | 27.307 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 39.05743885040283 MB   | 27.535 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 35.76597881317139 MB   | 27.672 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 64.57633590698242 MB   | 27.422 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 3.0148916244506836 MB  | 28.466 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 10.478784561157227 MB  | 28.117 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 4.128458023071289 MB   | 29.16 s    |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 10.473149299621582 MB  | 27.611 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 4.123907089233398 MB   | 27.045 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 29.658185958862305 MB  | 27.464 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 29.070018768310547 MB  | 27.542 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 29.658183097839355 MB  | 28.137 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 29.070018768310547 MB  | 27.794 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 29.658188819885254 MB  | 27.289 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 29.06999111175537 MB   | 26.888 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.6037378311157227 MB  | 24.196 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.5769920349121094 MB  | 24.06 s    |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.6037378311157227 MB  | 23.986 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.5769920349121094 MB  | 24.193 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.6037378311157227 MB  | 24.122 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 0.5769920349121094 MB  | 24.91 s    |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 104.03589057922363 MB  | 24.609 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 48.76966571807861 MB   | 24.936 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 44.94056701660156 MB   | 27.74 s    |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 39.04216289520264 MB   | 27.972 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 44.939799308776855 MB  | 27.747 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 39.05104064941406 MB   | 28.445 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 94.10916519165039 MB   | 26.346 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 5.069474220275879 MB   | 26.567 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 20.544109344482422 MB  | 27.1 s     |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 10.064950942993164 MB  | 26.799 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 20.5399112701416 MB    | 27.026 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 10.051980018615723 MB  | 27.133 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 32.41954517364502 MB   | 26.463 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 31.2620849609375 MB    | 26.971 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 32.41952896118164 MB   | 26.415 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 31.261950492858887 MB  | 27.055 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 32.41957092285156 MB   | 26.434 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 31.262290954589844 MB  | 26.528 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 2.991497039794922 MB   | 23.697 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.015192985534668 MB   | 23.817 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 2.991497039794922 MB   | 23.542 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.015192985534668 MB   | 23.641 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 2.991497039794922 MB   | 23.685 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 1.015192985534668 MB   | 23.78 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 109.28662872314453 MB  | 24.242 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 54.93435096740723 MB   | 24.58 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 114.84295749664307 MB  | 27.236 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 63.488091468811035 MB  | 27.556 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 114.85815715789795 MB  | 27.808 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 63.4901180267334 MB    | 27.759 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 109.1696548461914 MB   | 26.765 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 15.17289924621582 MB   | 26.965 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 107.3967514038086 MB   | 29.596 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 33.07749271392822 MB   | 29.883 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 107.3917760848999 MB   | 29.45 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 33.06851577758789 MB   | 29.905 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 105.23060607910156 MB  | 23.971 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 50.12174892425537 MB   | 24.146 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 56.29564380645752 MB   | 28.272 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 45.270320892333984 MB  | 28.941 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 56.29563903808594 MB   | 27.965 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.75 | 45.27372932434082 MB   | 28.513 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 97.6240291595459 MB    | 23.166 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 6.713757514953613 MB   | 23.106 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 54.85803699493408 MB   | 24.859 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 23.03006362915039 MB   | 24.731 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED   | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 54.85803699493408 MB   | 24.891 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD (Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.75 | 23.03006362915039 MB   | 24.753 s   |

### parquet.max.dictionary.compression.ratio = 0.5, ZSTD (Level 3) compression level = 6

Does higher ZSTD compression ratio => better compression of dictionary pages? Seems like no 

| Cardinality | Dict-Encoded Cols                        | Compression   | Page Size | Dict Page Size | Data Distribution | Sorting  | Extra Props                                    | File Size              | Write Time |
|-------------|------------------------------------------|---------------|-----------|----------------|-------------------|----------|------------------------------------------------|------------------------|------------|
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381778717041016 MB  | 26.254 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381887435913086 MB  | 25.332 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381901741027832 MB  | 25.152 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.16944599151611328 MB | 26.357 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1703042984008789 MB  | 26.355 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.17043590545654297 MB | 26.196 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964383125305176 MB  | 24.48 s    |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964366912841797 MB  | 24.408 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964346885681152 MB  | 23.967 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 24.403 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 24.122 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 23.913 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.700281143188477 MB  | 27.204 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.70945453643799 MB   | 27.173 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.701366424560547 MB  | 27.251 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5421295166015625 MB  | 26.171 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5453395843505859 MB  | 25.627 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5388832092285156 MB  | 26.692 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354819297790527 MB  | 24.698 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.35487461090088 MB   | 24.932 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354875564575195 MB  | 25.853 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 24.374 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 23.608 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 23.5 s     |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.041744232177734 MB  | 26.05 s    |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.02661609649658 MB   | 25.993 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.02631092071533 MB   | 25.766 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.016763687133789 MB   | 27.328 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.012697219848633 MB   | 27.322 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.0194950103759766 MB  | 26.685 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.06998920440674 MB   | 27.37 s    |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.070098876953125 MB  | 27.487 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.069915771484375 MB  | 26.576 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 24.229 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 24.164 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 23.776 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.765987396240234 MB  | 25.857 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.76882839202881 MB   | 25.756 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.768757820129395 MB  | 25.546 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.080471038818359 MB   | 27.221 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.083962440490723 MB   | 27.391 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.070533752441406 MB   | 26.983 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262176513671875 MB  | 27.501 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262167930603027 MB  | 27.779 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262198448181152 MB  | 27.612 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.279 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.209 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.363 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.93239784240723 MB   | 25.411 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.93143653869629 MB   | 25.82 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.931556701660156 MB  | 25.752 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.164961814880371 MB  | 28.126 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.166986465454102 MB  | 27.676 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.167783737182617 MB  | 27.938 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.121888160705566 MB  | 25.204 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.12169647216797 MB   | 24.983 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.122230529785156 MB  | 24.769 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 23.491 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 23.324 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 6) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 23.958 s   |


### parquet.max.dictionary.compression.ratio = 0.5, ZSTD (Level 15) compression level = 15

| Cardinality | Dict-Encoded Cols                        | Compression    | Page Size | Dict Page Size | Data Distribution | Sorting  | Extra Props                                    | File Size              | Write Time |
|-------------|------------------------------------------|----------------|-----------|----------------|-------------------|----------|------------------------------------------------|------------------------|------------|
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381750106811523 MB  | 24.221 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.382123947143555 MB  | 23.593 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381842613220215 MB  | 23.449 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.16998767852783203 MB | 24.871 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.17060565948486328 MB | 25.215 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1696634292602539 MB  | 25.15 s    |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964353561401367 MB  | 22.381 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.96436882019043 MB   | 22.372 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964362144470215 MB  | 22.352 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 23.097 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 22.912 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 22.531 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.71031665802002 MB   | 25.519 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.71908664703369 MB   | 25.335 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.708900451660156 MB  | 25.599 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5401487350463867 MB  | 25.523 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5384740829467773 MB  | 25.427 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5422582626342773 MB  | 25.397 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.35488986968994 MB   | 23.073 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354870796203613 MB  | 23.534 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.3549165725708 MB    | 23.53 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 23.013 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 22.73 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 22.653 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.02747631072998 MB   | 24.694 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.02805423736572 MB   | 24.54 s    |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.02757549285889 MB   | 24.536 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.034059524536133 MB   | 26.124 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.017080307006836 MB   | 26.39 s    |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.0198802947998047 MB  | 26.065 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.069972038269043 MB  | 25.43 s    |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.070079803466797 MB  | 26.399 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.070022583007812 MB  | 25.501 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 22.875 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 23.727 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 22.972 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.769392013549805 MB  | 24.118 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.767662048339844 MB  | 24.326 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.77004051208496 MB   | 24.177 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.078281402587891 MB   | 27.118 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.071378707885742 MB   | 823.392 s  |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.067712783813477 MB   | 26.253 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262073516845703 MB  | 26.162 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.26193904876709 MB   | 25.647 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262253761291504 MB  | 25.502 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 23.334 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 923.593 s  |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 23.152 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.926265716552734 MB  | 24.206 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.93179893493652 MB   | 24.437 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.93519401550293 MB   | 24.592 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.165613174438477 MB  | 26.46 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.160831451416016 MB  | 26.38 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.167511940002441 MB  | 26.938 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.12404441833496 MB   | 23.736 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.12448501586914 MB   | 23.737 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.123520851135254 MB  | 23.54 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 22.595 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 22.731 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 15) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 22.667 s   |
