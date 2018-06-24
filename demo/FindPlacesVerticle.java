package demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FindPlacesVerticle extends AbstractVerticle {

    private String API_KEY = "AIzaSyAzx0lnq4ob2aNGYEQeLD_-CaPpDgjAgwE";

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> {
            String country = req.getParam("country");
            String userText = req.getParam("user_text");
            JsonArray places = findPlaces(country, userText);
            vertx.eventBus().publish("requests", new JsonObject().put("country", country));
            req.response().putHeader("content-type", "text/plain")
                    .end(places.toString());
        }).listen(8080);
    }

    private JsonArray findPlaces(String country, String userText) {
        String urlToRead = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                + userText + "&components=country:" + country + "&key=" + API_KEY;
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        StringBuilder result = new StringBuilder();

        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject resultJson = new JsonObject(result.toString());
        return resultJson.getJsonArray("predictions");
    }
}
