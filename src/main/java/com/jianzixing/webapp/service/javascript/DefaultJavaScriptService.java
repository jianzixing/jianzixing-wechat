package com.jianzixing.webapp.service.javascript;

import com.jianzixing.webapp.service.SystemConfig;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangankang
 */
@Service
public class DefaultJavaScriptService implements JavaScriptService {
    private static volatile boolean isRunCompress = false;
    private static Log logger = LogFactory.getLog(DefaultJavaScriptService.class);

    @Override
    public void compressWebJs(boolean isReplace) throws IOException {
        if (!isRunCompress) {
            if (StringUtils.isBlank(SystemConfig.webPath)) {
                SystemConfig.webPath = DefaultJavaScriptService.class.getResource("/").getPath().split("WEB-INF")[0];
            }

            for (String js : SystemConfig.compressJsPaths) {
                String jsp = SystemConfig.webPath + js;
                List<File> fileList = new ArrayList<>();
                if (jsp.endsWith("*")) {
                    File dir = new File(jsp.replaceAll("\\*", ""));
                    File[] files = dir.listFiles();
                    for (File file : files) {
                        fileList.add(file);
                    }
                } else {
                    fileList.add(new File(jsp));
                }

                for (File jsFile : fileList) {
                    if (jsFile.isFile() && jsFile.exists() &&
                            (jsFile.getName().endsWith(".js") || jsFile.getName().endsWith(".css"))) {
                        String name = jsFile.getName();
                        name = !isReplace ? (name.endsWith(".js") ?
                                name.substring(0, name.length() - 2) + "min.js" :
                                name.substring(0, name.length() - 3) + "min.css") : name;

                        File compressJsFile = new File(jsFile.getParent() + File.separator + name);

                        Writer writer = null;
                        try {
                            String content = FileUtils.readFileToString(jsFile, "UTF-8");
                            writer = new OutputStreamWriter(new FileOutputStream(compressJsFile), "UTF-8");
                            if (compressJsFile.exists()) {
                                FileUtils.write(compressJsFile, "");
                            }
                            if (StringUtils.isNotBlank(content)) {
                                if (name.endsWith(".js")) {
                                    compress(content, writer);
                                } else {
                                    CssCompressor compressor = new CssCompressor(new InputStreamReader(IOUtils.toInputStream(content)));
                                    compressor.compress(writer, -1);
                                }
                            }
                            writer.flush();

                            logger.info("压缩文件 " + name + " 成功");
                        } finally {
                            if (writer != null) writer.close();
                        }
                    }
                }
            }

            isRunCompress = true;
        }
    }

    public static void compress(String code, Writer writer) throws IOException {
        Reader in = new InputStreamReader(IOUtils.toInputStream(code));
        JavaScriptCompressor compressor = new JavaScriptCompressor(in, null);
        compressor.compress(writer, -1, true, false, false, false);
    }
}
