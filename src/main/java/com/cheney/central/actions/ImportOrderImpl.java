package com.cheney.central.actions;

import com.cheney.central.config.Constants;
import com.cheney.central.entities.Cart;
import com.cheney.central.entities.Credential;
import com.cheney.central.entities.Token;
import com.cheney.central.entities.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.api.BaseApi;
import com.framework.api.MethodType;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.log4j.Logger;

import java.util.Map;

public class ImportOrderImpl implements ImportOrder {
    BaseApi baseApi = new BaseApi();
    ObjectMapper om = new ObjectMapper();

    private static final Logger logger = Logger.getLogger(ImportOrderImpl.class);
    private String access_token;


    @Override
    public Token token(Credential credentials) {
        Token tk = null;
        baseApi.getRequestSpecBuilder().
                setContentType(ContentType.URLENC.withCharset("UTF-8")).
                addFormParams(om.convertValue(credentials, Map.class)).
                setBasePath("/token");
        String tJson = baseApi.
                execute(MethodType.POST).
                asString();
        baseApi.validateStatus(Constants.success);

        try {
            tk = om.readValue(tJson, Token.class);
        } catch (Exception ex) {
            logger.error("login failed");
            ex.printStackTrace();
        }
        access_token = tk.getAccess_token();
        return tk;
    }

    @Override
    public UserInfo getUserInfo() {
        UserInfo ui = null;
        baseApi.getRequestSpecBuilder().
                addHeader("Authorization", access_token).
                setBasePath("/api/Account/UserInfo");
        String uJson = baseApi.
                execute(MethodType.GET).asString();
        baseApi.validateStatus(Constants.success);

        try {
            ui = om.readValue(uJson, UserInfo.class);
        } catch (Exception ex) {
            logger.error("user info fetch failed");
            ex.printStackTrace();
        }
        return ui;
    }

    @Override
    public boolean clearCart(String tagID) {
        baseApi.getRequestSpecBuilder().
                addHeader("Authorization", access_token)
                .setBasePath("/api/Cart/Clear/"+tagID);
        baseApi.
                execute(MethodType.GET);
        return baseApi.validateStatus(Constants.success);
    }

    @Override
    public String cartImport( Cart cart) {
        String json=null;
        try{
            json = om.writeValueAsString(cart);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        baseApi.
                getRequestSpecBuilder().
                addHeader("Authorization", access_token).
                addHeader("Accept","application/json").
                addHeader("Accept","text/plain").
                addHeader("Accept","*/*").
                addHeader("Content-Type","application/json").
                setBody(json);
        String js= baseApi.
                execute(MethodType.POST).asString();
        baseApi.validateStatus(Constants.success);
        return js;
    }

    @Override
    public Response submitOrders(String importData) {
        baseApi.
                getRequestSpecBuilder().
                addHeader("Authorization", access_token).
                addHeader("Accept","application/json").
                addHeader("Accept","text/plain").
                addHeader("Accept","*/*").
                addHeader("Content-Type","application/json").
                setBody(importData);
        Response res = baseApi.
                execute(MethodType.POST);
        baseApi.validateStatus(Constants.success);
        return res;
    }
}
