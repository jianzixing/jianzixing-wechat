import com.jianzixing.webapp.service.wechat.WeChatInterfaceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mimosaframework.core.json.ModelBuilder;
import org.mimosaframework.core.utils.HttpUtils;

import java.io.IOException;

public class TestReplyXml {

    @Test
    public void t1() {
        String xml = WeChatInterfaceUtils.modelToXml(
                ModelBuilder.create().put("abc", "a")
                        .startArray("def")
                        .startModel().put("d1", "d1").endParent()
                        .startModel().put("d2", "d2").endParent()
                        .endParent()
                        .startModel("efg").put("e1", "e1").endParent()
                        .toRootObject(), true);
        System.out.println(xml);
    }

    @Test
    @Ignore
    public void t2() throws IOException {
        HttpUtils.postJson("http://localhost:8080/wc_tp/c.html", "{}");
    }
}
