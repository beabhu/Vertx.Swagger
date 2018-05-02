package rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import model.Article;
import openapi.OpenApiRoutePublisher;

public class RestServiceVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> future) {

        Router router = Router.router(vertx);
        router.get("/api/baeldung/articles/article/:id")
                .handler(this::getArticles);
        router.post("/api/baeldung/articles/hello")
                .handler(this::postArticles);
        OpenApiRoutePublisher.publishOpenApiSpec(router, "/spec",
                "Vertx swagger open api generation", "1.0.0", "http://" + "localhost" + ":" + "8080" + "/");
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080), result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }
    @Operation(summary = "Provides status information for a specific upload.", method = "GET",
            responses = {
                    @ApiResponse(responseCode = "404", description = "Upload unit of work not found."),
                    @ApiResponse(responseCode = "200", description = "Upload unit of work found.",
                            content = @Content(
                                    schema = @Schema(implementation = Article.class),
                                    examples = {@ExampleObject (name="kri", summary="here you go", value = "{\n" +
                                            "  \"id\" : \"1\",\n" +
                                            "  \"content\" : \"This is an intro to vertx\",\n" +
                                            "  \"author\" : \"baeldung\",\n" +
                                            "  \"datePublished\" : \"01-02-2017\",\n" +
                                            "  \"wordCount\" : 1578\n" +
                                            "}")}),
                            headers = {@Header(name = "Upload-Length", description = "The total length of the upload unit of work.", required = true),
                                    @Header(name = "Upload-Offset", description = "How many bytes have been uploaded so far.", required = true)})},
            parameters = {@Parameter(in = ParameterIn.PATH, name = "uploadID",
                    required = true, description = "The ID of the upload unit of work", schema = @Schema(type = "string", format = "uuid"))})
    private void getArticles(RoutingContext routingContext) {
        System.out.println("inside get article");
        String articleId = routingContext.request()
                .getParam("id");
        Article article = new Article(articleId, "This is an intro to vertx", "baeldung", "01-02-2017", 1578);

        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(200)
                .end(Json.encodePrettily(article));
    }

    @Operation(summary = "Provides post information.", method = "POST")
    private void postArticles(@RequestBody(description = "Created user object", required = true,
            content = @Content(
                    schema = @Schema(implementation = Article.class),
                    examples = {@ExampleObject (name="kri", summary="here you go", value = "{\n" +
                            "  \"id\" : \"1\",\n" +
                            "  \"content\" : \"This is an intro to vertx\",\n" +
                            "  \"author\" : \"baeldung\",\n" +
                            "  \"datePublished\" : \"01-02-2017\",\n" +
                            "  \"wordCount\" : 1578\n" +
                            "}")})) RoutingContext routingContext) {
        System.out.println("inside post article");
        routingContext.response()
                .putHeader("content-type", "application/json")
                .setStatusCode(200)
                .end(Json.encodePrettily("33"));
    }
}