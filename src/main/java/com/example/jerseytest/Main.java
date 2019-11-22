package com.example.jerseytest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class Main {

   public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
        String url = args.length > 0 ? args[0] :"http://ec2-54-160-4-184.compute-1.amazonaws.com:89/example.json";
        System.out.println("success\tfail - " + url);
        int success = 0, failure = 0;
        Instant start = Instant.now(), logged = Instant.now();

        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
        }}, new java.security.SecureRandom());

        Client client = ClientBuilder.newBuilder()
                .sslContext(sslcontext)
                .hostnameVerifier((s1, s2) -> true)
                .build();

        int i = 0;
        int seq = -1;
        while (Duration.between(start, Instant.now()).toMillis() < 600_000) {
            WebTarget target = client.target(url + "/" + i++);
            Response resp = target.request().get();
            if (resp.getStatusInfo().equals(Response.Status.OK)) {
                if (!target.toString().contains("/fail.json/"))
                    resp.bufferEntity();
                if (target.toString().contains("/example.json/")) {
                    FooBar rec = resp.readEntity(new GenericType<FooBar>() {});
                    if (seq == -1)
                        seq = rec.foo;
                    if (seq++ == rec.foo)
                        success++;
                    else
                        failure++;
                }
                else {
                    resp.readEntity(new GenericType<EmptyBean>() {});
                    success++;
                }
            }
            else {
                failure++;
            }
            if (Duration.between(logged, Instant.now()).toMillis() > 1_000) {
                Duration runTime = Duration.between(logged, Instant.now());
                System.out.println(String.format("%7d\t%4d", success, failure));
                success = 0;
                logged = Instant.now();
            }
        }
    }

    static class FooBar {
        public int foo, bar;
    }
}