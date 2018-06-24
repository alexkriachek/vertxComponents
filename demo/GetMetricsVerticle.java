package demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class GetMetricsVerticle extends AbstractVerticle {

    private MongoClient mongoClient;

    @Override
    public void start() throws Exception {
        mongoClient = MongoClient.createShared(vertx, new JsonObject().put("db_name", "vertx"));
        vertx.createHttpServer().requestHandler(request -> {
            mongoClient.find("metrics", new JsonObject(),handler-> {
                request.response().putHeader("content-type", "text/plain")
                        .end(handler.result().toString());
            });
        }).listen(8081);
    }
}
