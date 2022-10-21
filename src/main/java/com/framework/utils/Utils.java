package com.framework.utils;

import com.cheney.central.entities.ImportEntity;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.framework.api.NotificationEvent;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class Utils {

    private static final String NOTIFICATION_API_URL = "http://user.diningedge.com/confirm-order?orderId=%s&purveyorId=%s&success=%d";

    private static final String USER_AGENT = "Mozilla/5.0";

    private static final Logger logger = Logger.getLogger(Utils.class);

    public void sendNotification(String orderId, String purveyorId, NotificationEvent event)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String notficationUrl = NOTIFICATION_API_URL;

        if (NotificationEvent.SUCCESS.equals(event)) {
            notficationUrl = String.format(notficationUrl, orderId, purveyorId, 1);
        } else {
            notficationUrl = String.format(notficationUrl, orderId, purveyorId, 0);
        }

        logger.info("Notification URL is :" + notficationUrl);
        URL url = new URL(null, notficationUrl, new sun.net.www.protocol.https.Handler());
        // do this only if URL is HTTPS
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        // Adding timeout parameters

        // create context
        SSLContext context = SSLContext.getInstance("TLS");
        // initialize the context with trust, key store
        context.init(null, null, new SecureRandom());

        // make connection
        SSLSocketFactory sockFact = context.getSocketFactory();
        connection.setSSLSocketFactory(sockFact);

        // reading output

        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // print result
            logger.info(response.toString());
            if (response.toString().equals("1")) {
                logger.info("Notification Success sent to Customer");
            }
        } else {
            logger.info("Notification Response Code is other than 200_OK so failed to send notification");
        }
    }

    public List<ImportEntity> parseCsv(String path) throws IOException {
        MappingIterator<ImportEntity> personIter = new CsvMapper().readerWithTypedSchemaFor(ImportEntity.class).readValues(path);
        List<ImportEntity> entities = personIter.readAll();
        return entities;
    }

}
