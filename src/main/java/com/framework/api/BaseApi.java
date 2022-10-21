package com.framework.api;

import com.cheney.central.config.Constants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseApi {
    private RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    private MethodType method;
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public MethodType getMethod() {
        return this.method;
    }

    public void setMethod(MethodType method) {
        this.method = method;
    }

    public RequestSpecBuilder getRequestSpecBuilder() {
        return this.requestSpecBuilder;
    }

    public void setRequestSpecBuilder(RequestSpecBuilder requestSpecBuilder) {
        this.requestSpecBuilder = requestSpecBuilder;
    }

    public Response execute(MethodType method) {
        RestAssured.defaultParser = Parser.JSON;
        RequestSpecification requestSpecification = getRequestSpecification();
        switch (method) {
            case GET:
                response = requestSpecification.when().get();
                break;
            case POST:
                response = requestSpecification.when().post();
                break;
            case PUT:
                response = requestSpecification.when().put();
                break;
            case DELETE:
                response = requestSpecification.when().delete();
                break;
            default:
                throw new RuntimeException("API method not specified");
        }
        this.printResponse(response);
        setResponse(response);
        return response;
    }

    private RequestSpecification getRequestSpecification() {
        return RestAssured
                .given()
                .config(RestAssured.config().sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .spec(getRequestSpecBuilder().setBaseUri(Constants.host).build());
    }

    private void printResponse(Response response) {
        System.out.println("*********** Response *******");
        response.prettyPrint();
    }

    public BaseApi setBody(Object obj) {
        this.requestSpecBuilder.setBody(obj);
        return this;
    }

    public boolean validateStatus(int status){
        return getResponse().statusCode() == status;
    }

}