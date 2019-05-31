package com.example.controller.app;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/*
 * Gson: https://github.com/google/gson
 * Maven info:
 *     groupId: com.google.code.gson
 *     artifactId: gson
 *     version: 2.8.1
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Translate {
    private static final String TAG = Translate.class.getSimpleName();

// **********************************************
// *** Update or verify the following values. ***
// **********************************************

    // Replace the subscriptionKey string value with your valid subscription key.
    private static String subscriptionKey = "d9f98b1aeb9a41679714e2ff00b47771";

    private static String host = "https://api.cognitive.microsofttranslator.com";
    private static String path_translate = "/translate?api-version=3.0";
    // Translate to German and Italian.
    private static String params = "&to=de&to=it";

    static String text = "Hello world!";

    public static class RequestBody {
        String Text;

        public RequestBody(String text) {
            this.Text = text;
        }
    }

    public static String getLanguages() throws Exception {
        String path_language = "/languages?api-version=3.0";
        URL url = new URL(host + path_language);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
        connection.setDoOutput(true);

        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }

    private static String Post(URL url, String content) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", content.length() + "");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
        connection.setRequestProperty("X-ClientTraceId", java.util.UUID.randomUUID().toString());
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        byte[] encoded_content = content.getBytes("UTF-8");
        wr.write(encoded_content, 0, encoded_content.length);
        wr.flush();
        wr.close();

        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }

    public static String translate() throws Exception {
        URL url = new URL(host + path_translate + params);

        List<RequestBody> objList = new ArrayList<>();
        objList.add(new RequestBody(text));
        String content = new Gson().toJson(objList);

        return Post(url, content);
    }

    private static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    public static void main(String... args) {
        try {
            String response = translate();
            Log.e(TAG, LogUtil.prependCallLocation(prettify(response)));
        } catch (Exception e) {
            Log.e(TAG, LogUtil.prependCallLocation(e.toString()));
        }
    }

    public static String translate(String sourceText, String target) {
        try {

            params = "&to="+target;
            URL url = new URL(host + path_translate + params);

            List<RequestBody> objList = new ArrayList<>();
            objList.add(new RequestBody(sourceText));
            String content = new Gson().toJson(objList);
            String response = Post(url, content);
            Log.e(TAG, LogUtil.prependCallLocation("translate: "+response));
            JsonElement json = new JsonParser().parse(response);
            JsonElement translations = json.getAsJsonArray().get(0).getAsJsonObject().get("translations");
            String result = translations.getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();
            return result;

        } catch (Exception e) {
            Log.e(TAG, LogUtil.prependCallLocation(e.toString()));
            return null;
        }
    }
}
