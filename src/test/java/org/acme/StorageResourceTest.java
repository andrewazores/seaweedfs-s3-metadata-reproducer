package org.acme;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;

@QuarkusTest
@QuarkusTestResource(S3Storage.class)
class StorageResourceTest {

    static final String FILENAME = "file.txt";
    static final String API_PATH = String.format("/storage/%s", FILENAME);

    @Test
    void test() {
        given()
            .body(StorageResourceTest.class.getResourceAsStream("file.txt"))
            .when()
            .post(API_PATH)
            .then()
            .statusCode(204);

        given()
            .when()
            .get(API_PATH)
            .then()
            .body(Matchers.equalTo("some file content" + System.lineSeparator()))
            .statusCode(200);

        given()
            .when()
            .get(API_PATH + "/meta")
            .then()
            .body(Matchers.equalTo("[Tag(Key=hello, Value=world)]"))
            .statusCode(200);

        given()
            .when()
            .delete(API_PATH)
            .then()
            .statusCode(204);
    }
}
