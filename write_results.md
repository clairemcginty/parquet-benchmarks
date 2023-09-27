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

## Result Data

**These tests were run using a 0.14.0-SNAPSHOT build of parquet-mr w/ head commit 9b5a962df3007009a227ef421600197531f970a5, on a 64 GB M1 MBP, using openJDK 17.**

## Baseline

| Cardinality | Dict-Encoded Cols                            | Compression      | Page Size | Dict Page Size | Data Distribution | Sorting      | Extra Props | File Size                  | Write Time   |
|-------------|----------------------------------------------|------------------|-----------|----------------|-------------------|--------------|-------------|----------------------------|--------------|
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Shuffled** |             | **23.4722900390625 MB**    | **22.329 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 23.472628593444824 MB      | 22.423 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 23.473164558410645 MB      | 22.893 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 23.472527503967285 MB      | 22.615 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 23.472851753234863 MB      | 23.092 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 23.47343349456787 MB       | 23.02 s      |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Shuffled     |             | 23.3819637298584 MB        | 22.886 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 23.382169723510742 MB      | 23.111 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 23.381975173950195 MB      | 23.191 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 23.381860733032227 MB      | 23.283 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 23.38181209564209 MB       | 24.038 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 23.381765365600586 MB      | 23.743 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Sorted**   |             | **0.2903270721435547 MB**  | **24.074 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Sorted       |             | 0.2910652160644531 MB      | 24.025 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Sorted       |             | 0.2907114028930664 MB      | 23.378 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Sorted       |             | 0.29032230377197266 MB     | 22.66 s      |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Sorted       |             | 0.290130615234375 MB       | 22.697 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Sorted       |             | 0.2903470993041992 MB      | 22.979 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Sorted       |             | 0.16928958892822266 MB     | 23.723 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Sorted       |             | 0.16951751708984375 MB     | 23.799 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Sorted       |             | 0.16934585571289062 MB     | 23.168 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Sorted       |             | 0.16944408416748047 MB     | 23.168 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Sorted       |             | 0.1695089340209961 MB      | 23.224 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Sorted       |             | 0.16986370086669922 MB     | 22.939 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Shuffled** |             | **17.970382690429688 MB**  | **22.638 s** |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 17.97037982940674 MB       | 21.606 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 17.970388412475586 MB      | 22.749 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 17.970393180847168 MB      | 21.876 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 17.970391273498535 MB      | 22.497 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 17.97037982940674 MB       | 22.505 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Shuffled     |             | 17.964327812194824 MB      | 21.814 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 17.964366912841797 MB      | 21.2 s       |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 17.964366912841797 MB      | 22.321 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 17.964356422424316 MB      | 21.679 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 17.96434783935547 MB       | 21.85 s      |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 17.96435832977295 MB       | 21.712 s     |
| **1000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Sorted**   |             | **0.06650638580322266 MB** | **24.83 s**  |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.06650638580322266 MB     | 21.051 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.06650638580322266 MB     | 20.964 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.06650638580322266 MB     | 20.734 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.06650638580322266 MB     | 20.823 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 0.06650638580322266 MB     | 20.896 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Sorted       |             | 0.05811119079589844 MB     | 22.901 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.05811119079589844 MB     | 21.226 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.05811119079589844 MB     | 20.896 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.05811119079589844 MB     | 21.24 s      |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.05811119079589844 MB     | 20.874 s     |
| 1000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 0.05811119079589844 MB     | 20.942 s     |
| **5000**    | **stringField<br/>doubleField**              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Shuffled** |             | **38.402926445007324 MB**  | **23.933 s** |
| 5000        | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 38.40816307067871 MB       | 23.739 s     |
| 5000        | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 38.405006408691406 MB      | 23.819 s     |
| 5000        | stringField<br/>doubleField                  | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 38.39303493499756 MB       | 23.765 s     |
| 5000        | stringField<br/>doubleField                  | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 38.41379261016846 MB       | 23.762 s     |
| 5000        | stringField<br/>doubleField                  | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 38.39376735687256 MB       | 23.923 s     |
| 5000        | stringField<br/>doubleField                  | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Shuffled     |             | 30.584674835205078 MB      | 24.104 s     |
| 5000        | stringField<br/>doubleField                  | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 30.589930534362793 MB      | 23.801 s     |
| 5000        | stringField<br/>doubleField                  | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 30.591837882995605 MB      | 24.14 s      |
| 5000        | stringField<br/>doubleField                  | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 30.592344284057617 MB      | 24.886 s     |
| 5000        | stringField<br/>doubleField                  | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 30.589900970458984 MB      | 23.892 s     |
| 5000        | stringField<br/>doubleField                  | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 30.581298828125 MB         | 23.989 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Sorted**   |             | **1.2016019821166992 MB**  | **24.586 s** |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Sorted       |             | 1.2016010284423828 MB      | 23.669 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Sorted       |             | 1.2039566040039062 MB      | 23.941 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Sorted       |             | 1.2030048370361328 MB      | 23.634 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Sorted       |             | 1.2003402709960938 MB      | 24.194 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Sorted       |             | 1.2031888961791992 MB      | 23.359 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Sorted       |             | 0.5406560897827148 MB      | 24.026 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Sorted       |             | 0.5418920516967773 MB      | 23.436 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Sorted       |             | 0.5418004989624023 MB      | 23.802 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Sorted       |             | 0.5453424453735352 MB      | 24.156 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Sorted       |             | 0.5380029678344727 MB      | 24.159 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Sorted       |             | 0.5406818389892578 MB      | 23.595 s     |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Shuffled** |             | **23.41108798980713 MB**   | **22.166 s** |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 23.411139488220215 MB      | 22.349 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 23.41111660003662 MB       | 22.059 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 23.41111946105957 MB       | 22.753 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 23.41112232208252 MB       | 22.258 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 23.411099433898926 MB      | 22.29 s      |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Shuffled     |             | 23.35485553741455 MB       | 22.459 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 23.35480785369873 MB       | 22.267 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 23.354817390441895 MB      | 22.333 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 23.354887008666992 MB      | 22.568 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 23.354873657226562 MB      | 22.481 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 23.354886054992676 MB      | 22.27 s      |
| **5000**    | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Sorted**   |             | **0.191162109375 MB**      | **21.061 s** |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.191162109375 MB          | 20.989 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.191162109375 MB          | 20.98 s      |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.191162109375 MB          | 21.494 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.191162109375 MB          | 20.901 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 0.191162109375 MB          | 20.978 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Sorted       |             | 0.1070871353149414 MB      | 21.167 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.1070871353149414 MB      | 21.138 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.1070871353149414 MB      | 21.398 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.1070871353149414 MB      | 21.049 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.1070871353149414 MB      | 21.099 s     |
| 5000        | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 0.1070871353149414 MB      | 21.344 s     |
| **50000**   |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Shuffled** |             | **101.93850231170654 MB**  | **22.207 s** |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 101.93762302398682 MB      | 22.277 s     |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 101.93991947174072 MB      | 22.17 s      |
| 50000       |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 101.93821144104004 MB      | 22.361 s     |
| 50000       |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 101.9404821395874 MB       | 22.214 s     |
| 50000       |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 101.94060802459717 MB      | 22.306 s     |
| 50000       |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Shuffled     |             | 45.41781139373779 MB       | 22.6 s       |
| 50000       |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 45.41973400115967 MB       | 22.81 s      |
| 50000       |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 45.41772747039795 MB       | 22.772 s     |
| 50000       |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 45.42021465301514 MB       | 22.847 s     |
| 50000       |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 45.419589042663574 MB      | 22.916 s     |
| 50000       |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 45.417680740356445 MB      | 22.789 s     |
| **50000**   | **stringField<br/>doubleField**              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Sorted**   |             | **81.09330749511719 MB**   | **24.343 s** |
| 50000       | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Sorted       |             | 81.10061264038086 MB       | 24.488 s     |
| 50000       | stringField<br/>doubleField                  | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Sorted       |             | 26.960718154907227 MB      | 24.628 s     |
| 50000       | stringField<br/>doubleField                  | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Sorted       |             | 81.09521484375 MB          | 24.798 s     |
| 50000       | stringField<br/>doubleField                  | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Sorted       |             | 26.96629238128662 MB       | 25.532 s     |
| 50000       | stringField<br/>doubleField                  | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Sorted       |             | 26.970330238342285 MB      | 24.44 s      |
| 50000       | stringField<br/>doubleField                  | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Sorted       |             | 2.0018577575683594 MB      | 24.705 s     |
| 50000       | stringField<br/>doubleField                  | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Sorted       |             | 1.9890127182006836 MB      | 24.325 s     |
| 50000       | stringField<br/>doubleField                  | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Sorted       |             | 2.956907272338867 MB       | 25.103 s     |
| 50000       | stringField<br/>doubleField                  | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Sorted       |             | 2.000965118408203 MB       | 24.498 s     |
| 50000       | stringField<br/>doubleField                  | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Sorted       |             | 2.9345130920410156 MB      | 24.964 s     |
| 50000       | stringField<br/>doubleField                  | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Sorted       |             | 2.9415769577026367 MB      | 24.782 s     |
| **50000**   |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Shuffled** |             | **99.11834716796875 MB**   | **22.9 s**   |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 99.11838340759277 MB       | 22.333 s     |
| 50000       |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 99.11837005615234 MB       | 21.771 s     |
| 50000       |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 99.11836528778076 MB       | 21.966 s     |
| 50000       |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 99.11836528778076 MB       | 21.81 s      |
| 50000       |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 99.11837768554688 MB       | 22.003 s     |
| 50000       |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Shuffled     |             | 40.00229263305664 MB       | 22.371 s     |
| 50000       |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 40.000550270080566 MB      | 22.395 s     |
| 50000       |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 40.00164985656738 MB       | 23.439 s     |
| 50000       |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 40.00048065185547 MB       | 23.333 s     |
| 50000       |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 39.99679183959961 MB       | 22.446 s     |
| 50000       |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 40.00142860412598 MB       | 23.048 s     |
| **50000**   | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Sorted**   |             | **1.6037378311157227 MB**  | **21.344 s** |
| 50000       | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 1.6037378311157227 MB      | 21.184 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 1.6037378311157227 MB      | 21.238 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 1.6037378311157227 MB      | 21.383 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 1.6037378311157227 MB      | 21.381 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 1.6037378311157227 MB      | 21.387 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Sorted       |             | 0.5769920349121094 MB      | 21.239 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.5769920349121094 MB      | 21.342 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.5769920349121094 MB      | 21.588 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 0.5769920349121094 MB      | 21.395 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 0.5769920349121094 MB      | 21.927 s     |
| 50000       | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 0.5769920349121094 MB      | 21.558 s     |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Shuffled** |             | **103.65415573120117 MB**  | **22.77 s**  |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 103.65515041351318 MB      | 22.498 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 103.65121841430664 MB      | 22.39 s      |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 103.6572618484497 MB       | 22.26 s      |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 103.65322208404541 MB      | 22.23 s      |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 103.65467834472656 MB      | 22.238 s     |
| 100000      |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Shuffled     |             | 47.97018241882324 MB       | 22.888 s     |
| 100000      |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 47.97392272949219 MB       | 22.905 s     |
| 100000      |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 47.970412254333496 MB      | 23.115 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 47.97272872924805 MB       | 22.856 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 47.97314453125 MB          | 23.618 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 47.97404384613037 MB       | 23.425 s     |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Sorted**   |             | **103.65542793273926 MB**  | **24.338 s** |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Sorted       |             | 103.65390491485596 MB      | 24.251 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Sorted       |             | 103.65701198577881 MB      | 24.195 s     |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Sorted       |             | 103.6555404663086 MB       | 24.109 s     |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Sorted       |             | 103.65639209747314 MB      | 24.058 s     |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Sorted       |             | 103.65402030944824 MB      | 24.172 s     |
| 100000      |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Sorted       |             | 2.630552291870117 MB       | 25.227 s     |
| 100000      |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Sorted       |             | 2.6288795471191406 MB      | 25.685 s     |
| 100000      |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Sorted       |             | 2.627203941345215 MB       | 24.955 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Sorted       |             | 2.6386308670043945 MB      | 24.919 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Sorted       |             | 2.6370601654052734 MB      | 24.581 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Sorted       |             | 2.6386566162109375 MB      | 24.68 s      |
| **100000**  |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Shuffled** |             | **99.6485481262207 MB**    | **22.407 s** |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 99.64861011505127 MB       | 22.059 s     |
| 100000      |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 99.64859867095947 MB       | 21.897 s     |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 99.64858722686768 MB       | 21.672 s     |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 99.6485948562622 MB        | 21.832 s     |
| 100000      |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 99.64859104156494 MB       | 22.324 s     |
| 100000      |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Shuffled     |             | 41.86580467224121 MB       | 22.966 s     |
| 100000      |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 41.86729621887207 MB       | 23.33 s      |
| 100000      |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 41.86827373504639 MB       | 22.357 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 41.865983963012695 MB      | 22.416 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 41.86412048339844 MB       | 22.332 s     |
| 100000      |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 41.86623477935791 MB       | 22.174 s     |
| **100000**  | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Sorted**   |             | **2.991497039794922 MB**   | **21.585 s** |
| 100000      | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 2.991497039794922 MB       | 21.639 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 2.991497039794922 MB       | 22.231 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 2.991497039794922 MB       | 21.586 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 2.991497039794922 MB       | 21.399 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 2.991497039794922 MB       | 21.58 s      |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Sorted       |             | 1.015192985534668 MB       | 22.336 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 1.015192985534668 MB       | 21.598 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 1.015192985534668 MB       | 21.821 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 1.015192985534668 MB       | 21.67 s      |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 1.015192985534668 MB       | 21.522 s     |
| 100000      | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 1.015192985534668 MB       | 21.491 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Shuffled** |             | **108.4235725402832 MB**   | **22.716 s** |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 108.42037296295166 MB      | 22.512 s     |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 108.42608547210693 MB      | 22.751 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 108.42116928100586 MB      | 22.369 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 108.4228401184082 MB       | 22.361 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 108.42262363433838 MB      | 22.826 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Shuffled     |             | 54.05525779724121 MB       | 23.125 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Shuffled     |             | 54.06032180786133 MB       | 23.09 s      |
| 1000000     |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Shuffled     |             | 54.05522060394287 MB       | 23.529 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Shuffled     |             | 54.05922603607178 MB       | 23.402 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Shuffled     |             | 54.057308197021484 MB      | 22.999 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Shuffled     |             | 54.05997371673584 MB       | 23.122 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **Normal**        | **Sorted**   |             | **108.42092704772949 MB**  | **25.027 s** |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | Normal            | Sorted       |             | 108.4244794845581 MB       | 24.766 s     |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | Normal            | Sorted       |             | 108.42370796203613 MB      | 24.803 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | Normal            | Sorted       |             | 108.42051029205322 MB      | 25.076 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | Normal            | Sorted       |             | 108.4209337234497 MB       | 24.793 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | Normal            | Sorted       |             | 108.4234733581543 MB       | 24.939 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | Normal            | Sorted       |             | 14.100202560424805 MB      | 26.389 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | Normal            | Sorted       |             | 14.116928100585938 MB      | 25.158 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | Normal            | Sorted       |             | 14.103150367736816 MB      | 25.008 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | Normal            | Sorted       |             | 14.101014137268066 MB      | 24.979 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | Normal            | Sorted       |             | 14.101051330566406 MB      | 25.292 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | Normal            | Sorted       |             | 14.10360336303711 MB       | 25.069 s     |
| **1000000** |                                              | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Shuffled** |             | **104.41772747039795 MB**  | **21.523 s** |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 104.41773414611816 MB      | 21.617 s     |
| 1000000     |                                              | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 104.41774368286133 MB      | 21.836 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 104.41773223876953 MB      | 21.714 s     |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 104.4177417755127 MB       | 21.83 s      |
| 1000000     |                                              | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 104.41772079467773 MB      | 21.686 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Shuffled     |             | 49.247376441955566 MB      | 22.245 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 49.24638843536377 MB       | 22.367 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 49.2475061416626 MB        | 22.298 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Shuffled     |             | 49.24793338775635 MB       | 22.5 s       |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Shuffled     |             | 49.24742889404297 MB       | 22.355 s     |
| 1000000     |                                              | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Shuffled     |             | 49.24627208709717 MB       | 22.263 s     |
| **1000000** | **stringField<br/>doubleField<br/>intField** | **UNCOMPRESSED** | **1 MB**  | **1 MB**       | **RoundRobin**    | **Sorted**   |             | **97.6240291595459 MB**    | **21.248 s** |
| 1000000     | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 97.6240291595459 MB        | 21.248 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 54.85803699493408 MB       | 23.091 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 97.6240291595459 MB        | 21.956 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 54.85803699493408 MB       | 23.036 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | UNCOMPRESSED     | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 54.85803699493408 MB       | 22.993 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 1 MB      | 1 MB           | RoundRobin        | Sorted       |             | 6.713757514953613 MB       | 21.468 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 1 MB           | RoundRobin        | Sorted       |             | 6.713757514953613 MB       | 21.611 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 10 MB     | 10 MB          | RoundRobin        | Sorted       |             | 23.03006362915039 MB       | 23.258 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 1 MB           | RoundRobin        | Sorted       |             | 6.713757514953613 MB       | 21.351 s     |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 10 MB          | RoundRobin        | Sorted       |             | 23.03006362915039 MB       | 23.55 s      |
| 1000000     | stringField<br/>doubleField<br/>intField     | ZSTD(Level 3)    | 50 MB     | 50 MB          | RoundRobin        | Sorted       |             | 23.03006362915039 MB       | 24.307 s     |

##  Max Dictionary Compression Ratio Option

Test of a "max compression ratio" for dictionary encoding, which will produce a dictionary as long as the ratio of encoded data to raw data is below a certain threshold. (Basically, this is a ratio of repeated values : total values.)
You can see that with higher ratios, Parquet is more likely to produce dict-encoded columns, even for shuffled/high cardinality data.

Code reference: https://github.com/apache/parquet-mr/compare/master...clairemcginty:parquet-mr:test-compression

### parquet.max.dictionary.compression.ratio = 0.5

| Cardinality | Dict-Encoded Cols                        | Compression   | Page Size | Dict Page Size | Data Distribution | Sorting  | Extra Props                                    | File Size              | Write Time |
|-------------|------------------------------------------|---------------|-----------|----------------|-------------------|----------|------------------------------------------------|------------------------|------------|
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.472222328186035 MB  | 25.066 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.47243595123291 MB   | 25.678 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.472421646118164 MB  | 25.982 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.47252082824707 MB   | 25.78 s    |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.473129272460938 MB  | 25.421 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.47240161895752 MB   | 25.526 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.38211154937744 MB   | 25.525 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381821632385254 MB  | 26.0 s     |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381807327270508 MB  | 26.233 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.38182544708252 MB   | 25.713 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.382004737854004 MB  | 25.322 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.381892204284668 MB  | 25.673 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.28980445861816406 MB | 26.951 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.2911348342895508 MB  | 25.805 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.29075050354003906 MB | 25.632 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.28999805450439453 MB | 25.768 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.2900400161743164 MB  | 25.592 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.29018688201904297 MB | 25.684 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1697244644165039 MB  | 26.665 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.16956520080566406 MB | 25.906 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.16998767852783203 MB | 26.099 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1700601577758789 MB  | 25.873 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.17029094696044922 MB | 26.183 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1696786880493164 MB  | 25.811 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970407485961914 MB  | 23.962 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970382690429688 MB  | 24.66 s    |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970382690429688 MB  | 23.963 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970394134521484 MB  | 24.58 s    |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970394134521484 MB  | 24.574 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.970378875732422 MB  | 25.06 s    |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.96433448791504 MB   | 24.28 s    |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964353561401367 MB  | 24.087 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964317321777344 MB  | 23.855 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964340209960938 MB  | 24.545 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.96436882019043 MB   | 24.711 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 17.964306831359863 MB  | 24.747 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 25.723 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 23.989 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 23.927 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 24.064 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 23.943 s   |
| 1000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.06650638580322266 MB | 23.871 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 24.635 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 23.664 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 24.291 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 23.923 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 23.609 s   |
| 1000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.05811119079589844 MB | 23.527 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.820634841918945 MB  | 26.722 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.813767433166504 MB  | 27.279 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.819554328918457 MB  | 27.579 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.8362455368042 MB    | 27.102 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.82191753387451 MB   | 26.779 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 28.806218147277832 MB  | 26.921 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.701781272888184 MB  | 26.604 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.70246410369873 MB   | 26.651 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.703757286071777 MB  | 26.795 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.706488609313965 MB  | 27.109 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.70261001586914 MB   | 27.27 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 27.711233139038086 MB  | 26.955 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.2011947631835938 MB  | 26.604 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.2011585235595703 MB  | 26.381 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.2016992568969727 MB  | 26.065 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.2033367156982422 MB  | 26.29 s    |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.2000274658203125 MB  | 27.106 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.1995611190795898 MB  | 26.759 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5415010452270508 MB  | 26.33 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.539484977722168 MB   | 26.678 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.541752815246582 MB   | 26.432 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5458965301513672 MB  | 26.348 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5430965423583984 MB  | 26.057 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5472297668457031 MB  | 26.026 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.411115646362305 MB  | 25.176 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.411110877990723 MB  | 24.962 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.411110877990723 MB  | 25.49 s    |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.41111660003662 MB   | 24.935 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.41112232208252 MB   | 24.902 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.41109561920166 MB   | 25.03 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.3548583984375 MB    | 25.477 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354912757873535 MB  | 25.195 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354796409606934 MB  | 25.15 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354857444763184 MB  | 25.591 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354846000671387 MB  | 25.13 s    |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 23.354867935180664 MB  | 24.766 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 23.773 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 23.661 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 24.192 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 23.881 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 23.611 s   |
| 5000        | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.191162109375 MB      | 23.802 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 24.432 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 24.054 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 24.295 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 23.854 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 23.672 s   |
| 5000        | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.1070871353149414 MB  | 23.811 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 98.42677783966064 MB   | 25.379 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 98.46480655670166 MB   | 25.324 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.04789447784424 MB   | 27.911 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 98.46821594238281 MB   | 25.878 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.046202659606934 MB  | 28.037 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.0458984375 MB       | 28.276 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.029611587524414 MB  | 25.721 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.01645755767822 MB   | 25.716 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 35.76422691345215 MB   | 28.636 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.02737903594971 MB   | 25.922 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 35.75613307952881 MB   | 28.009 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 35.7638521194458 MB    | 28.168 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 64.57947731018066 MB   | 27.489 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 64.58010673522949 MB   | 27.372 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.463946342468262 MB  | 27.122 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 64.57654094696045 MB   | 27.13 s    |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.480406761169434 MB  | 27.292 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.477828979492188 MB  | 27.142 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.0197925567626953 MB  | 27.222 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.01560115814209 MB    | 27.601 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 4.108355522155762 MB   | 27.749 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 3.018604278564453 MB   | 27.44 s    |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 4.116168022155762 MB   | 27.753 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 4.126495361328125 MB   | 27.379 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.658214569091797 MB  | 27.005 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.65818500518799 MB   | 27.133 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.658204078674316 MB  | 27.929 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.65818691253662 MB   | 26.608 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.658236503601074 MB  | 26.715 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.6582088470459 MB    | 26.562 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.070091247558594 MB  | 27.483 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.069969177246094 MB  | 27.144 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.070021629333496 MB  | 27.473 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.06999397277832 MB   | 26.784 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.069993019104004 MB  | 27.076 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 29.06989288330078 MB   | 26.79 s    |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 24.668 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 24.144 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 24.106 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 23.937 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 23.914 s   |
| 50000       | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.6037378311157227 MB  | 24.299 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 24.803 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 24.329 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 24.159 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 23.856 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 23.966 s   |
| 50000       | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 0.5769920349121094 MB  | 23.958 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 104.03614330291748 MB  | 25.199 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 104.03122997283936 MB  | 25.091 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 44.93772792816162 MB   | 28.814 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 104.0381269454956 MB   | 25.162 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 44.9373664855957 MB    | 28.578 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 44.95126819610596 MB   | 28.695 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.76775360107422 MB   | 26.013 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.771817207336426 MB  | 25.414 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.050241470336914 MB  | 28.769 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 48.76794242858887 MB   | 25.739 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.04986572265625 MB   | 29.067 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 39.05006408691406 MB   | 28.991 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 94.17981719970703 MB   | 27.011 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 94.10799407958984 MB   | 27.055 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 20.51360034942627 MB   | 27.793 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 94.1766586303711 MB    | 27.523 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 20.524049758911133 MB  | 28.217 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 20.53489398956299 MB   | 28.0 s     |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.073307991027832 MB   | 27.377 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.079611778259277 MB   | 27.355 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.074830055236816 MB  | 28.116 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 5.076205253601074 MB   | 29.173 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.055835723876953 MB  | 36.1 s     |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 10.084415435791016 MB  | 27.489 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.41959190368652 MB   | 27.719 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.41955089569092 MB   | 27.324 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.41953182220459 MB   | 27.657 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.419554710388184 MB  | 28.177 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.419554710388184 MB  | 28.249 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 32.419569969177246 MB  | 28.037 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.26191520690918 MB   | 27.447 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262118339538574 MB  | 27.193 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.261953353881836 MB  | 27.484 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262131690979004 MB  | 27.313 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.262248992919922 MB  | 27.365 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 31.26213836669922 MB   | 27.562 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 24.404 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 24.752 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 24.276 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 24.479 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 24.782 s   |
| 100000      | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 2.991497039794922 MB   | 24.559 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.191 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.705 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.725 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.775 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.514 s   |
| 100000      | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 1.015192985534668 MB   | 24.676 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 109.2839527130127 MB   | 25.16 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 109.28225994110107 MB  | 24.94 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 114.86360836029053 MB  | 28.094 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 109.28554725646973 MB  | 24.943 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 114.83678722381592 MB  | 27.974 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 98.17296409606934 MB   | 32.64 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.92994976043701 MB   | 25.762 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.93323040008545 MB   | 25.659 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 63.496527671813965 MB  | 28.507 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 54.93170642852783 MB   | 25.966 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 63.49849987030029 MB   | 30.695 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 67.62064361572266 MB   | 33.384 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 109.17657470703125 MB  | 27.733 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 109.16957473754883 MB  | 27.634 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 107.39879608154297 MB  | 30.706 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 109.17083740234375 MB  | 27.926 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 107.40631866455078 MB  | 30.2 s     |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 97.62663173675537 MB   | 33.2 s     |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.167672157287598 MB  | 27.738 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.171114921569824 MB  | 27.954 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 33.0784912109375 MB    | 31.192 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 15.170759201049805 MB  | 28.24 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 33.08864402770996 MB   | 30.938 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 47.49184703826904 MB   | 33.06 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 105.22843933105469 MB  | 24.784 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 105.22853755950928 MB  | 24.728 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 56.29562950134277 MB   | 29.943 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 105.2265214920044 MB   | 26.417 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 56.29563045501709 MB   | 29.651 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 56.29563522338867 MB   | 29.459 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.12490367889404 MB   | 24.735 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.12476062774658 MB   | 25.049 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.27782154083252 MB   | 29.738 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 50.121745109558105 MB  | 24.791 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.270514488220215 MB  | 29.49 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | parquet.max.dictionary.compression.ratio = 0.5 | 45.26921081542969 MB   | 29.236 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 97.6240291595459 MB    | 23.844 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 97.6240291595459 MB    | 24.158 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 54.85803699493408 MB   | 25.46 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 97.6240291595459 MB    | 24.013 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 54.85803699493408 MB   | 25.71 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 54.85803699493408 MB   | 25.526 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 24.082 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 23.88 s    |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 23.03006362915039 MB   | 25.713 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 6.713757514953613 MB   | 24.008 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 23.03006362915039 MB   | 25.954 s   |
| 1000000     | stringField<br/>doubleField<br/>intField | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | parquet.max.dictionary.compression.ratio = 0.5 | 23.03006362915039 MB   | 27.292 s   |


# Overall comparison

Overall thoughts:
 - The max ratio param doesn't benefit data that's in sorted order (IMO, because even without the addition of this param, sorted columns are more likely to produce a successful dict encoding)
 - On shuffled (non-sorted) data, the max ratio param produces a much better (up to 50%) uncompressed file result. However, after applying file-level compression such as ZSTD, baseline and max ratio param turn out about equal (within 5-10% margin)
 - Max ratio param works best with larger page sizes (10 mb or 50mb) with large-ish dictionary page sizes (10mb)

| Cardinality | Compression   | Page Size | Dict Page Size | Data Distribution | Sorting  | File Size (Baseline)   | File Size (Max Compression Ratio 0.5) | Winner                |
|-------------|---------------|-----------|----------------|-------------------|----------|------------------------|---------------------------------------|-----------------------|
| 1000        | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | 23.4722900390625 MB    | 23.472222328186035 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | 23.472628593444824 MB  | 23.47243595123291 MB                  | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | 23.473164558410645 MB  | 23.472421646118164 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | 23.472527503967285 MB  | 23.47252082824707 MB                  | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | 23.472851753234863 MB  | 23.473129272460938 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | 23.47343349456787 MB   | 23.47240161895752 MB                  | None                  |
| 1000        | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | 23.3819637298584 MB    | 23.38211154937744 MB                  | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | 23.382169723510742 MB  | 23.381821632385254 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | 23.381975173950195 MB  | 23.381807327270508 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | 23.381860733032227 MB  | 23.38182544708252 MB                  | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | 23.38181209564209 MB   | 23.382004737854004 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | 23.381765365600586 MB  | 23.381892204284668 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | 0.2903270721435547 MB  | 0.28980445861816406 MB                | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | 0.2910652160644531 MB  | 0.2911348342895508 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | 0.2907114028930664 MB  | 0.29075050354003906 MB                | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | 0.29032230377197266 MB | 0.28999805450439453 MB                | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | 0.290130615234375 MB   | 0.2900400161743164 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | 0.2903470993041992 MB  | 0.29018688201904297 MB                | None                  |
| 1000        | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | 0.16928958892822266 MB | 0.1697244644165039 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | 0.16951751708984375 MB | 0.16956520080566406 MB                | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | 0.16934585571289062 MB | 0.16998767852783203 MB                | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | 0.16944408416748047 MB | 0.1700601577758789 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | 0.1695089340209961 MB  | 0.17029094696044922 MB                | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | 0.16986370086669922 MB | 0.1696786880493164 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 17.970382690429688 MB  | 17.970407485961914 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 17.97037982940674 MB   | 17.970382690429688 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 17.970388412475586 MB  | 17.970382690429688 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 17.970393180847168 MB  | 17.970394134521484 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 17.970391273498535 MB  | 17.970394134521484 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 17.97037982940674 MB   | 17.970378875732422 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 17.964327812194824 MB  | 17.96433448791504 MB                  | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 17.964366912841797 MB  | 17.964353561401367 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 17.964366912841797 MB  | 17.964317321777344 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 17.964356422424316 MB  | 17.964340209960938 MB                 | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 17.96434783935547 MB   | 17.96436882019043 MB                  | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 17.96435832977295 MB   | 17.964306831359863 MB                 | None                  |
| 1000        | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 0.06650638580322266 MB | 0.06650638580322266 MB                | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 0.06650638580322266 MB | 0.06650638580322266 MB                | None                  |
| 1000        | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 0.06650638580322266 MB | 0.06650638580322266 MB                | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 0.06650638580322266 MB | 0.06650638580322266 MB                | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 0.06650638580322266 MB | 0.06650638580322266 MB                | None                  |
| 1000        | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 0.06650638580322266 MB | 0.06650638580322266 MB                | None                  |
| 1000        | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 0.05811119079589844 MB | 0.05811119079589844 MB                | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 0.05811119079589844 MB | 0.05811119079589844 MB                | None                  |
| 1000        | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 0.05811119079589844 MB | 0.05811119079589844 MB                | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 0.05811119079589844 MB | 0.05811119079589844 MB                | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 0.05811119079589844 MB | 0.05811119079589844 MB                | None                  |
| 1000        | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 0.05811119079589844 MB | 0.05811119079589844 MB                | None                  |
| 5000        | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | 38.402926445007324 MB  | 28.820634841918945 MB                 | Max Compression Ratio |
| 5000        | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | 38.40816307067871 MB   | 28.813767433166504 MB                 | Max Compression Ratio |
| 5000        | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | 38.405006408691406 MB  | 28.819554328918457 MB                 | Max Compression Ratio |
| 5000        | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | 38.39303493499756 MB   | 28.8362455368042 MB                   | Max Compression Ratio |
| 5000        | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | 38.41379261016846 MB   | 28.82191753387451 MB                  | Max Compression Ratio |
| 5000        | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | 38.39376735687256 MB   | 28.806218147277832 MB                 | Max Compression Ratio |
| 5000        | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | 30.584674835205078 MB  | 27.701781272888184 MB                 | Max Compression Ratio |
| 5000        | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | 30.589930534362793 MB  | 27.70246410369873 MB                  | Max Compression Ratio |
| 5000        | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | 30.591837882995605 MB  | 27.703757286071777 MB                 | Max Compression Ratio |
| 5000        | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | 30.592344284057617 MB  | 27.706488609313965 MB                 | Max Compression Ratio |
| 5000        | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | 30.589900970458984 MB  | 27.70261001586914 MB                  | Max Compression Ratio |
| 5000        | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | 30.581298828125 MB     | 27.711233139038086 MB                 | Max Compression Ratio |
| 5000        | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | 1.2016019821166992 MB  | 1.2011947631835938 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | 1.2016010284423828 MB  | 1.2011585235595703 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | 1.2039566040039062 MB  | 1.2016992568969727 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | 1.2030048370361328 MB  | 1.2033367156982422 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | 1.2003402709960938 MB  | 1.2000274658203125 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | 1.2031888961791992 MB  | 1.1995611190795898 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | 0.5406560897827148 MB  | 0.5415010452270508 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | 0.5418920516967773 MB  | 0.539484977722168 MB                  | None                  |
| 5000        | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | 0.5418004989624023 MB  | 0.541752815246582 MB                  | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | 0.5453424453735352 MB  | 0.5458965301513672 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | 0.5380029678344727 MB  | 0.5430965423583984 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | 0.5406818389892578 MB  | 0.5472297668457031 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 23.41108798980713 MB   | 23.411115646362305 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 23.411139488220215 MB  | 23.411110877990723 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 23.41111660003662 MB   | 23.411110877990723 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 23.41111946105957 MB   | 23.41111660003662 MB                  | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 23.41112232208252 MB   | 23.41112232208252 MB                  | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 23.411099433898926 MB  | 23.41109561920166 MB                  | None                  |
| 5000        | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 23.35485553741455 MB   | 23.3548583984375 MB                   | None                  |
| 5000        | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 23.35480785369873 MB   | 23.354912757873535 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 23.354817390441895 MB  | 23.354796409606934 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 23.354887008666992 MB  | 23.354857444763184 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 23.354873657226562 MB  | 23.354846000671387 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 23.354886054992676 MB  | 23.354867935180664 MB                 | None                  |
| 5000        | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 0.191162109375 MB      | 0.191162109375 MB                     | None                  |
| 5000        | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 0.191162109375 MB      | 0.191162109375 MB                     | None                  |
| 5000        | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 0.191162109375 MB      | 0.191162109375 MB                     | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 0.191162109375 MB      | 0.191162109375 MB                     | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 0.191162109375 MB      | 0.191162109375 MB                     | None                  |
| 5000        | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 0.191162109375 MB      | 0.191162109375 MB                     | None                  |
| 5000        | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 0.1070871353149414 MB  | 0.1070871353149414 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 0.1070871353149414 MB  | 0.1070871353149414 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 0.1070871353149414 MB  | 0.1070871353149414 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 0.1070871353149414 MB  | 0.1070871353149414 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 0.1070871353149414 MB  | 0.1070871353149414 MB                 | None                  |
| 5000        | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 0.1070871353149414 MB  | 0.1070871353149414 MB                 | None                  |
| 50000       | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | 101.93850231170654 MB  | 98.42677783966064 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | 101.93762302398682 MB  | 98.46480655670166 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | 101.93991947174072 MB  | 39.04789447784424 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | 101.93821144104004 MB  | 98.46821594238281 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | 101.9404821395874 MB   | 39.046202659606934 MB                 | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | 101.94060802459717 MB  | 39.0458984375 MB                      | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | 45.41781139373779 MB   | 45.029611587524414 MB                 | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | 45.41973400115967 MB   | 45.01645755767822 MB                  | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | 45.41772747039795 MB   | 35.76422691345215 MB                  | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | 45.42021465301514 MB   | 45.02737903594971 MB                  | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | 45.419589042663574 MB  | 35.75613307952881 MB                  | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | 45.417680740356445 MB  | 35.7638521194458 MB                   | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | 81.09330749511719 MB   | 64.57947731018066 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | 81.10061264038086 MB   | 64.58010673522949 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | 26.960718154907227 MB  | 10.463946342468262 MB                 | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | 81.09521484375 MB      | 64.57654094696045 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | 26.96629238128662 MB   | 10.480406761169434 MB                 | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | 26.970330238342285 MB  | 10.477828979492188 MB                 | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | 2.0018577575683594 MB  | 3.0197925567626953 MB                 | Baseline              |
| 50000       | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | 1.9890127182006836 MB  | 3.01560115814209 MB                   | Baseline              |
| 50000       | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | 2.956907272338867 MB   | 4.108355522155762 MB                  | Baseline              |
| 50000       | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | 2.000965118408203 MB   | 3.018604278564453 MB                  | Baseline              |
| 50000       | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | 2.9345130920410156 MB  | 4.116168022155762 MB                  | Baseline              |
| 50000       | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | 2.9415769577026367 MB  | 4.126495361328125 MB                  | Baseline              |
| 50000       | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 99.11834716796875 MB   | 29.658214569091797 MB                 | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 99.11838340759277 MB   | 29.65818500518799 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 99.11837005615234 MB   | 29.658204078674316 MB                 | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 99.11836528778076 MB   | 29.65818691253662 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 99.11836528778076 MB   | 29.658236503601074 MB                 | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 99.11837768554688 MB   | 29.6582088470459 MB                   | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 40.00229263305664 MB   | 29.070091247558594 MB                 | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 40.000550270080566 MB  | 29.069969177246094 MB                 | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 40.00164985656738 MB   | 29.070021629333496 MB                 | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 40.00048065185547 MB   | 29.06999397277832 MB                  | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 39.99679183959961 MB   | 29.069993019104004 MB                 | Max Compression Ratio |
| 50000       | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 40.00142860412598 MB   | 29.06989288330078 MB                  | Max Compression Ratio |
| 50000       | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 1.6037378311157227 MB  | 1.6037378311157227 MB                 | None                  |
| 50000       | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 1.6037378311157227 MB  | 1.6037378311157227 MB                 | None                  |
| 50000       | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 1.6037378311157227 MB  | 1.6037378311157227 MB                 | None                  |
| 50000       | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 1.6037378311157227 MB  | 1.6037378311157227 MB                 | None                  |
| 50000       | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 1.6037378311157227 MB  | 1.6037378311157227 MB                 | None                  |
| 50000       | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 1.6037378311157227 MB  | 1.6037378311157227 MB                 | None                  |
| 50000       | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 0.5769920349121094 MB  | 0.5769920349121094 MB                 | None                  |
| 50000       | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 0.5769920349121094 MB  | 0.5769920349121094 MB                 | None                  |
| 50000       | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 0.5769920349121094 MB  | 0.5769920349121094 MB                 | None                  |
| 50000       | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 0.5769920349121094 MB  | 0.5769920349121094 MB                 | None                  |
| 50000       | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 0.5769920349121094 MB  | 0.5769920349121094 MB                 | None                  |
| 50000       | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 0.5769920349121094 MB  | 0.5769920349121094 MB                 | None                  |
| 100000      | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | 103.65415573120117 MB  | 104.03614330291748 MB                 | Baseline              |
| 100000      | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | 103.65515041351318 MB  | 104.03122997283936 MB                 | Baseline              |
| 100000      | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | 103.65121841430664 MB  | 44.93772792816162 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | 103.6572618484497 MB   | 104.0381269454956 MB                  | Baseline              |
| 100000      | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | 103.65322208404541 MB  | 44.9373664855957 MB                   | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | 103.65467834472656 MB  | 44.95126819610596 MB                  | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | 47.97018241882324 MB   | 48.76775360107422 MB                  | Baseline              |
| 100000      | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | 47.97392272949219 MB   | 48.771817207336426 MB                 | Baseline              |
| 100000      | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | 47.970412254333496 MB  | 39.050241470336914 MB                 | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | 47.97272872924805 MB   | 48.76794242858887 MB                  | Baseline              |
| 100000      | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | 47.97314453125 MB      | 39.04986572265625 MB                  | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | 47.97404384613037 MB   | 39.05006408691406 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | 103.65542793273926 MB  | 94.17981719970703 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | 103.65390491485596 MB  | 94.10799407958984 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | 103.65701198577881 MB  | 20.51360034942627 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | 103.6555404663086 MB   | 94.1766586303711 MB                   | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | 103.65639209747314 MB  | 20.524049758911133 MB                 | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | 103.65402030944824 MB  | 20.53489398956299 MB                  | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | 2.630552291870117 MB   | 5.073307991027832 MB                  | Baseline              |
| 100000      | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | 2.6288795471191406 MB  | 5.079611778259277 MB                  | Baseline              |
| 100000      | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | 2.627203941345215 MB   | 10.074830055236816 MB                 | Baseline              |
| 100000      | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | 2.6386308670043945 MB  | 5.076205253601074 MB                  | Baseline              |
| 100000      | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | 2.6370601654052734 MB  | 10.055835723876953 MB                 | Baseline              |
| 100000      | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | 2.6386566162109375 MB  | 10.084415435791016 MB                 | Baseline              |
| 100000      | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 99.6485481262207 MB    | 32.41959190368652 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 99.64861011505127 MB   | 32.41955089569092 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 99.64859867095947 MB   | 32.41953182220459 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 99.64858722686768 MB   | 32.419554710388184 MB                 | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 99.6485948562622 MB    | 32.419554710388184 MB                 | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 99.64859104156494 MB   | 32.419569969177246 MB                 | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 41.86580467224121 MB   | 31.26191520690918 MB                  | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 41.86729621887207 MB   | 31.262118339538574 MB                 | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 41.86827373504639 MB   | 31.261953353881836 MB                 | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 41.865983963012695 MB  | 31.262131690979004 MB                 | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 41.86412048339844 MB   | 31.262248992919922 MB                 | Max Compression Ratio |
| 100000      | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 41.86623477935791 MB   | 31.26213836669922 MB                  | Max Compression Ratio |
| 100000      | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 2.991497039794922 MB   | 2.991497039794922 MB                  | None                  |
| 100000      | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 2.991497039794922 MB   | 2.991497039794922 MB                  | None                  |
| 100000      | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 2.991497039794922 MB   | 2.991497039794922 MB                  | None                  |
| 100000      | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 2.991497039794922 MB   | 2.991497039794922 MB                  | None                  |
| 100000      | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 2.991497039794922 MB   | 2.991497039794922 MB                  | None                  |
| 100000      | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 2.991497039794922 MB   | 2.991497039794922 MB                  | None                  |
| 100000      | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 1.015192985534668 MB   | 1.015192985534668 MB                  | None                  |
| 100000      | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 1.015192985534668 MB   | 1.015192985534668 MB                  | None                  |
| 100000      | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 1.015192985534668 MB   | 1.015192985534668 MB                  | None                  |
| 100000      | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 1.015192985534668 MB   | 1.015192985534668 MB                  | None                  |
| 100000      | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 1.015192985534668 MB   | 1.015192985534668 MB                  | None                  |
| 100000      | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 1.015192985534668 MB   | 1.015192985534668 MB                  | None                  |
| 1000000     | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Shuffled | 108.4235725402832 MB   | 109.2839527130127 MB                  | Baseline              |
| 1000000     | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Shuffled | 108.42037296295166 MB  | 109.28225994110107 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Shuffled | 108.42608547210693 MB  | 114.86360836029053 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Shuffled | 108.42116928100586 MB  | 109.28554725646973 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Shuffled | 108.4228401184082 MB   | 114.83678722381592 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Shuffled | 108.42262363433838 MB  | 98.17296409606934 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Shuffled | 54.05525779724121 MB   | 54.92994976043701 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Shuffled | 54.06032180786133 MB   | 54.93323040008545 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Shuffled | 54.05522060394287 MB   | 63.496527671813965 MB                 | Baseline              |
| 1000000     | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Shuffled | 54.05922603607178 MB   | 54.93170642852783 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Shuffled | 54.057308197021484 MB  | 63.49849987030029 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Shuffled | 54.05997371673584 MB   | 67.62064361572266 MB                  | Baseline              |
| 1000000     | UNCOMPRESSED  | 1 MB      | 1 MB           | Normal            | Sorted   | 108.42092704772949 MB  | 109.17657470703125 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 10 MB     | 1 MB           | Normal            | Sorted   | 108.4244794845581 MB   | 109.16957473754883 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 10 MB     | 10 MB          | Normal            | Sorted   | 108.42370796203613 MB  | 107.39879608154297 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 50 MB     | 1 MB           | Normal            | Sorted   | 108.42051029205322 MB  | 109.17083740234375 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 50 MB     | 10 MB          | Normal            | Sorted   | 108.4209337234497 MB   | 107.40631866455078 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 50 MB     | 50 MB          | Normal            | Sorted   | 108.4234733581543 MB   | 97.62663173675537 MB                  | Max Compression Ratio |
| 1000000     | ZSTD(Level 3) | 1 MB      | 1 MB           | Normal            | Sorted   | 14.100202560424805 MB  | 15.167672157287598 MB                 | Baseline              |
| 1000000     | ZSTD(Level 3) | 10 MB     | 1 MB           | Normal            | Sorted   | 14.116928100585938 MB  | 15.171114921569824 MB                 | Baseline              |
| 1000000     | ZSTD(Level 3) | 10 MB     | 10 MB          | Normal            | Sorted   | 14.103150367736816 MB  | 33.0784912109375 MB                   | Baseline              |
| 1000000     | ZSTD(Level 3) | 50 MB     | 1 MB           | Normal            | Sorted   | 14.101014137268066 MB  | 15.170759201049805 MB                 | Baseline              |
| 1000000     | ZSTD(Level 3) | 50 MB     | 10 MB          | Normal            | Sorted   | 14.101051330566406 MB  | 33.08864402770996 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 50 MB     | 50 MB          | Normal            | Sorted   | 14.10360336303711 MB   | 47.49184703826904 MB                  | Baseline              |
| 1000000     | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 104.41772747039795 MB  | 105.22843933105469 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 104.41773414611816 MB  | 105.22853755950928 MB                 | Baseline              |
| 1000000     | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 104.41774368286133 MB  | 56.29562950134277 MB                  | Max Compression Ratio |
| 1000000     | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 104.41773223876953 MB  | 105.2265214920044 MB                  | Baseline              |
| 1000000     | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 104.4177417755127 MB   | 56.29563045501709 MB                  | Max Compression Ratio |
| 1000000     | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 104.41772079467773 MB  | 56.29563522338867 MB                  | Max Compression Ratio |
| 1000000     | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Shuffled | 49.247376441955566 MB  | 50.12490367889404 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Shuffled | 49.24638843536377 MB   | 50.12476062774658 MB                  | Baseline              |
| 1000000     | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Shuffled | 49.2475061416626 MB    | 45.27782154083252 MB                  | Max Compression Ratio |
| 1000000     | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Shuffled | 49.24793338775635 MB   | 50.121745109558105 MB                 | Baseline              |
| 1000000     | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Shuffled | 49.24742889404297 MB   | 45.270514488220215 MB                 | Max Compression Ratio |
| 1000000     | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Shuffled | 49.24627208709717 MB   | 45.26921081542969 MB                  | Max Compression Ratio |
| 1000000     | UNCOMPRESSED  | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 97.6240291595459 MB    | 97.6240291595459 MB                   | None                  |
| 1000000     | UNCOMPRESSED  | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 97.6240291595459 MB    | 97.6240291595459 MB                   | None                  |
| 1000000     | UNCOMPRESSED  | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 54.85803699493408 MB   | 54.85803699493408 MB                  | None                  |
| 1000000     | UNCOMPRESSED  | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 97.6240291595459 MB    | 97.6240291595459 MB                   | None                  |
| 1000000     | UNCOMPRESSED  | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 54.85803699493408 MB   | 54.85803699493408 MB                  | None                  |
| 1000000     | UNCOMPRESSED  | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 54.85803699493408 MB   | 54.85803699493408 MB                  | None                  |
| 1000000     | ZSTD(Level 3) | 1 MB      | 1 MB           | RoundRobin        | Sorted   | 6.713757514953613 MB   | 6.713757514953613 MB                  | None                  |
| 1000000     | ZSTD(Level 3) | 10 MB     | 1 MB           | RoundRobin        | Sorted   | 6.713757514953613 MB   | 6.713757514953613 MB                  | None                  |
| 1000000     | ZSTD(Level 3) | 10 MB     | 10 MB          | RoundRobin        | Sorted   | 23.03006362915039 MB   | 23.03006362915039 MB                  | None                  |
| 1000000     | ZSTD(Level 3) | 50 MB     | 1 MB           | RoundRobin        | Sorted   | 6.713757514953613 MB   | 6.713757514953613 MB                  | None                  |
| 1000000     | ZSTD(Level 3) | 50 MB     | 10 MB          | RoundRobin        | Sorted   | 23.03006362915039 MB   | 23.03006362915039 MB                  | None                  |
| 1000000     | ZSTD(Level 3) | 50 MB     | 50 MB          | RoundRobin        | Sorted   | 23.03006362915039 MB   | 23.03006362915039 MB                  | None                  |
