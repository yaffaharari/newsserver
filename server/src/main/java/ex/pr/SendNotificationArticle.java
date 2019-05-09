package ex.pr;

import com.google.gson.Gson;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SendNotificationArticle implements Runnable {

    private static final String FIREBAE_AUTH = "key=AAAAqDTOSo8:APA91bElxnWSQ4ZXs5VxkFm_74ZteUnG9IAB92jDxWKfzD9nT9SPMS2luh87JTHhaK6yGTbvEGSZir_GFd54q0rFUKhQozuFX37rReohXU3C190Xwp2meXlYB9MdE9AMaS-nEsWhaSQ3";
    private List<FeedItem> feedItemList;
    private List<String> tokenClientList;

    public SendNotificationArticle(List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
    }

    @Override
    public void run() {
        // tokenClientList = getClientList();
       /* for (FeedItem feedItem : feedItemList) {
            sendNotification(feedItem);
        }*/
        synchronized (feedItemList) {
            for (int i = 0; i <= feedItemList.size() - 1; i++) {
                FeedItem feedItem = feedItemList.get(i);
                sendNotification(feedItem);
                if (i == feedItemList.size() - 1) {
                    feedItemList.clear();
                }
            }
        }
    }

    private void sendNotification(FeedItem item) {
        String postUrl = "https://fcm.googleapis.com/fcm/send";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);

        post.setHeader("authorization", FIREBAE_AUTH);
        post.setHeader("Content-type", "application/json");

        JSONObject contentJson = new JSONObject();
        contentJson.put("title", item.getTitle());
        contentJson.put("description", item.getDescription());
        contentJson.put("link", item.getLink());
        contentJson.put("pubDate", getItemDateFormated(item.getPubDate().toString()));

        JSONObject pushNotificationJson = new JSONObject();

        pushNotificationJson.put("data", contentJson);
        pushNotificationJson.put("to", "/topics/newsUpdateTopic");

        System.out.println(pushNotificationJson.toString());

        try {
            StringEntity stringEntity = new StringEntity(pushNotificationJson.toString(),"UTF-8");
            //stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(post);
            System.out.println(response.getEntity().getContent().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getItemDateFormated(String pubDate) {
        DateTimeFormatter dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME;
        String currectPubDate;
        try {
            ZonedDateTime zoned = ZonedDateTime.parse(pubDate, dateFormat);
            LocalDateTime local = zoned.toLocalDateTime(); //2019-02-22T10:59:12
            currectPubDate = local + "Z";
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return "0000-00-00T00:00:00Z";  //default date
        }
        return currectPubDate;
    }
}

/*
    @SuppressWarnings("Duplicates")
    private List<String> getClientList() {
        ArrayList<String> tokens = new ArrayList<>();
        Connection connection = null;
        try {
            Class.forName(Constants.DEFAULT_DRIVER_CLASS);
            connection = DriverManager.getConnection(Constants.DEFAULT_URL, Constants.USERNAME, Constants.PASSWORD);
            Statement st = connection.createStatement();
            String sql = "select * from TokenList.dbo.table_tokens";
            ResultSet result = st.executeQuery(sql);
            while(result.next()) {
                String clientToken = result.getString("client_token");
                System.out.println(clientToken);
                tokens.add(clientToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tokens;
    }*/
