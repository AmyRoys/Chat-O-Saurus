import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ClaimBusterAPI {

    public static void main(String[] args) {
        String input = "Donald Duck is the current president of the United States.";
        double score = getClaimBusterScore(input);
        System.out.println("ClaimBuster score for \"" + input + "\": " + score);
    }
    public static double getClaimBusterScore(String input) {
        String apiKey = "f69e1f1a98ee44ef8b586c31df77ecea";
        double score = -1.0; // Default value if score extraction fails
        String encodedInput;
        String apiEndpoint;

        try {
            encodedInput = URLEncoder.encode(input, "UTF-8");
            apiEndpoint = "https://idir.uta.edu/claimbuster/api/v2/score/text/" + encodedInput;
        } catch (IOException e) {
            System.out.println("Error encoding input: " + e.getMessage());
            return score;
        }

        try {
            URL url = new URL(apiEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method and headers
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-api-key", apiKey);

            // Get the API response
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                String jsonResponse = response.toString();
                JSONParser parser = new JSONParser();
                try {
                    JSONObject json = (JSONObject) parser.parse(jsonResponse);
                    JSONArray results = (JSONArray) json.get("results");
                    if (results != null && results.size() > 0) {
                        JSONObject result = (JSONObject) results.get(0);
                        score = (Double) result.get("score");
                    } else {
                        System.out.println("No results found in the JSON response.");
                    }
                } catch (ParseException e) {
                    System.out.println("Error parsing JSON response: " + e.getMessage());
                }
            } else {
                System.out.println("API request failed. Response Code: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return score;
    }
}
