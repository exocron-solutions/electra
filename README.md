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

# Concept
Our database was planned to proof the concept of an indexed key value data storage. It was planned to
maximize performance and speed while being as lightweight and easy to use as possible.

## File Formats
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

## Caching Lifecycle

## Algorithms


