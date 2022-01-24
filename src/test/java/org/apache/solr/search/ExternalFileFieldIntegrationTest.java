package org.apache.solr.search;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.curator.shaded.com.google.common.io.Files;
import org.junit.jupiter.api.Test;

public class ExternalFileFieldIntegrationTest {

    public static int NUM_DOCS = 8_800_000;
    public static int NUM_BOOSTS = 4_400_000;

    @Test
    public void createTestDocuments() throws Exception {
        // generate shuffled ids
        List<String> idList = LongStream.range(1, 8_800_000 + 1)
                .mapToObj(id -> String.valueOf(id))
                .collect(Collectors.toList());
        Collections.shuffle(idList);

        // post ids to Solr
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8983/solr/eff-test/update/csv?fieldnames=id"))
                .header("Content-type", "application/csv")
                .POST(HttpRequest.BodyPublishers.ofString(idList.stream()
                        .collect(Collectors.joining("\n")), StandardCharsets.UTF_8))
                .build();
        HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
        HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8983/solr/eff-test/update?commit=true"))
                .GET()
                .build(), BodyHandlers.ofString());

    }

    @Test
    public void generateBoosts() throws Exception {
        // generate boosts
        generateBoostsFor("first");
        generateBoostsFor("second");
        generateBoostsFor("third");
    }

    private void generateBoostsFor(String name) throws IOException {
        Random r = new Random();
        long start = r.nextInt(4_400_000 - 1);
        long end = start + 4_400_000;
        String boosts = LongStream.range(start, end)
                .mapToObj(id -> Map.entry(id, (r.nextFloat() * 4) + 1.45f))
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.joining("\n"));
        File boostFile = new File("./target/solr/eff-test/data/external_" + name);
        Files.write(boosts, boostFile, StandardCharsets.UTF_8);
    }

}
