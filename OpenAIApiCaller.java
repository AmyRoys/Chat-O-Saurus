import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class OpenAIApiCaller {

    public static String callOpenAIApi(String prompt) throws URISyntaxException, IOException, InterruptedException {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://chatgpt-api6.p.rapidapi.com/standard-gpt"))
//                .header("content-type", "application/json")
//                .header("X-RapidAPI-Key", "c88c412b56msh8b5e0e6d604d613p1eaffcjsn3fea5812d815")
//                .header("X-RapidAPI-Host", "chatgpt-api6.p.rapidapi.com")
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\r\n    \"conversation\": [\r\n        {\r\n            \"role\": \"user\",\r\n            \"content\": \""+ prompt + "\"\r\n        }\r\n    ]\r\n}"))
//                .build();
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        int code = response.statusCode();
//        System.out.println("The response is: " + response.body());
//        System.out.println("The response code is: " + code);
//        String content = null;
//        try {
//            JSONParser parser = new JSONParser();
//            JSONObject jsonObject = (JSONObject) parser.parse(response.body());
//
//            JSONObject answerObject = (JSONObject) jsonObject.get("answer");
//            content = (String) answerObject.get("content");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return "This is the response from the server";
    }
}


