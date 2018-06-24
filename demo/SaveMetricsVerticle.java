package demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class SaveMetricsVerticle extends AbstractVerticle {

    private MongoClient client;

    @Override
    public void start() throws Exception
    {
        client = MongoClient.createShared(vertx, new JsonObject().put("db_name", "vertx"));
        vertx.eventBus().consumer("requests", this::updateMetrix);
    }

    private void updateMetrix(Message<Object> objectMessage) {

        JsonObject body = (JsonObject) objectMessage.body();
        String country = body.getString("country");

        updateCounter("TotalNumberOfRequests");
        updateCounter(country);
    }

    private void updateCounter (String coutnry) {
        JsonObject jsonObject = new JsonObject()
                .put("country", coutnry);

        client.find("metrics", jsonObject, handler->{
            if (handler.result().isEmpty()) {
                jsonObject.put("counter", 1);
            } else {
                jsonObject.put("_id", handler.result().get(0).getValue("_id"));
                jsonObject.put("counter", handler.result().get(0).getInteger("counter") + 1);
            }
            client.save("metrics", jsonObject, stringAsyncResult -> {
            });
        });
    }
}
