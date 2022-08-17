package com.p3solutions.blob2rss.process;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class directoryListener {

    public static void listenDirectoryForNewHTMLFile(String inputPath, String opPath) {

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(inputPath);
            path.register(watchService, ENTRY_CREATE);
            boolean poll = true;
            while (poll) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
//					if (checkExportFile(event.context().toString()))
                    convertHtmltoPdf(event.context().toString(), inputPath, opPath);

                }
                poll = key.reset();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//	private static boolean checkExportFile(String fileName) {
//
//		boolean value = false;
//
//		try {
//			String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
//			if (extension.equalsIgnoreCase("html") && fileName.startsWith("PDF"))
//				value = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return value;
//	}

    public static void convertHtmltoPdf(String fileName, String filePath, String opPath) {
        File file = new File(filePath + File.separator + fileName);

        try {
            int err;
            int comprLen = 100000000;
            int uncomprLen = comprLen;
            byte[] compr = new byte[comprLen];
            byte[] uncompr = new byte[uncomprLen];

            System.out.println("Processing " + file.getAbsolutePath());
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            fileInputStream.skip(8);
            fileInputStream.read(compr);
            fileInputStream.close();

            Inflater inflater = new Inflater(JZlib.WrapperType.ZLIB);

            inflater.setInput(compr);
            inflater.setOutput(uncompr);

            err = inflater.init();
            CHECK_ERR(inflater, err, "inflateInit");

            while (inflater.total_out < uncomprLen && inflater.total_in < comprLen) {
                inflater.avail_in = inflater.avail_out = 1; /* force small buffers */
                err = inflater.inflate(JZlib.Z_FULL_FLUSH);
                if (err == JZlib.Z_STREAM_END)
                    break;
                CHECK_ERR(inflater, err, "inflate");
            }

            err = inflater.end();
            CHECK_ERR(inflater, err, "inflateEnd");

            String str = new String(uncompr).replaceAll("\0", "");
            Files.write(
                    Paths.get(opPath + File.separator + file.getName().replace(".dat", "") + "_decompressed.rrs"),
                    str.getBytes(), StandardOpenOption.CREATE);

            System.out.println("Processed " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Failed to parse : " + file.getAbsolutePath() + " Exception Message :" + e.getMessage());
        }


    }


    static void CHECK_ERR(ZStream z, int err, String msg) throws Exception {
        if (err != JZlib.Z_OK) {
            if (z.msg != null)
                System.out.print(z.msg + " ");
            throw new Exception(z.msg + " " + msg + " error: " + err);
        }
    }

}
