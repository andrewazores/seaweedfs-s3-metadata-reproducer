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
            .log()
            .all()
            .body(StorageResourceTest.class.getResourceAsStream(FILENAME))
            .when()
            .post(API_PATH)
            .then()
            .log()
            .all()
            .statusCode(204);

        given()
            .log()
            .all()
            .when()
            .get(API_PATH)
            .then()
            .log()
            .all()
            .body(Matchers.equalTo("some file content" + System.lineSeparator()))
            .statusCode(200);

        given()
            .log()
            .all()
            .when()
            .get(API_PATH + "/meta")
            .then()
            .log()
            .all()
            .body(Matchers.equalTo("{hello=world}"))
            .statusCode(200);

        given()
            .log()
            .all()
            .when()
            .delete(API_PATH)
            .then()
            .log()
            .all()
            .statusCode(204);
    }
}
