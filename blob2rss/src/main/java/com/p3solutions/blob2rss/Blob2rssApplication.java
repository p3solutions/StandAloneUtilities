package com.p3solutions.blob2rss;

import com.p3solutions.blob2rss.process.directoryListener;

public class Blob2rssApplication {

    public static void main(String[] args) {
        String downloadPath = args[0]; // "C:\\Users\\DELL\\Downloads";
        String opPath = args[1]; // "C:\\Users\\DELL\\Downloads";
        directoryListener.listenDirectoryForNewHTMLFile(downloadPath,opPath);
    }

}
