package main.bd.res;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {
    public static ArrayList<JSONObject> readJsonFromUrl(String url, Callback cb) throws IOException, JSONException {

        JSONObject
        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_FORM_URLENCODED);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://localhost:8080/register");
        request.setEntity(entity);

        HttpResponse response = httpClient.execute(request);
        System.out.println(response.getStatusLine().getStatusCode());

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String l;
            ArrayList<JSONObject> objects = new ArrayList<JSONObject>();
            while ((l = rd.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(l);
                cb.run(jsonObject);
                objects.add(jsonObject);
            }
            return objects;
        } finally {
            is.close();
        }
    }

    public static void main(String[] args) throws IOException, JSONException {
        readJsonFromUrl("https://api.textsynth.com/v1/engines/gptj_6B/completions", new Callback() {
            @Override
            public void run(JSONObject obj) {
                System.out.println(obj.toString());
            }
        });
    }

    public interface Callback {
        void run(JSONObject obj);
    }
}