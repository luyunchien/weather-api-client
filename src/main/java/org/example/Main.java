package org.example;

import com.google.gson.Gson;
import lombok.ToString;
import org.apache.http.HttpEntity;
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
    public static void main(String[] args) throws Exception{
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //getWeather("a", 5, httpClient);
            List<WeatherInfo> data = getAllWeather("a", httpClient);
            System.out.println(data.size());
            for(WeatherInfo info : data){
                System.out.println(info.name);
            }
        }

    }

    public static List<WeatherInfo> getAllWeather(String search, CloseableHttpClient httpClient) throws Exception{
        int page = 1;
        List<WeatherInfo> data = new ArrayList<>();
        while (true){
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
        System.out.println(uri);

        HttpGet request = new HttpGet(uri);
        try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
            System.out.println(httpResponse.getStatusLine().getStatusCode());
            HttpEntity entity = httpResponse.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);

            Gson gson = new Gson();
            WeatherResponse weatherResponse = gson.fromJson(result, WeatherResponse.class);
            System.out.println(weatherResponse);
            return weatherResponse;
        }
    }

    @ToString
    static class WeatherResponse{
        int page;
        int per_page;
        int total;
        int total_pages;
        List<WeatherInfo> data;
    }

    @ToString
    static class WeatherInfo{
        String name;
        String weather;
        List<String> status;
    }
}

