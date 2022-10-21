package com.cheney.central.actions;

import com.cheney.central.entities.Cart;
import com.cheney.central.entities.Credential;
import com.cheney.central.entities.Token;
import com.cheney.central.entities.UserInfo;
import io.restassured.response.Response;

public interface ImportOrder {
    Token token(Credential credentials);

    UserInfo getUserInfo();

    boolean clearCart(String tagId);

    String cartImport(Cart cart);

    Response submitOrders(String importData);
}
