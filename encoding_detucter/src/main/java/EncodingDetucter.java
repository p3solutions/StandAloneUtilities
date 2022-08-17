import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;

public class EncodingDetucter {
    public static void main(String[] args) throws IOException {


        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(args[0]));
        CharsetDetector cd = new CharsetDetector();
        cd.setText(bis);
        CharsetMatch cm = cd.detect();

        if (cm != null) {
            System.out.println(cm.getName());
        }else {
            throw new UnsupportedCharsetException("not able to detect the charset");
        }

    }
}
