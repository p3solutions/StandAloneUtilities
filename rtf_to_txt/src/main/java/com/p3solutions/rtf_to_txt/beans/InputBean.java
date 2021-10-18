package com.p3solutions.rtf_to_txt.beans;

import org.kohsuke.args4j.Option;

public class InputBean {

    @Option(name = "-ip", aliases = {"--inputPath"}, usage = "Input Location", required = true)
    private String inputPath;

    @Option(name = "-op", aliases = {"--outputPath"}, usage = "Output Location", required = true)
    private String outputPath;

    @Option(name = "-mpp", aliases = {"--maximumParallelProcess"}, usage = "Maximum Thread", required = true)
    private int mpp;

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public int getMpp() {
        return mpp;
    }

    public void setMpp(int mpp) {
        this.mpp = mpp;
    }
}
