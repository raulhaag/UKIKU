package knf.kuma.videoservers;

import android.content.Context;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static knf.kuma.videoservers.VideoServer.Names.MP4UPLOAD;

public class MP4UploadServer extends Server {
    public MP4UploadServer(Context context, String baseLink) {
        super(context, baseLink);
    }

    @Override
    public boolean isValid() {
        return baseLink.contains("server=mp4");
    }

    @Override
    public String getName() {
        return MP4UPLOAD;
    }

    @Nullable
    @Override
    public VideoServer getVideoServer() {
        String server_link = "https://www.mp4upload.com/embed-" + baseLink.substring(baseLink.indexOf("value=") + 6, baseLink.lastIndexOf("\"")) + ".html";
        try {
            String data = Jsoup.connect(server_link).get().outerHtml();
            data = unpack(data).replaceAll(".+\"file\":\"", "").replaceAll("\".+$","");

            return new VideoServer(MP4UPLOAD, new Option(getName(), null, data));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private String unpack(String source) {
        String decoded = null;
        Pattern pat = Pattern.compile("eval(.+),(\\d+),(\\d+),'(.+?)'");
        Matcher m = pat.matcher(source);
        try {
            m.find();
            String p = m.group(1).replaceAll("\\\\", "");
            int a = Integer.parseInt(m.group(2));
            int c = Integer.parseInt(m.group(3));
            String[] k = m.group(4).split("\\|");

            while (c != 0) {
                c--;
                if (k[c].length() != 0)
                    p = p.replaceAll("\\b" + baseT(c, a) + "\\b", k[c]);
            }

            decoded = p;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decoded;
    }

    private String baseT(int num, int radix) {
        int mNum = num;
        char[] digits = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        if (radix < 2 || radix > 62) {
            radix = 10;
        }
        if (mNum < radix) {
            return "" + digits[mNum];
        }
        boolean hayMas = true;
        String cadena = "";
        while (hayMas) {
            cadena = digits[mNum % radix] + cadena;
            mNum = mNum / radix;
            if (!(mNum > radix)) {
                hayMas = false;
                cadena = digits[mNum] + cadena;
            }
        }
        return cadena;
    }
}
