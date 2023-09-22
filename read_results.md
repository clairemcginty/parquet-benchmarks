# Read Results

## Baseline

**These tests were run using a 0.14.0-SNAPSHOT build of parquet-mr w/ head commit 9b5a962df3007009a227ef421600197531f970a5, on a 64 GB M1 MBP, using openJDK 17.**

| Cardinality | Page Size | Compression  | Distribution | Sorting  | Dict-Encoded Cols                        | Extra Conf | Read Time            |
|-------------|-----------|--------------|--------------|----------|------------------------------------------|------------|----------------------|
| 1000        | 1 MB      | UNCOMPRESSED | Normal       | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 3.1453552791999995 s |
| 1000        | 1 MB      | ZSTD         | Normal       | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 3.101964468775 s     |
| 1000        | 10 MB     | UNCOMPRESSED | Normal       | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 3.00244832395 s      |
| 1000        | 10 MB     | ZSTD         | Normal       | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 3.14300040315 s      |
| 1000        | 1 MB      | UNCOMPRESSED | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.9167192198 s       |
| 1000        | 1 MB      | ZSTD         | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.039048254125 s     |
| 1000        | 10 MB     | UNCOMPRESSED | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.85890009475 s      |
| 1000        | 10 MB     | ZSTD         | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.955834794775 s     |
| 1000        | 1 MB      | UNCOMPRESSED | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 2.932544788475 s     |
| 1000        | 1 MB      | ZSTD         | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 3.0483232094000003 s |
| 1000        | 10 MB     | UNCOMPRESSED | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 2.8561283875 s       |
| 1000        | 10 MB     | ZSTD         | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 2.9447729698249994 s |
| 1000        | 1 MB      | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.8408431864250003 s |
| 1000        | 1 MB      | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.87584951665 s      |
| 1000        | 10 MB     | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.8434156051750006 s |
| 1000        | 10 MB     | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.7932324926749996 s |
| 5000        | 1 MB      | UNCOMPRESSED | Normal       | Shuffled | stringfield<br/>doublefield              | None       | 3.423857905566667 s  |
| 5000        | 1 MB      | ZSTD         | Normal       | Shuffled | stringfield<br/>doublefield              | None       | 3.5030458777333333 s |
| 5000        | 10 MB     | UNCOMPRESSED | Normal       | Shuffled | stringfield<br/>doublefield              | None       | 3.3572524083083337 s |
| 5000        | 10 MB     | ZSTD         | Normal       | Shuffled | stringfield<br/>doublefield              | None       | 3.626288109733333 s  |
| 5000        | 1 MB      | UNCOMPRESSED | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.99019390105 s      |
| 5000        | 1 MB      | ZSTD         | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.98612941565 s      |
| 5000        | 10 MB     | UNCOMPRESSED | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.0384285364250005 s |
| 5000        | 10 MB     | ZSTD         | Normal       | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.0738739677 s       |
| 5000        | 1 MB      | UNCOMPRESSED | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 2.9075526187500005 s |
| 5000        | 1 MB      | ZSTD         | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 2.9633675958500003 s |
| 5000        | 10 MB     | UNCOMPRESSED | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 3.0470641635500004 s |
| 5000        | 10 MB     | ZSTD         | RoundRobin   | Shuffled | stringfield<br/>doublefield<br/>intfield | None       | 3.142998322925 s     |
| 5000        | 1 MB      | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.87156700725 s      |
| 5000        | 1 MB      | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.902183349975 s     |
| 5000        | 10 MB     | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.02073387085 s      |
| 5000        | 10 MB     | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.0048125614249996 s |
| 50000       | 1 MB      | UNCOMPRESSED | Normal       | Shuffled |                                          | None       | 3.1306262489000005 s |
| 50000       | 1 MB      | ZSTD         | Normal       | Shuffled |                                          | None       | 3.1974337124999996 s |
| 50000       | 10 MB     | UNCOMPRESSED | Normal       | Shuffled |                                          | None       | 3.157759563525 s     |
| 50000       | 10 MB     | ZSTD         | Normal       | Shuffled |                                          | None       | 3.2348765697249995 s |
| 50000       | 1 MB      | UNCOMPRESSED | Normal       | Sorted   | stringfield<br/>doublefield              | None       | 3.1448123260250003 s |
| 50000       | 1 MB      | ZSTD         | Normal       | Sorted   | stringfield<br/>doublefield              | None       | 3.0642951646249994 s |
| 50000       | 10 MB     | UNCOMPRESSED | Normal       | Sorted   | stringfield<br/>doublefield              | None       | 3.119225705225 s     |
| 50000       | 10 MB     | ZSTD         | Normal       | Sorted   | stringfield<br/>doublefield              | None       | 3.0904492427249997 s |
| 50000       | 1 MB      | UNCOMPRESSED | RoundRobin   | Shuffled |                                          | None       | 3.2978042902749998 s |
| 50000       | 1 MB      | ZSTD         | RoundRobin   | Shuffled |                                          | None       | 3.2103571208250004 s |
| 50000       | 10 MB     | UNCOMPRESSED | RoundRobin   | Shuffled |                                          | None       | 3.160584253075 s     |
| 50000       | 10 MB     | ZSTD         | RoundRobin   | Shuffled |                                          | None       | 3.26184524795 s      |
| 50000       | 1 MB      | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.94739650525 s      |
| 50000       | 1 MB      | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.037496717675 s     |
| 50000       | 10 MB     | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.877026494725 s     |
| 50000       | 10 MB     | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.998386607325 s     |
| 100000      | 1 MB      | UNCOMPRESSED | Normal       | Shuffled |                                          | None       | 3.169464512525 s     |
| 100000      | 1 MB      | ZSTD         | Normal       | Shuffled |                                          | None       | 3.1833764676749996 s |
| 100000      | 10 MB     | UNCOMPRESSED | Normal       | Shuffled |                                          | None       | 3.1425801062749996 s |
| 100000      | 10 MB     | ZSTD         | Normal       | Shuffled |                                          | None       | 3.22435361865 s      |
| 100000      | 1 MB      | UNCOMPRESSED | Normal       | Sorted   |                                          | None       | 3.14020715105 s      |
| 100000      | 1 MB      | ZSTD         | Normal       | Sorted   |                                          | None       | 3.1190989896250003 s |
| 100000      | 10 MB     | UNCOMPRESSED | Normal       | Sorted   |                                          | None       | 3.06697292605 s      |
| 100000      | 10 MB     | ZSTD         | Normal       | Sorted   |                                          | None       | 3.0707962760500003 s |
| 100000      | 1 MB      | UNCOMPRESSED | RoundRobin   | Shuffled |                                          | None       | 3.216192071875 s     |
| 100000      | 1 MB      | ZSTD         | RoundRobin   | Shuffled |                                          | None       | 3.1829533895249997 s |
| 100000      | 10 MB     | UNCOMPRESSED | RoundRobin   | Shuffled |                                          | None       | 3.202778370541667 s  |
| 100000      | 10 MB     | ZSTD         | RoundRobin   | Shuffled |                                          | None       | 3.139665918775 s     |
| 100000      | 1 MB      | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.937549088575 s     |
| 100000      | 1 MB      | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.04919086245 s      |
| 100000      | 10 MB     | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 2.98824135625 s      |
| 100000      | 10 MB     | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.0012607020999993 s |
| 1000000     | 1 MB      | UNCOMPRESSED | Normal       | Shuffled |                                          | None       | 3.037158563625 s     |
| 1000000     | 1 MB      | ZSTD         | Normal       | Shuffled |                                          | None       | 3.120232870825 s     |
| 1000000     | 10 MB     | UNCOMPRESSED | Normal       | Shuffled |                                          | None       | 3.110930533275 s     |
| 1000000     | 10 MB     | ZSTD         | Normal       | Shuffled |                                          | None       | 3.2018961228750005 s |
| 1000000     | 1 MB      | UNCOMPRESSED | Normal       | Sorted   |                                          | None       | 3.0712404895499996 s |
| 1000000     | 1 MB      | ZSTD         | Normal       | Sorted   |                                          | None       | 3.0398329875250005 s |
| 1000000     | 10 MB     | UNCOMPRESSED | Normal       | Sorted   |                                          | None       | 3.0361092676749997 s |
| 1000000     | 10 MB     | ZSTD         | Normal       | Sorted   |                                          | None       | 3.0868807958250004 s |
| 1000000     | 1 MB      | UNCOMPRESSED | RoundRobin   | Shuffled |                                          | None       | 3.119673904175 s     |
| 1000000     | 1 MB      | ZSTD         | RoundRobin   | Shuffled |                                          | None       | 3.1824255832999997 s |
| 1000000     | 10 MB     | UNCOMPRESSED | RoundRobin   | Shuffled |                                          | None       | 3.09565449065 s      |
| 1000000     | 10 MB     | ZSTD         | RoundRobin   | Shuffled |                                          | None       | 3.246674213575 s     |
| 1000000     | 1 MB      | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.1447222677 s       |
| 1000000     | 1 MB      | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.1132453739500003 s |
| 1000000     | 10 MB     | UNCOMPRESSED | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.12168884895 s      |
| 1000000     | 10 MB     | ZSTD         | RoundRobin   | Sorted   | stringfield<br/>doublefield<br/>intfield | None       | 3.0693572770750004 s |
