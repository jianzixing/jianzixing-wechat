import com.jianzixing.webapp.service.GlobalService;
import com.jianzixing.webapp.service.trigger.EventType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mimosaframework.core.json.ModelBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring-context.xml"})
public class TestTrigger {

    @Test
    public void test() {
        GlobalService.triggerService.trigger(1, EventType.PLACE_AN_ORDER, ModelBuilder.create().put("PRICE", 3).toRootObject());
    }
}
