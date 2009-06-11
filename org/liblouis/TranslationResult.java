package org.liblouis;

public class TranslationResult {
    private String translation;
    private int[] inputPos;
    private int[] outputPos;
    private int cursorPos;
    TranslationResult(String translation, int[] outputPos, int[] inputPos, int cursorPos) {
        this.translation = translation;
        this.inputPos = inputPos;
        this.outputPos = outputPos;
        this.cursorPos = cursorPos;
    }
    public String getTranslation() {
        return translation;
    }
    public int[] getInputPos() {
        return inputPos;
    }
    public int[] getOutputPos() {
        return outputPos;
    }
    public int getCursorPos() {
        return cursorPos;
    }
}
