package com.luyunchien.weather;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException("Usage: weather.jar SEARCH_QUERY");
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            List<WeatherInfo> data = getAllWeather(args[0], httpClient);
            System.out.println(String.format("Found %d results", data.size()));
            for(WeatherInfo info : data){
                System.out.println(info.toString());
            }
        }
    }

    public static List<WeatherInfo> getAllWeather(String search, CloseableHttpClient httpClient) throws Exception{
        int page = 1;
        List<WeatherInfo> data = new ArrayList<>();
        while (true) {
            WeatherResponse response = getWeather(search, page, httpClient);
            if(response.data.isEmpty()) break;
            data.addAll(response.data);
            page++;
        }
        return data;
    }

    public static WeatherResponse getWeather(String search, int page, CloseableHttpClient httpClient) throws Exception{
        URIBuilder builder = new URIBuilder("https://jsonmock.hackerrank.com/api/weather/search");
        builder.addParameter("name", search);
        builder.addParameter("page", String.valueOf(page));
        URI uri = builder.build();

        HttpGet request = new HttpGet(uri);
        try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException(String.format("Unexpected status code: %d", statusCode));
            }
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);

            Gson gson = new Gson();
            WeatherResponse weatherResponse = gson.fromJson(result, WeatherResponse.class);
            return weatherResponse;
        }
    }

    @SuppressWarnings("unused")
    static class WeatherResponse{
        int page;
        int per_page;
        int total;
        int total_pages;
        List<WeatherInfo> data;
    }

    static class WeatherInfo{
        String name;
        String weather;
        List<String> status;

        @Override
        public String toString() {
            return String.format("City = %s; Weather = %s; Status = %s", name, weather, status);
        }
    }
}

