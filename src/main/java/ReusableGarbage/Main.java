package ReusableGarbage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;

import MwApi.MwApiAllPagesResult;
import org.json.JSONObject;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        String apiUrl = "https://ffxiv.consolegameswiki.com/mediawiki/api.php?action=query&format=json&list=allpages&aplimit=max";
        MwApiAllPagesResult apiData = getApiData(apiUrl);

        System.out.println("Retrieved all pages from " + apiUrl);
        System.out.println("Appending all pages to a list");

        String AllPages = apiData.getAllPageIdTitlesCsvString();

        String continueToken = apiData.getContinueToken();

        while (continueToken != null) {
            apiUrl = "https://ffxiv.consolegameswiki.com/mediawiki/api.php?action=query&format=json&list=allpages&aplimit=max&apcontinue=" + continueToken;
            apiData = getApiData(apiUrl);

            System.out.println("Retrieved all pages from " + apiUrl);
            System.out.println("Appending all pages to a list");

            AllPages += apiData.getAllPageIdTitlesCsvString();
            continueToken = apiData.getContinueToken();
        }

        String directoryPath = System.getProperty("user.dir")+ File.separator+"resultJSONs"+File.separator;

        // help me figure out if directoryPath exists, if it does not, create it
        File directory = new File(directoryPath);
        if (! directory.exists()){
            directory.mkdir();
        }

        // Specify the file path including the directory
        String filePath = directoryPath + "AllPages.csv";

        // Write the JSON data to a file
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(AllPages);
        }

        System.out.println("Wrote JSON data to output.json");
    }

    protected static MwApiAllPagesResult getApiData(String apiUrl) throws IOException, InterruptedException {
        // Create an HttpClient instance
        HttpClient client = HttpClient.newHttpClient();

        // Create a HttpRequest object
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        // Send the request and get the response
        System.out.println("Sending request to " + apiUrl);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Get the response body
        System.out.println("Retrieved response from " + apiUrl);
        String jsonResponse = response.body();
        JSONObject jsonObject = new JSONObject(jsonResponse);

        List<String[]> allPageIdTitles = new ArrayList<>();

        JSONObject jqResult = jsonObject.getJSONObject("query");
        jqResult.getJSONArray("allpages").forEach((page) -> {
            allPageIdTitles.add(new String[] {Integer.toString(((JSONObject) page).getInt("pageid")), ((JSONObject) page).getString("title")});
        });

        String continueToken = null;

        try {
            continueToken = jsonObject.getJSONObject("continue").getString("apcontinue");
        }
        catch (Exception e) {
            System.out.println("No continue token found");

            return new MwApiAllPagesResult(allPageIdTitles, null);
        }

        return new MwApiAllPagesResult(allPageIdTitles, continueToken);
    }
}