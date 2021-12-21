# Proof of concept for [`SOLR-10550`](https://issues.apache.org/jira/browse/SOLR-10550)

This is a proof of concept for a memory leak in the `ExternalFileField` as described in
[`SOLR-10550`](https://issues.apache.org/jira/browse/SOLR-10550). This repository contains
patched files as a drop-in replacement in Solr.

## Setup

If you have [Apache Maven](https://maven.apache.org/download.cgi) installed, run:

```shell
mvn clean verify
```

If you have Docker installed, run:

```shell
docker run -it -v $(pwd):$(pwd) -w $(pwd) maven:3.8.4-jdk-11 mvn clean verify
```

## Patching the target Solr

Copy all files from `target/classes` into your Solr installation's
`solr-webapp/webapp/WEB-INF/classes` directory. You could also build
a patched Docker image:

```shell
docker build -t solr:8.11.1-patched .
docker run -p 8983:8983 solr:8.11.1-patched
```
