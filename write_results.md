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

## Baseline

**These tests were run using a 0.14.0-SNAPSHOT build of parquet-mr w/ head commit 9b5a962df3007009a227ef421600197531f970a5, on a 64 GB M1 MBP, using openJDK 17.**

| Cardinality | Dict-Encoded Cols                            | Compression      | Page Size | Data Distribution | Sorting      | File Size                  | Write Time   |
|-------------|----------------------------------------------|------------------|-----------|-------------------|--------------|----------------------------|--------------|
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Shuffled** | **23.47275161743164 MB**   | **25.373 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | Normal            | Shuffled     | 23.382027626037598 MB      | 25.488 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | Normal            | Shuffled     | 23.472515106201172 MB      | 25.369 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | Normal            | Shuffled     | 23.381916046142578 MB      | 25.441 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Sorted**   | **0.28974342346191406 MB** | **27.094 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | Normal            | Sorted       | 0.16922855377197266 MB     | 26.541 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | Normal            | Sorted       | 0.28966617584228516 MB     | 26.148 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | Normal            | Sorted       | 0.16945171356201172 MB     | 26.252 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Shuffled** | **17.970391273498535 MB**  | **24.296 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | RoundRobin        | Shuffled     | 17.964359283447266 MB      | 23.733 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | RoundRobin        | Shuffled     | 17.970388412475586 MB      | 23.523 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | RoundRobin        | Shuffled     | 17.964329719543457 MB      | 23.528 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Sorted**   | **0.06650638580322266 MB** | **25.088 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | RoundRobin        | Sorted       | 0.05811119079589844 MB     | 24.797 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | RoundRobin        | Sorted       | 0.06650638580322266 MB     | 24.057 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | RoundRobin        | Sorted       | 0.05811119079589844 MB     | 24.088 s     |
| **5000**    | **stringField<br/>doubleField**              | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Shuffled** | **38.40328502655029 MB**   | **26.744 s** |
| 5000        | stringField<br/>doubleField                  | ZSTD             | 1 MB      | Normal            | Shuffled     | 30.59129238128662 MB       | 27.411 s     |
| 5000        | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | Normal            | Shuffled     | 38.40816783905029 MB       | 27.195 s     |
| 5000        | stringField<br/>doubleField                  | ZSTD             | 10 MB     | Normal            | Shuffled     | 30.593666076660156 MB      | 28.437 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Sorted**   | **1.202035903930664 MB**   | **26.158 s** |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | Normal            | Sorted       | 0.5516834259033203 MB      | 26.585 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | Normal            | Sorted       | 1.2015323638916016 MB      | 26.142 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | Normal            | Sorted       | 0.5419712066650391 MB      | 26.765 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Shuffled** | **23.41111660003662 MB**   | **25.8 s**   |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | RoundRobin        | Shuffled     | 23.354849815368652 MB      | 25.379 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | RoundRobin        | Shuffled     | 23.411113739013672 MB      | 26.569 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | RoundRobin        | Shuffled     | 23.35483455657959 MB       | 25.015 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Sorted**   | **0.191162109375 MB**      | **23.623 s** |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | RoundRobin        | Sorted       | 0.1070871353149414 MB      | 24.528 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | RoundRobin        | Sorted       | 0.191162109375 MB          | 24.705 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | RoundRobin        | Sorted       | 0.1070871353149414 MB      | 24.648 s     |
| **50000**   |                                              | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Shuffled** | **101.94068050384521 MB**  | **26.321 s** |
| 50000       |                                              | ZSTD             | 1 MB      | Normal            | Shuffled     | 45.421515464782715 MB      | 26.242 s     |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | Normal            | Shuffled     | 101.93698406219482 MB      | 25.356 s     |
| 50000       |                                              | ZSTD             | 10 MB     | Normal            | Shuffled     | 45.41885185241699 MB       | 25.461 s     |
| **50000**   | **stringField<br/>doubleField**              | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Sorted**   | **81.1013126373291 MB**    | **27.107 s** |
| 50000       | stringField<br/>doubleField                  | ZSTD             | 1 MB      | Normal            | Sorted       | 1.997756004333496 MB       | 28.522 s     |
| 50000       | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | Normal            | Sorted       | 81.10037326812744 MB       | 27.236 s     |
| 50000       | stringField<br/>doubleField                  | ZSTD             | 10 MB     | Normal            | Sorted       | 2.002534866333008 MB       | 28.315 s     |
| **50000**   |                                              | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Shuffled** | **99.11835289001465 MB**   | **25.281 s** |
| 50000       |                                              | ZSTD             | 1 MB      | RoundRobin        | Shuffled     | 40.00043487548828 MB       | 25.323 s     |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | RoundRobin        | Shuffled     | 99.1183500289917 MB        | 25.778 s     |
| 50000       |                                              | ZSTD             | 10 MB     | RoundRobin        | Shuffled     | 39.99959468841553 MB       | 25.06 s      |
| **50000**   | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Sorted**   | **1.6037378311157227 MB**  | **24.106 s** |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | RoundRobin        | Sorted       | 0.5769920349121094 MB      | 24.056 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | RoundRobin        | Sorted       | 1.6037378311157227 MB      | 24.517 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | RoundRobin        | Sorted       | 0.5769920349121094 MB      | 24.15 s      |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Shuffled** | **103.65634059906006 MB**  | **25.649 s** |
| 100000      |                                              | ZSTD             | 1 MB      | Normal            | Shuffled     | 47.97103977203369 MB       | 26.517 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | Normal            | Shuffled     | 103.65502643585205 MB      | 24.773 s     |
| 100000      |                                              | ZSTD             | 10 MB     | Normal            | Shuffled     | 47.968990325927734 MB      | 25.357 s     |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Sorted**   | **103.658203125 MB**       | **27.779 s** |
| 100000      |                                              | ZSTD             | 1 MB      | Normal            | Sorted       | 2.636960029602051 MB       | 27.792 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | Normal            | Sorted       | 103.6509370803833 MB       | 27.257 s     |
| 100000      |                                              | ZSTD             | 10 MB     | Normal            | Sorted       | 2.638911247253418 MB       | 26.969 s     |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Shuffled** | **99.64859104156494 MB**   | **24.683 s** |
| 100000      |                                              | ZSTD             | 1 MB      | RoundRobin        | Shuffled     | 41.86725425720215 MB       | 25.089 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | RoundRobin        | Shuffled     | 99.64855861663818 MB       | 24.675 s     |
| 100000      |                                              | ZSTD             | 10 MB     | RoundRobin        | Shuffled     | 41.866536140441895 MB      | 24.935 s     |
| **100000**  | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Sorted**   | **2.991497039794922 MB**   | **24.603 s** |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | RoundRobin        | Sorted       | 1.015192985534668 MB       | 24.606 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | RoundRobin        | Sorted       | 2.991497039794922 MB       | 24.417 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | RoundRobin        | Sorted       | 1.015192985534668 MB       | 24.378 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Shuffled** | **108.42271995544434 MB**  | **25.71 s**  |
| 1000000     |                                              | ZSTD             | 1 MB      | Normal            | Shuffled     | 54.06161403656006 MB       | 25.54 s      |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | Normal            | Shuffled     | 108.42219161987305 MB      | 25.519 s     |
| 1000000     |                                              | ZSTD             | 10 MB     | Normal            | Shuffled     | 54.059226989746094 MB      | 26.409 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | **Normal**        | **Sorted**   | **108.42190837860107 MB**  | **27.612 s** |
| 1000000     |                                              | ZSTD             | 1 MB      | Normal            | Sorted       | 14.103327751159668 MB      | 28.665 s     |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | Normal            | Sorted       | 108.4225664138794 MB       | 28.504 s     |
| 1000000     |                                              | ZSTD             | 10 MB     | Normal            | Sorted       | 14.101709365844727 MB      | 28.882 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Shuffled** | **104.41773414611816 MB**  | **25.022 s** |
| 1000000     |                                              | ZSTD             | 1 MB      | RoundRobin        | Shuffled     | 49.24773025512695 MB       | 25.118 s     |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | RoundRobin        | Shuffled     | 104.41772556304932 MB      | 24.874 s     |
| 1000000     |                                              | ZSTD             | 10 MB     | RoundRobin        | Shuffled     | 49.246949195861816 MB      | 25.058 s     |
| **1000000** | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **RoundRobin**    | **Sorted**   | **97.6240291595459 MB**    | **23.763 s** |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD             | 1 MB      | RoundRobin        | Sorted       | 6.713757514953613 MB       | 23.828 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | RoundRobin        | Sorted       | 97.6240291595459 MB        | 23.854 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD             | 10 MB     | RoundRobin        | Sorted       | 6.713757514953613 MB       | 24.185 s     |
