package io.vertx.example.jsonschema;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.*;

public class ValidateJson {

  public static String BASE_DIRECTORY = "io/vertx/example/jsonschema/";

  /**
   *
   * @param schema, the JsonSchema that you want to use with your repository.
   * @return a SchemaRepository which is used to validate json.
   */
  public static SchemaRepository createSchemaRepository(JsonSchema schema) {
    return SchemaRepository.create(new JsonSchemaOptions()
        // Set the base URI for all schemas, incase the schema you load doesn't contain one by default.
        .setBaseUri("https://vertx.io")
        // Set the json schema draft that you want to use for validation.
        .setDraft(Draft.DRAFT202012)
        // Set the output format when validating json objects.
        .setOutputFormat(OutputFormat.Basic))
      //dereference the schema so that it is ready to be used later on and referenced by the Schema URI.
      .dereference(schema);
  }

  /**
   *
   * @param vertx instance
   * @param fileName that is loaded into a JsonSchema
   */
  public static Future<JsonSchema> loadSchema(Vertx vertx, String fileName) {
    return loadJsonFromFile(vertx, BASE_DIRECTORY + fileName)
      .compose(json -> Future.succeededFuture(JsonSchema.of(json)));
  }

  /**
   *
   * @param vertx instance
   * @param filePath that is loaded into a JsonObject
   */
  public static Future<JsonObject> loadJsonFromFile(Vertx vertx, String filePath) {
    return vertx.fileSystem().readFile(filePath)
      .compose(buffer -> Future.succeededFuture(buffer.toJsonObject()));
  }

  /**
   *
   * @param repository The SchemaRepository that the json object will be validated against.
   * @param schema The schema that is used for validation
   * @param json to validate
   * @return an OutputUnit on whether the validation succeeded or failed.
   */
  public static OutputUnit validateJson(SchemaRepository repository, String schemaUri, Object json) {
    return repository.validator(schemaUri).validate(json);
  }


  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    loadSchema(vertx, "basic_json_schema.json")
      .onSuccess(jsonSchema -> {
        System.out.println("Successfully loaded json schema.");
        SchemaRepository repository = createSchemaRepository(jsonSchema);
        System.out.println("Successfully loaded and dereferenced the json schema repository.");
        loadJsonFromFile(vertx, BASE_DIRECTORY + "/basic_json.json")
          .onSuccess(jsonObject -> {

            //Since we previously dereferenced the basic json schema, we can just use the $id defined the
            // schema to validate the json object provided.
            OutputUnit outputUnit = validateJson(repository, "https://vertx.io/basic.json", jsonObject);
            System.out.println("Json validity: " + outputUnit.getValid() + " errors: " + outputUnit.getErrors());
            System.exit(0);

          })
          .onFailure(err -> {
            err.printStackTrace();
            System.exit(1);
          });
      })
      .onFailure(err -> {
        err.printStackTrace();
        System.exit(1);
      });
  }

}
