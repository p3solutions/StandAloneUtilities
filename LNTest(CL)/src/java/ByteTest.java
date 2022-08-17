import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

import org.apache.commons.io.FileUtils;


public class ByteTest {
	public static void main(String[] args) throws IOException {

		String str ="R0lGODlhuQAvAOYAAP///wAAAPDw8LCwsMjIyEhISOjo6NjY2Pjo6Pjw8HgAAIiIiKgAALAAAODg\r\n" + 
				"4PhISMAAAMgAALi4uNAICDhAQGBoYHAAAKCgoMDAwNDQ0GgAAJCYkJiYmNAAAOAgIPhYWPhoaPio\r\n" + 
				"qHiAeKAAANBAQNgQEOAYGOAwMPg4OPhAQPh4ePjY2GBgYKiwqLgAANAQEOA4OOgoKOgwMOg4OOho\r\n" + 
				"aPA4OPDg4PiIiPiYmPi4uFBYWGAAAGhoaICAgIgAAJCQkJgAALAICLgQEMAQENAgINgICNgoKNgw\r\n" + 
				"MOBgYPBAQPBoaPB4ePgYGPgoKPjIyHBwcJAAAKioqMAYGMDIyMgYGMggINAoKNCAgNhwcOBISOBY\r\n" + 
				"WOggIOhISOhwcOiIkPAwMPCQkPCgoPCwsPDIyPDY2PDo6PgICPhwcPjAwPjg4AAAAAAAAAAAAAAA\r\n" + 
				"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACwA\r\n" + 
				"AAAAuQAvAEAI/wABCBxIsKDBgwgTKlzIsKHDhxAjSpxIsaJFhAwmaHyBBMELjgiyIJiAQEuECBBS\r\n" + 
				"+sgw4McCDhgOSLhIs6bNmzgXDgAgoKdPATge/BwqYGfOo0iTKj1oFMCNEA8AUCAAYANVACwEIigD\r\n" + 
				"oOnSr2DDih1L1mAAAGfTFjwrkG0At2gHqm0bt+7cuG/pyj3IVq9fv3Dp9p07uK5hwQQLr+2beK3Z\r\n" + 
				"vYUZlwULI8WDyylS1KjxJUaMLR5MlChRZEKHCAwu/DBwdbLr1zgPyJ49mwOB27hvk7hyW4CB38AN\r\n" + 
				"eIVNvHjF4QIfrADgxEwOol2NS58OEXkLAxcuLMiwQMKGBQ42tP+ITh2pZMl/FaKnef5tXrTuE64/\r\n" + 
				"ytj9e7n3G+t3GJiv472NKWZfW+8NZmB/9Q34l4L7CRYffAXiV19i8bWHYIQAZujWgxDeVd5XTf0E\r\n" + 
				"3AEEHIDBhyiiSEQMMrS4RAJKJPAiFwmYkAANpJkWARASgCeAAyemKCRsPjBgJAMNJKlkkkcaOcKT\r\n" + 
				"Fvx2QAYESCDBAMgNqeVkOxElAApe/pTllmSGhVxPIHwQQpjklemmWMg18cEDISwHwAppqoCDAG2+\r\n" + 
				"6adSRl3mBAAt6PBEAQtUcAEFiFJQQVZj/ikpTRjwUMClmGaqKaY8BDnpp6C6eV9+iDE0n0XrTZgh\r\n" + 
				"ZGCpqt5jDZr/ehis/RH4YGSsNoigYa7qGqt9u+LKa6nCwmrsqrMOW2yoOJ3n56nMWpTbtLnNFO21\r\n" + 
				"EJ1wAgwzdAuDtkcYYYQVRFRBhRRDCCFEEEHY1hq28CJ0RA1JJJHCGQkkgEaMCYDhQb5jlKDjCFH0\r\n" + 
				"YIAD78arMABDmODBw13kGwYNNSJAAwIIiNHBaRFAQQAHB/AE5MILA5FSSiQgAEEEKaeBBQQIkODC\r\n" + 
				"zEkq4ABLHCzQw3aRkiypAkAHLbQFRBOtwdFH77BDcA40fUDPPl/bJVEo2EDUbz1BHTWzUw/1wQ1h\r\n" + 
				"Zr111CH+FEITYRc1ts9ZrvCBDUxYDZ3Wa0+KXA4qAIBACCCg0sDEAyqoAAKfdNf9p1cI3ICDQmR4\r\n" + 
				"0afhUgsEQg4oIJABBd8VcEEFGzg6BQsXPA451waJIMKlIlSwgA4UnF7BTIWPXmbsBdEuu5YOYKn7\r\n" + 
				"7rz3nvDtwAcv/NbOHvtqTqnuNx+0N/WqPIUHQuShWQY+FtmBG25IoIQO/uf9YejdpeD42QNm64Ic\r\n" + 
				"Gj9r+BwOSGqAeHkfLGTua5gr+P6tWrxd9xMGoPgVQsjyjBe+/iVLP7VyVrHEx7/4lQp/x1IMshgo\r\n" + 
				"LAlaEIIYLGCslHW/4f3qWREJCAA7";
		
		
		Base64.Decoder mimeDecode = Base64.getMimeDecoder();
        byte[] decode = mimeDecode.decode(str.getBytes());
        FileUtils.writeByteArrayToFile(new File("C:\\\\Users\\\\91735\\\\Desktop\\\\im_out.gif"), decode);
		
	}

}
