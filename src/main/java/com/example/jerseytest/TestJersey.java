package com.example.jerseytest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.Instant;

public class TestJersey {
    public static void main(String[] args) throws Exception {
        Client HTTP = ClientBuilder.newClient();
        WebTarget target = null;
        Response lastResponse;
        int failure = 0;
        int success = 0;
        Instant start = Instant.now();
        Instant logged = start;
        target = HTTP.target("https://example.com/api");

        while (Duration.between(start, Instant.now()).toMillis() <= 10_000)  {

//            Invocation.Builder request = target.request().header("Accept", "application/json").header("X-Shopify-Access-Token", "d6f6fb322d3c27ebcbc344a14f23b113");
            Invocation.Builder request = target.request().header("Authorization", "Bearer CJfR_5PnLRID34cCGLnmHSDwtooDKLbnCjIZAAamUHWJVFB_iwDzK95euno5hOgSExFI-ToRAH9nx_8HDIQ_oPv_4P99TwhCGQAGplB1dfgqhMVSV3Isvt5MTdOf_-HGcs4");
            lastResponse = request.get();

            if (lastResponse == null || !lastResponse.getStatusInfo().equals(Response.Status.OK)) {
                failure++;
                continue;
            }
            lastResponse.readEntity(String.class);
            success++;
            long runTime = Duration.between(logged, Instant.now()).toMillis();

            if (runTime >= 1_000) {
                float successRate = ((float) success) / ((float) runTime) * 1_000;
                float failureRate = ((float) failure) / ((float) runTime) * 1_000;
                System.out.print(String.format("%.02f\t%.02f\n", successRate, failureRate));
                success = 0;
                failure = 0;
                logged = Instant.now();
            }

//            Thread.sleep(1_000);
        }
    }
}
