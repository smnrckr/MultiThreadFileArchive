package model;

public class FileStatistics {
    private final int lineCount;
    private final int characterCount;

    public FileStatistics(int lineCount, int characterCount) {
        this.lineCount = lineCount;
        this.characterCount = characterCount;
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getCharacterCount() {
        return characterCount;
    }

    @Override
    public String toString() {
        return lineCount + " satır / " + characterCount + " karakter";
    }
}
