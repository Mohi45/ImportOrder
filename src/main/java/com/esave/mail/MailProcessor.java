package com.esave.mail;

import com.cheney.central.process.ProcessOrdersSel;
import com.esave.entities.OrderDetails;
import com.esave.exception.PurveyorNotFoundException;
import com.framework.api.NotificationEvent;
import com.framework.selenium.SeleniumItradeIO;
import com.framework.selenium.SendMailSSL;
import com.framework.utils.PropertiesManager;
import com.framework.utils.Utils;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPSSLStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SearchTerm;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//"testprav59@gmail.com";
//"telzcaoavpskmzpn";
public class MailProcessor {

    private static final String DEFAUT_ATTACHMET_DIR = "/var/jenkins_home/workspace/CheneyItradeImportOrder/src/main/resources/orders";//"src\\main\\resources\\orders";
    private static final String DEFAULT_PURVEYOR_PROPERTIES = "purveyor.properties";
    private static final String DEFAULT_LOCATION_PROPERTIES = "/var/jenkins_home/workspace/CheneyItradeImportOrder/src/main/resources/location.properties";//"\\src\\main\\resources\\location.properties";
    private static final String DEFAULT_PURVEYOR_ID = "1308";
    private static final String USER_EMAIL = "importorders.diningedge@gmail.com";
    private static final String USER_PASSWORD = "kiwfakjksprtnpsx";
    private static String AC_NUM = "";

    private boolean isCheneyCentral = false;
    private static final Logger logger = Logger.getLogger(MailProcessor.class);

    private String saveDirectory;

    static SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
    static SimpleDateFormat df_nv = new SimpleDateFormat("dd-mm-yyyy");
    static SimpleDateFormat df1 = new SimpleDateFormat("mm/dd/yyyy");
    static Date date;

    /**
     * Sets the directory where attached files will be stored.
     *
     * @param dir absolute path of the directory
     */
    public void setSaveDirectory(String dir) {
        this.saveDirectory = dir;
    }

    private IMAPSSLStore createConnection() throws MessagingException {
        // Create IMAPSSLStore object
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        //URLName urlName = new URLName("imap.gmail.com");
        //IMAPSSLStore store = new IMAPSSLStore(session, urlName);
        Store store = session.getStore("imaps");
        // while implementation
        logger.info("Connecting to gmail...");

        // Connect to GMail, enter user name and password here
        store.connect("imap.gmail.com", USER_EMAIL, USER_PASSWORD);

        logger.info("Connected to - " + store);
        return (IMAPSSLStore) store;
    }

    /**
     * Main method evoke search function
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        findPath();
        PropertiesManager.purveyorPropertiesFile = DEFAULT_PURVEYOR_PROPERTIES;
        PropertiesManager.locationPropertiesFile = DEFAULT_LOCATION_PROPERTIES;

        try {

            MailProcessor mailProcessor = new MailProcessor();

            mailProcessor.setSaveDirectory(DEFAUT_ATTACHMET_DIR);

            // create an Imap connection with gmail
            IMAPSSLStore store = null;
            try {
                store = mailProcessor.createConnection();
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            // process the orders from EMail
            mailProcessor.processOrdersFromEmail(store);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unknown error occured please contact developer");
        }

    }

    /**
     * This method is used to connect to GMail account and search in it for
     * keyword
     *
     * @param store //	 * @param keywordToSearch
     * @throws MessagingException
     * @throws PurveyorNotFoundException
     * @throws IOException
     */
    private void processOrdersFromEmail(IMAPSSLStore store) {

        IMAPFolder folderInbox = null;
        IMAPFolder folderProcessed = null;
        IMAPFolder folderUnprocessed = null;
        try {

            boolean isProcessed = false;
            // Get the folder you want to search in e.g. INBOX
            try {
                folderInbox = (IMAPFolder) store.getFolder("INBOX");
                folderProcessed = (IMAPFolder) store.getFolder("processed");
                folderUnprocessed = (IMAPFolder) store.getFolder("unprocessed");
                logger.info("Total mails in inbox are = " + folderInbox.getMessageCount());

                if (folderInbox.getMessageCount() > 0) {
                    logger.info("Searching started....");

                    // Create GMail raw search term and use it to search in
                    // folder
                    folderInbox.open(Folder.READ_WRITE);
                    SearchTerm rawTerm = new SearchTerm() {

                        private static final long serialVersionUID = 1L;

                        @Override
                        public boolean match(Message message) {
                            try {
                                if (message.getSubject().contains("Order") || message.getSubject().contains("order")) {
                                    return true;
                                }
                            } catch (MessagingException ex) {
                                ex.printStackTrace();
                            }
                            return false;
                        }
                    };
                    Message[] messagesFound = folderInbox.search(rawTerm);

                    logger.info("Total messages found for keyword are = " + messagesFound.length);
                    logger.info("Messages found are:");

                    List<Message> tempSuccessList = new ArrayList<>();
                    List<Message> tempFailureList = new ArrayList<>();
                    // Process the messages found in search
                    logger.info("--------------------------------------------");
                    String contentType;
                    String messageContent;
                    for (Message message : messagesFound) {
                        contentType = message.getContentType();
                        logger.info("# " + message.getSubject());
                        // check if cheney central
                        isCheneyCentral = message.getSubject().toLowerCase().startsWith("cheney central:");

                        OrderDetails orderDetails = null;
                        try {

                            if (contentType.contains("text/plain") || contentType.contains("text/html")) {
                                Object content = message.getContent();
                                if (content != null) {
                                    messageContent = content.toString();
                                    processOrder(message.getSubject(), messageContent);
                                    isProcessed = true;
                                }
                            }

                            if (contentType.contains("multipart")) {
                                Multipart multiPart = (Multipart) message.getContent();
                                int numberOfParts = multiPart.getCount();
                                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                                    if (!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                        messageContent = getTextFromMimeMultipart(part);
                                        try {
                                            // pass Failure message
                                            orderDetails = processOrder(message.getSubject(), messageContent);
                                        } catch (MessagingException e) {
                                            // block
                                            e.printStackTrace();
                                        }
                                        if (orderDetails != null) {

                                            logger.info("Id's fetched");
                                        }
                                    }
                                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                        if (orderDetails != null) {
                                            part.saveFile(saveDirectory + File.separator + orderDetails.getOrderId()
                                                    + ".csv");
                                            try {
                                                // pass Failure message
                                                SendMailSSL.setFailureMessage(multiPart);
                                                logger.info("cheney central value is " + isCheneyCentral);

                                                if (isCheneyCentral) {
                                                    ProcessOrdersSel processOrders = new ProcessOrdersSel(orderDetails);
                                                    processOrders.start();
                                                } else {
                                                    // Call Selenium ##
                                                    SeleniumItradeIO sel = new SeleniumItradeIO();
                                                    sel.start(orderDetails);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                            isProcessed = true;
                        } catch (MessagingException | IndexOutOfBoundsException | IOException e) {
                            e.printStackTrace();
                        } catch (PurveyorNotFoundException e) {
                            try {
                                new Utils().sendNotification(e.getOrderId(), e.getPurveyorId(),
                                        NotificationEvent.FAILURE);
                                SendMailSSL.sendMailAction(e.getOrderId(), "Failure!");
                            } catch (IOException e1) {
                                logger.info("Communication failure occured while sending failure notification");
                                e1.printStackTrace();
                            } catch (KeyManagementException | NoSuchAlgorithmException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
						System.out.println("isProcessed =" + isProcessed);
                        if (isProcessed) {
                            tempSuccessList.add(message);
                        } else {
                            tempFailureList.add(message);
                        }
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                    Message[] tempSuccessMessageArray = tempSuccessList.toArray(new Message[tempSuccessList.size()]);
                    Message[] tempFailureMessageArray = tempFailureList.toArray(new Message[tempFailureList.size()]);
                    folderInbox.copyMessages(tempSuccessMessageArray, folderProcessed);
                    folderInbox.copyMessages(tempFailureMessageArray, folderUnprocessed);
                    logger.info("--------------------------------------------");
                    folderInbox.expunge();
                    logger.info("Searching done!");
                }
            } finally {
                if (folderInbox.isOpen()) {
                    folderInbox.close(true);
                }
                if (folderProcessed.isOpen()) {
                    folderProcessed.close(true);
                }
                if (folderUnprocessed.isOpen()) {
                    folderProcessed.close(true);
                }
                store.close();
            }
        } catch (MessagingException e) {

            e.printStackTrace();
        }

    }

    /**
     * Downloads new messages and saves attachments to disk if any.
     *
     * @param messageContent
     *            the message content
     * @return the string
     * @throws MessagingException
     *             the messaging exception
     * @throws PurveyorNotFoundException
     *             the purveyor not found exception
     */
    /*
     * private boolean downloadEmailAttachmentsAndProcessOrder(Message message)
     * throws PurveyorNotFoundException, MessagingException { String
     * messageContent = "";
     *
     * // content may contain attachments Multipart multiPart = null;
     *
     * try { multiPart = (Multipart) message.getContent(); int numberOfParts =
     * multiPart.getCount(); for (int partCount = 0; partCount < numberOfParts;
     * partCount++) { MimeBodyPart part = (MimeBodyPart)
     * multiPart.getBodyPart(partCount);
     *
     * // this part may be the message content scanner = new
     * Scanner(part.getInputStream()); String fileName = processOrder(scanner,
     * messageContent);
     *
     * if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) { // this
     * part is attachment fileName = part.getFileName();
     * part.saveFile(saveDirectory + File.separator + fileName); } } } catch
     * (IOException e) { e.printStackTrace(); }
     *
     * return false; }
     */

    /**
     * Process order.
     * <p>
     * //	 * @param scanner
     * the scanner
     *
     * @param messageContent the message content
     * @return the string
     * @throws IOException               Signals that an I/O exception has occurred.
     * @throws MessagingException        the messaging exception
     * @throws PurveyorNotFoundException the purveyor not found exception
     */

    // private String processOrder(Scanner scanner, String messageContent)
    // throws MessagingException, PurveyorNotFoundException {
    // String purveyorId = null;
    // String locationId = null;
    // String orderId = null;
    // while (scanner.hasNextLine()) {
    // String line = scanner.nextLine();
    // if (line.startsWith("Purveyor:")) {
    // String purveyorIdRecieved = line.substring(line.indexOf("(") + 1,
    // line.indexOf(")", line.indexOf("(")));
    // purveyorId = purveyorIdRecieved.trim();
    // }
    // if (line.startsWith("Location:")) {
    // String locationIdRecieved = line.substring(line.indexOf("(") + 1,
    // line.indexOf(")", line.indexOf("(")));
    // locationId = locationIdRecieved.trim();
    // }
    // if (line.startsWith("Order #:")) {
    // orderId = line.trim();
    // }
    // }
    // if (StringUtils.isEmpty(purveyorId)) {
    // if (StringUtils.isEmpty(locationId)) {
    // throw new PurveyorNotFoundException("Location details not found in the
    // order email", 101, purveyorId,
    // orderId);
    // } // Need to consult DEFAULT_PURVEYOR_ID throw new
    // PurveyorNotFoundException("Purveyor details not found in the order
    // email", 101, DEFAULT_PURVEYOR_ID,
    // orderId);
    // }
    // PurveyorDetails purveyorDetails = null;
    // try {
    // purveyorDetails = fetchPurveyorDetailsFromSystem(purveyorId, locationId,
    // orderId);
    // if (purveyorDetails != null) {
    // try {
    // Utils.sendNotification(purveyorId, orderId, NotificationEvent.SUCCESS);
    // } catch (IOException e1) {
    // logger.info("Communication failure occured while sending success
    // notification");
    // e1.printStackTrace();
    // }
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // Selenium sel = new Selenium();
    // sel.start(purveyorDetails);
    // return orderId;
    // }

//	private boolean updateDeliveryDate(String deDate){
//		Date date = new Date();
//		DateFormat estDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		estDate.setTimeZone(TimeZone.getTimeZone("EST"));
//		String TodaysDate = simpleDateFormat.format(new Date(estDate.format(date)));
//		String Date = simpleDateFormat.format(new Date(deDate));
//
//
//
//		return false;
//
//	}
    private OrderDetails processOrder(String subject, String messageContent) throws MessagingException, PurveyorNotFoundException {
        OrderDetails orderDetails = null;
        messageContent = messageContent.replace("\n", "").replace("\r", "").replace("=", "");

        HashMap<String, String> detailsMap;
        if (isCheneyCentral) {
            detailsMap = parseCheneyCentral(messageContent);
        } else if (subject.equalsIgnoreCase("new order")) {
            detailsMap = parseRebuild(messageContent);
        } else {
            detailsMap = parseCurrentVersion(messageContent);
        }

        if (StringUtils.isEmpty(detailsMap.get("purveyorId"))) {
            // Need to consult DEFAULT_PURVEYOR_ID
            throw new PurveyorNotFoundException("Purveyor details not found in the order email", 101,
                    DEFAULT_PURVEYOR_ID, detailsMap.get("orderId"));
        }

        if (StringUtils.isEmpty(detailsMap.get("locationId"))) {
            throw new PurveyorNotFoundException("Location details not found in the order email", 101, detailsMap.get("purveyorId"),
                    detailsMap.get("orderId"));
        }
        if (StringUtils.isEmpty(detailsMap.get("orderId"))) {
            throw new PurveyorNotFoundException("Order Id not found in the order email", 101, detailsMap.get("purveyorId"), null);
        }

        try {
            orderDetails = fetchPurveyorDetailsFromSystem(detailsMap.get("purveyorId"), detailsMap.get("locationId"), detailsMap.get("orderId"), detailsMap.get("deliveryDate"), AC_NUM);
            // Send Success Notification
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    private HashMap<String, String> parseRebuild(String messageContent) {
        logger.info(messageContent);

        HashMap<String, String> parsedMap = new HashMap<>();
        messageContent = messageContent.replaceAll("<[^>]*>", "");
        int start = messageContent.indexOf("Purveyor:");
        int end = messageContent.indexOf(regex(messageContent)) + regex(messageContent).length();
        messageContent = messageContent.substring(start, end).replaceAll(" ","");

        String purveyorId = messageContent.substring(messageContent.indexOf('(') + 1, messageContent.indexOf(')'));
        logger.info("purveyorId : "+ purveyorId);

        String orderId = messageContent.substring(messageContent.indexOf("Order#:"),
                messageContent.indexOf("Restaurant:"));
        orderId = orderId.replaceAll("[^0-9]", "");
        logger.info("orderId : "+ orderId);

        messageContent = messageContent.split("Restaurant:")[1];

        String locationId = messageContent.substring(messageContent.indexOf("Location:") , messageContent.indexOf(")"));
        locationId = locationId.replaceAll("[^0-9]", "");
        logger.info("locationId : "+ locationId);

        parsedMap.put("purveyorId", purveyorId);
        parsedMap.put("locationId", locationId);
        parsedMap.put("orderId", orderId);

        try {
            String delivery = messageContent.substring(messageContent.length()-12);
            delivery = delivery.replace("'","");
            logger.info("Delivery date : " + delivery);
            String deliveryDate = updateDeliveryDateNv(delivery);
            parsedMap.put("deliveryDate", deliveryDate);
        } catch (Exception e) {
            logger.info("delivery date not fetched : " + e.getMessage());
            parsedMap.put("deliveryDate", "");
        }
        return parsedMap;
    }

    private HashMap<String, String> parseCheneyCentral(String messageContent) {
        logger.info(messageContent);
        HashMap<String, String> parsedMap = new HashMap<>();
        String purveyorId = messageContent.substring(messageContent.indexOf("Purveyor:(") + "Purveyor:(".length(),
                messageContent.indexOf(")", messageContent.indexOf("(")));
        parsedMap.put("purveyorId", purveyorId);
        logger.info(purveyorId);

        String locationId = messageContent.substring(messageContent.indexOf("Location:(") + "Location:(".length(),
                messageContent.indexOf(")", messageContent.indexOf("Location:(")));
        parsedMap.put("locationId", locationId);
        logger.info(locationId);

        String orderId = messageContent.substring(messageContent.indexOf("Order #:") + "Order #:".length(),
                messageContent.indexOf("Location:("));
        orderId = orderId.replaceAll("[^0-9]", "");
        parsedMap.put("orderId", orderId);
        logger.info(orderId);

        parsedMap.put("deliveryDate", "");

        String acNum = messageContent.substring(messageContent.indexOf("Account Number:") + "Account Number:".length(),
                messageContent.indexOf("Order Date:"));
        acNum = acNum.replaceAll("[^0-9]", "");

        AC_NUM = acNum;

        return parsedMap;
    }

    private HashMap<String, String> parseCurrentVersion(String messageContent) {
        logger.info(messageContent);
        HashMap<String, String> parsedMap = new HashMap<>();
        String purveyorId = messageContent.substring(messageContent.indexOf("Purveyor:(") + "Purveyor:(".length(),
                messageContent.indexOf(")", messageContent.indexOf("(")));
        parsedMap.put("purveyorId", purveyorId);
        logger.info(purveyorId);

        String locationId = messageContent.substring(messageContent.indexOf("Location:(") + "Location:(".length(),
                messageContent.indexOf(")", messageContent.indexOf("Location:(")));
        parsedMap.put("locationId", locationId);
        logger.info(locationId);

        String orderId = messageContent.substring(messageContent.indexOf("Order #:") + "Order #:".length(),
                messageContent.indexOf("Location:("));
        orderId = orderId.replaceAll("[^0-9]", "");
        parsedMap.put("orderId", orderId);
        logger.info(orderId);

        try {
            String delivery = messageContent.substring(messageContent.indexOf("'202") + "\"".length(),
                    messageContent.indexOf("'Cheney"));
            logger.info("Delivery date : " + delivery);
            String deliveryDate = updateDeliveryDate(delivery);
            parsedMap.put("deliveryDate", deliveryDate);
        } catch (Exception e) {
            logger.info("delivery date not fetched : " + e.getMessage());
            parsedMap.put("deliveryDate", "");
        }
        return parsedMap;
    }

    private String updateDeliveryDate(String Ddate) {
        String dd;
        try {
            date = df.parse(Ddate);
            dd = df1.format(date);
            logger.info("Delivery date for input : " + dd);
        } catch (ParseException e) {
            logger.info("Failed at delivery date conversion");
            e.printStackTrace();
            dd = "";
        }
        return dd;
    }

    private String updateDeliveryDateNv(String Ddate) {
        String dd;
        try {
            date = df.parse(Ddate);
            dd = df1.format(date);
            logger.info("Delivery date for input : " + dd);
        } catch (ParseException e) {
            logger.info("Failed at delivery date conversion");
            e.printStackTrace();
            dd = "";
        }
        return dd;
    }

    /**
     * @param purveyorId
     * @param locationId
     * @throws IOException
     * @throws PurveyorNotFoundException
     */
    private OrderDetails fetchPurveyorDetailsFromSystem(String purveyorId, String locationId, String orderId, String deliverydate, String accountNumber)
            throws IOException, PurveyorNotFoundException {
        OrderDetails orderDetails = null;
        //Properties purveyorProperties = PropertiesManager.getPurveyorProperties();
        //String purveyorStoreUrl = purveyorProperties.getProperty(purveyorId);
        //logger.info("Purveyor URL is : " + purveyorStoreUrl);
        Properties locationProperties = PropertiesManager.getLocationProperties();
        String storeCredentials = locationProperties.getProperty(locationId);
        if (StringUtils.isNotEmpty(storeCredentials)) {
            String storeUserName = storeCredentials.split("/")[0];
            String storePassword = storeCredentials.split("/")[1];
//			if (StringUtils.isEmpty(purveyorStoreUrl)) {
//				if (StringUtils.isEmpty(storeUserName) || StringUtils.isEmpty(storePassword)) {
//					throw new PurveyorNotFoundException("Location details does not exist in the system", 103,
//							purveyorId, orderId);
//				}
//				throw new PurveyorNotFoundException("Location details does not exist in the system", 102, purveyorId,
//						orderId);
//			}
            orderDetails = new OrderDetails(storeUserName, storePassword, orderId, purveyorId, deliverydate, accountNumber);
            logger.info(orderDetails.toString());
        } else {
            throw new PurveyorNotFoundException("Location details does not exist in the system", 102, purveyorId,
                    orderId);
        }

        return orderDetails;
    }

    private String getTextFromMimeMultipart(MimeBodyPart bodyPart) throws PurveyorNotFoundException {
        String messageContent = null;
        InputStream inputStream;
        StringBuilder responseBuffer = new StringBuilder();
        try {
            inputStream = bodyPart.getInputStream();
            byte[] temp = new byte[1024];

            int countCurrentRead;
            while ((countCurrentRead = inputStream.read(temp)) > 0) {
                responseBuffer.append(new String(temp, 0, countCurrentRead, StandardCharsets.UTF_8));
            }
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }

        messageContent = responseBuffer.toString();
        return messageContent;
    }
public static void findPath(){
        File file=new File("location.properties");
    System.out.println("Absolute path: " + file.getAbsolutePath());
}
    public String tommorowDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);
        return dateFormat.format(calendar.getTime()).toString().substring(8,10);

    }

    public String regex(String msg){
        Pattern pattern = Pattern.compile("(-[0-9]{2}')");
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find())
        {
            return matcher.group(1);
        }else{
            return tommorowDate();
        }

    }
}
