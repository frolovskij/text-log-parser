package com.github.frolovskij.textlogparser;

public class Schema {

    private boolean multiLineSupport;
    private String singleLineRegex;
    private String multiLineRegex;

    public boolean isMultiLine() {
        return multiLineSupport;
    }

    public void setMultiLineSupport(boolean multiLineSupport) {
        this.multiLineSupport = multiLineSupport;
    }

    public String getSingleLineRegex() {
        return singleLineRegex;
    }

    public void setSingleLineRegex(String singleLineRegex) {
        this.singleLineRegex = singleLineRegex;
    }

    public String getMultiLineRegex() {
        return multiLineRegex;
    }

    public void setMultiLineRegex(String multiLineRegex) {
        this.multiLineRegex = multiLineRegex;
    }

}
