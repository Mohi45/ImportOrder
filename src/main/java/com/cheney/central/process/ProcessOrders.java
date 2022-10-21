package com.cheney.central.process;

import com.cheney.central.actions.ImportOrder;
import com.cheney.central.actions.ImportOrderImpl;
import com.cheney.central.entities.*;
import com.esave.entities.OrderDetails;
import com.esave.exception.ImportOrderException;
import com.framework.selenium.SeleniumItradeIO;
import com.framework.selenium.SendMailSSL;
import com.framework.utils.Utils;
import io.restassured.response.Response;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ProcessOrders {
    private static final Logger logger = Logger.getLogger(ProcessOrders.class);

    private final ImportOrder importOrder = new ImportOrderImpl();
    private Credential credential;
    private UserInfo userInfo;

    private Utils utils;

    private String fileName = "C:/orders/id.csv";
    private List<ImportEntity> entities;
    private final OrderDetails orderDetails;

    public ProcessOrders(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void orderProcessing() {
        try {
            credential = new Credential(orderDetails.getUserName(), orderDetails.getPassword());
            fileName = fileName.replace("id", orderDetails.getOrderId());
            importOrder.token(credential);
            try {
                entities = utils.parseCsv(fileName);
            } catch (Exception ex) {
                logger.info("Failed to parse CSV file" + fileName);
                ex.printStackTrace();
            }
            importOrder.clearCart("17171");
            userInfo = importOrder.getUserInfo();
            Tag tag = getTag(17171, userInfo);
            Cart cart = createImportCart(tag);
            String resJson = importOrder.cartImport(cart);
            Response res = importOrder.submitOrders(resJson);
            res.prettyPrint();
            if (res.statusCode() == 200) {
                // notification
                new SeleniumItradeIO().sendOrderStatusMail(orderDetails, "Success");
            }
        } catch (ImportOrderException i) {
            i.printStackTrace();
            SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), i.getDetailedMessage());
        } catch (Exception ex) {
            logger.info("Failed !!!!" + ex.getMessage());
            ex.printStackTrace();
            // notification
            SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), "Order Import Failed");
        }
    }

    private Cart createImportCart(Tag tg) {
        Cart cart = new Cart();
        cart.setFacility(tg.facility);
        cart.setDeliveryDate("Order Date");
        cart.setTagID(tg.tagID);
        cart.setNumber(tg.number);
        cart.setPoNo("Order Number");
        cart.setOrderType("D");
        List<Item> items = new ArrayList<>();
        for (ImportEntity entity : entities) {
            Item item = new Item();
            item.setItem(entity.item.trim());
            item.setAdd2COG(true);
            item.setBreakable(false);
            item.setHasgs1data(false);
            item.setHaspic(false);
            item.setSplit(false);
            item.setQuantity(entity.quantity);
            items.add(item);
        }
        cart.setItems(items);
        return cart;
    }

    public String getToken(){
        credential = new Credential(orderDetails.getUserName(), orderDetails.getPassword());
        Token token = importOrder.token(credential);
        return token.access_token;
    }

    public Tag getTag(int id, UserInfo userInfo) {
        Tag tg = null;
        List<Tag> tags = userInfo.getTags();
        for (Tag t : tags) {
            if (t.tagID == id) {
                tg = t;
            }
        }
        return tg;
    }
}
