[![Build Status](https://travis-ci.com/FelixKlauke/electra.svg?token=v7R4FVyUfBVWw8zFwc5F&branch=master)](https://travis-ci.com/FelixKlauke/electra)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/FelixKlauke/electra)

# electra

# Usage
- Install [Maven](http://maven.apache.org/download.cgi)
- Clone this repo
- Install: ```mvn clean install```

**Maven dependencies**

_Electra Client:_
```xml
<dependency>
    <groupId>io.electra</groupId>
    <artifactId>electra-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

# Benchmarks & Performance

# Concept
Our database was planned to proof the concept of an indexed key value data storage. It was planned to
maximize performance and speed while being as lightweight and easy to use as possible.

## File Formats
We organized our files in block formats with a fixed size.

### Index File

### Data File

## Data Record

### Index

### Data

## Caching Lifecycle
We organized our files in block formats with a fixed size. There is one file that will store the indices and one
file that will store the corresponding data.

### Index File
One Block is organized as:
```
{
    keyHash:  4 bytes
    isEmpty:  1 byte
    position: 4 bytes
}
```

As you can see one index will be stored using 9 bytes. The first 4 bytes will give the key hash of the data record
this index wants us to know. The isEmpty flags tells us about the state of the index. If it is empty it could be
overwritten with a new index at any time because the old index got deleted.

#### Empty data index
One special index ist the "first empty data index". It is always stored as the first (0) position in the index file and
the stored index will point to the first empty block in the data file.

### Data File
One Block is organized as:
```
{
    nextPosition: 4 bytes
    contentLength: 4 bytes
    content: 'contentLength bytes' (1-120)
}
```

The data blocks have a fixed size of 128 bytes. The first 4 bytes will point to the next position of the data record
or to -1 if this is the only block of the record. The following 4 bytes will tell us about the length of the following
content, which is the actual (part of the) data of the record.

## Data Record
When you look at the index and data format you could predict how a data record is built. One data record is built
of an index and one or more data blocks. The index will give information about the first data block. The data block
will point to the next data block or to itself. That results in a linked list of data blocks.

### Index


### Data
Imagine that the data file would look the following, when X means the block is filled with data and O means the block
is empty:
```
+----------------+---+---+---+---+---+---+---+---+---+
| Block Position | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 |
+----------------+---+---+---+---+---+---+---+---+---+
| Data           | X | O | O | X | X | X | O | X | 0 |
+----------------+---+---+---+---+---+---+---+---+---+
```

When you would have the following data records:
- [ key: key1, contentLength: 3,   content: <content> ]
- [ key: key2, contentLength: 300, content: <content> ]
- [ key: key3, contentLength: 56,  content: <content> ]

Then the data could be organized this way:
```
+----------------+------+---+---+------+---+----+---+------+---+
|     Record     | key1 |   |   | key2 |   |    |   | key3 |   |
+----------------+------+---+---+------+---+----+---+------+---+
| Block Position | 0    | 1 | 2 | 3    | 4 | 5  | 6 | 7    | 8 |
+----------------+------+---+---+------+---+----+---+------+---+
| Data           | X    | O | O | X    | X | X  | O | X    | 0 |
+----------------+------+---+---+------+---+----+---+------+---+
```

Which means the next positions would be set to:
```
+----------------+------+---+---+------+---+----+---+------+---+
|     Record     | key1 |   |   | key2 |   |    |   | key3 |   |
+----------------+------+---+---+------+---+----+---+------+---+
| Block Position | 0    | 1 | 2 | 3    | 4 | 5  | 6 | 7    | 8 |
+----------------+------+---+---+------+---+----+---+------+---+
| Data           | X    | O | O | X    | X | X  | O | X    | 0 |
+----------------+------+---+---+------+---+----+---+------+---+
| Block pointer  | -1   |   |   | 4    | 5 | -1 |   | -1   |   |
+----------------+------+---+---+------+---+----+---+------+---+
```

Actually the record would look like this when you insert the empty block chain:
```
+----------------+------+---+---+------+---+----+---+------+----+
|     Record     | key1 |   |   | key2 |   |    |   | key3 |    |
+----------------+------+---+---+------+---+----+---+------+----+
| Block Position | 0    | 1 | 2 | 3    | 4 | 5  | 6 | 7    |  8 |
+----------------+------+---+---+------+---+----+---+------+----+
| Data           | X    | O | O | X    | X | X  | O | X    |  0 |
+----------------+------+---+---+------+---+----+---+------+----+
| Block pointer  | -1   | 2 | 6 | 4    | 5 | -1 | 8 | -1   | -1 |
+----------------+------+---+---+------+---+----+---+------+----+
```

In this case we would have the following chains:
```
+--------------------------------------+------------------+
|                Index                 |      Chain       |
+--------------------------------------+------------------+
| [keyHash: -1, empty: 1, position: 1] | 1 => 2 => 6 => 8 |
+--------------------------------------+------------------+
| [keyHash:  1, empty: 0, position: 0] | 0                |
| [keyHash:  2, empty: 0, position: 3] | 3 => 4 => 5      |
| [keyHash:  3, empty: 0, position: 7] | 7                |
+--------------------------------------+------------------+
```

Now we have a LinkedList of blocks for each record and the list for empty blocks :)

## Caching Lifecycle
Of course we try to minimize the I/O operations and only read from disk when it really has to be. The whole caching
consists of four caches:
- BlockCache
- BlockChainCache
- IndexCache
- DataCache

### BlockCache
To prevent data reading from disk all the time when we want to read the same data and also to have changes in memory
while they are still in process to be written to the disk, this cache provides all currently loaded and all
recently accessed data blocks. It contains the content of a block keyed by its position in the data file.

### BlockChainCache
We want to build the block chains fast. Really fast. That is why we have an own cache for the next blocks. This cache
contains all links between two data blocks. The key is the 'source' and the value is the 'target'.

### IndexCache
The index cache is maybe not even a cache. It holds all indices in memory because we can't risk to read it from disk.
The organization of the index cache can differ. We're doing experiments will B+ trees, koloboke's enhanced maps
and other data structures. The main goal is to store the index keyed by its key hash.

### DataCache
The data cache is 'the highest cache'. It operates before any CRUD operation in the database. When you are saving a new
value in the database, the cache will register this value. If you query for a value the data cache will be consulted
first. If you try to delete data it will be invalidated in this cache. It's like a top level cache that operates mainly
in runtime to make the data you save accessible in memory. But we can't hold all data you save in memory so this cache
will let all values expire one minute after they were written into the cache.

## Algorithms
In the following we will try to explain our central repositories needed to organize our data.

### Saving
Imagine having the following data structure:
```
+-------------+---+---+---+---+---+---+---+---+---+---+----+----+----+
| Block Index | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 |
+-------------+---+---+---+---+---+---+---+---+---+---+----+----+----+
| Data        | X | X | O | X | X | X | O | O | X | O | X  | X  | O  |
+-------------+---+---+---+---+---+---+---+---+---+---+----+----+----+
```

If you want to insert a new data record now you will have to:
- Calculate how many blocks are needed for the data you want to save
- Allocate the calculated amount of blocks
- Split the data onto the blocks
- Write the data into the blocks
- Create and save a new index that is pointing to the first data block

Lets assume we would want to save data that would need three blocks blocks. The next step is to allocate the amount of
blocks. In this case these would result in the blocks 2, 6 and 7. Now we can split our data into pieces and write them
in the blocks. At last we would create the new index that points to the data block 2.

If we look at the next block pointers our table should look like this:
```
+-------------+---+---+---+---+---+----+---+----+----+----+----+----+----+
| Block Index | 0 | 1 | 2 | 3 | 4 | 5  | 6 | 7  | 8  | 9  | 10 | 11 | 12 |
+-------------+---+---+---+---+---+----+---+----+----+----+----+----+----+
| Data        | X | X | X | X | X | X  | X | X  | X  | O  | X  | X  | O  |
| Next Block  | 0 | 1 | 6 | 4 | 5 | -1 | 7 | -1 | -1 | 12 | 11 | -1 | -1 |
+-------------+---+---+---+---+---+----+---+----+----+----+----+----+----+
```

At this time the empty data index should point at 9.

#### Free Block allocation


#### Data splitting

### Updating

### Querying

### Deleting
