import org.junit.Ignore;
import org.junit.Test;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestHttpClientUpload {

    @Test
    @Ignore
    public void upload() throws IOException {
        String url = "http://localhost:8080/admin/File/uploadFile.action";
        File f = new File("C:\\Users\\yangankang\\Pictures\\a.jpg");
        Map params = new LinkedHashMap();
        params.put("file", new FileInputStream(f));
        params.put("gid", "0");
        params.put("file_file_name", "a.jpg");

        HttpUtils.upload(url, params);
    }
}
