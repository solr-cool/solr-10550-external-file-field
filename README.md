# Proof of concept for fixing Solr External File Fields

This is a proof of concept for overhauling the Solr External File Field. Main goal
is to speed up reading and matching external file field definitons.

Related issues are:

* [`SOLR-10550`](https://issues.apache.org/jira/browse/SOLR-10550)
* [`SOLR-2583`](https://issues.apache.org/jira/browse/SOLR-2583)

## Building the project

If you have [Apache Maven](https://maven.apache.org/download.cgi) installed, run:

```shell
$ mvn clean verify -DskipTests=true
```

If you have Docker installed, run:

```shell
$ docker run -it \
    -v $(pwd):$(pwd) -w $(pwd) \
    maven:3.8.4-jdk-11 mvn clean verify
```

### Patching the target Solr

Copy all files from `target/classes` into your Solr installation's
`solr-webapp/webapp/WEB-INF/classes` directory. and
restart Solr.

## Testing the patch

> 😟 This is the rough part

1. First, build a patched Solr Docker version

```bash
$ mvn clean package -DskipTests=true && \
    docker build -t solr:8.11.1-patched .
```

2. Then, open a second terminal and launch the patched Solr

```bash
$ docker run --rm -it -p 8983:8983 \
    -v $(pwd)/target/solr:/var/solr/data \
    solr:8.11.1-patched
```

3. Afterwards, create a new core named `eff-test`

```bash
curl "http://localhost:8983/solr/admin/cores?action=CREATE&name=eff-test&instanceDir=eff-test&config=solrconfig.xml&dataDir=data"
```
4. Create some random boosts by executing the `ExternalFileFieldIntegrationTest#generateBoosts` JUnit test.
5. Load some 8.8M documents by executing the `ExternalFileFieldIntegrationTest#createTestDocuments` JUnit test.

On my M1 Mac with NVMe SSD discs, the process of loading took 30s in unpatched, sequential loading.
With the patched, parallel loading this takes about 15s.
