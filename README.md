# Genevere

Genevere is a generic ETL data pump written in Java designed to make it fast and easy to perform
simple transfers of data between a source and a target.

The problems it is designed to solve:

* In languages such as python, the drivers are often riddled with performance issues,
  incompatibilities and installation issues. Some drivers do not implement BULK inserts,
  which are absolute musts for high performance processing.
* Java has great database drivers for any vendor/platform.
* Java is optimized and for small programs, the JIT compiler makes the code as performant
  as native code and sometimes even better. This is a great feature for loops you'd typically
  have in ETL.
* The code is made extensible for customizable transforms beyond the available ones or to make
  new source readers or target writers.
* High performance data transfers through the use of record batching at source and target.
* More dynamic languages like Python are great writing generalized code to extract data, but less great
  for having good performance in data transfers. So the idea is to continue to use more dynamic languages to
  generate the SQL to read and other parameters, but executing the actual transfer using Java if performance
  is quickly becoming a concern.
* I've seen code where local files had to be generated to be able to transfer data using binary custom tools for
  a source or target system. Local files can become rather large and use a lot of local disk I/O, which can have
  bandwidth limitations. So the objective is to always transfer records in batches kept in memory.

### Examples

There are some example files here:

https://github.com/gtoonstra/genevere/tree/master/examples

### Design

1. The design is created around the problem of "How do I get data from system A into system B as fast
and easy as possible with as little effort as possible?"

2. For this, we analyze specific, typical operations in the code and design abstractions around them,
so that we can generalize around them. This allows us to extend the library easily in the future.

We can identify:

* A class for connecting to a remote system to read data (reader), pushing out records in batches of a configured size,
  driven by custom input parameters.
* A class that knows about file formats if so required and can read and convert from a local file.
* A list of "transform" classes which takes a record and returns a transformed record.
* A class that can take the generalized data and convert that into a target file format.
* A class for connecting to a remote system to write data (writer), writing records in batches of a configured size and
  committing the data at configured intervals if it is possible to do with that system.
* Allow for easy extension by utilizing interfaces and dynamic instantiation of classes.

3. The process is driven by a single command file, which configures the pipeline and then runs it. 

4. For security, credentials for both systems are picked up from 4 environment variables.

In Python, you'd typically connect to both systems, figure out what work needs to be done and then
write command file(s) to run the pipelines.
