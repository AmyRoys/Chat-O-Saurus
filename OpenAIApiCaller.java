import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class OpenAIApiCaller {

    public static String callOpenAIApi(String prompt) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openai80.p.rapidapi.com/chat/completions"))
                .header("content-type", "application/json")
                .header("X-RapidAPI-Key", "c88c412b56msh8b5e0e6d604d613p1eaffcjsn3fea5812d815")
                .header("X-RapidAPI-Host", "openai80.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\r\n    \"model\": \"gpt-3.5-turbo\",\r\n    \"messages\": [\r\n        {\r\n            \"role\": \"user\",\r\n            \"content\": \"" + prompt + "\"\r\n        }\r\n    ]\r\n}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        String content = null;
        try {
            // Parse the JSON response
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response.body());

            // Extract the content from the choices array
            JSONArray choicesArray = (JSONArray) jsonObject.get("choices");
            JSONObject choiceObject = (JSONObject) choicesArray.get(0);
            JSONObject messageObject = (JSONObject) choiceObject.get("message");
            content = (String) messageObject.get("content");

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return content;
    }
}
