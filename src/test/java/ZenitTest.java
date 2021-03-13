import org.example.bookmaker.Result;
import org.example.bookmaker.Zenit;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ZenitTest {

    @Test
    public void zenitJsonShouldNotNull() throws IOException {
        String url = "https://zenit.win/ajax/line/printer/react?all=0&onlyview=0&timeline=0&sport=1&league=&games=&ross=0&popular_group=&offset=0&length=50&timezone=3&lang_id=1&sort_mode=2&b_id=&popular=0&client_v=";
        Zenit zenit = new Zenit();
        JSONObject json = zenit.getJson(url);
        Assert.assertNotNull(json);
    }

    @Test
    public void resultJsonShouldNotNull() throws IOException {
        String URL_JSON_RESULT = "https://zenit.win/nws/v1/matchesresults?timezone_id=3&is_live=0&main_score=0&date_from=%s&date_to=%s&site_type=1&language_id=1049&sports%%5B0%%5D=1&include_other=0";
        String nowDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-M-d"));
        String url = String.format(URL_JSON_RESULT, nowDate, nowDate);
        Result result = new Result();
        JSONObject json = result.getJson(url);
        Assert.assertNotNull(json);
    }
}
