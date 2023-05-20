package MwApi;

import java.util.List;

public class MwApiAllPagesResult {
    private final List<String[]> allPageIdTitles;
    private final String continueToken;

    public MwApiAllPagesResult(List<String[]> allPageIdTitles, String continueToken) {
        this.allPageIdTitles = allPageIdTitles;
        this.continueToken = continueToken;
    }

    public List<String[]> getAllPageIdTitles() {
        return allPageIdTitles;
    }

    public String getContinueToken() {
        return continueToken;
    }

    public String getAllPageIdTitlesCsvString() {
        StringBuilder sb = new StringBuilder();
        for (String[] pageIdTitle : allPageIdTitles) {
            sb.append(pageIdTitle[0]).append(";\"").append(pageIdTitle[1]).append("\"\n");
        }
        return sb.toString();
    }
}
