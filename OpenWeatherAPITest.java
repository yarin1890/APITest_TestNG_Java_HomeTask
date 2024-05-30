import org.testng.Assert;
import org.testng.annotations.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenWeatherAPITest {
    private static final Logger logger = Logger.getLogger(OpenWeatherAPITest.class.getName()); // Added a logger for easier log reading

    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "a6919dd2e20c5ca67bcf1c727a0b36bf";

    private HttpClient httpClient;

    @BeforeClass
    public void setUp() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testCountryIsCorrectForTelAviv() {
        try {
            final String CITY_TEL_AVIV = "Tel Aviv";
            final String COUNTRY_IL = "IL";
            final int RESPONSE_CODE_200 = 200;

            // Use apache's HTTP libraries to send a request, convert it to a JSON, and release the connection to prevent potential leak issues
            HttpGet httpGet = new HttpGet(API_BASE_URL + "?q=" + CITY_TEL_AVIV.replace(" ", "-") + "," + COUNTRY_IL + "&APPID=" + API_KEY);
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpGet);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = new JSONObject(jsonResponse);
            int responseCode = response.getStatusLine().getStatusCode();
            response.close();

            System.out.println("API Request's Response Data: " + jsonObject); // *Section 1 of the home task*

            System.out.println("API Request's Response Code: " + responseCode); // *Section 2 of the home task*
            Assert.assertEquals(responseCode, RESPONSE_CODE_200, "Error: The Response Code is not 200 as expected!"); // *Section 3 of the home task*

            String country = jsonObject.getJSONObject("sys").getString("country"); // *Section 4 of the home task*
            String cityName = jsonObject.getString("name");
            double temp = jsonObject.getJSONObject("main").getDouble("temp");

            if (CITY_TEL_AVIV.equalsIgnoreCase(cityName)) // *Section 5 of the home task* - if London/NY will stay Fahrenheit
                temp = fahrenheitToCelsius(temp);

            if (CITY_TEL_AVIV.equalsIgnoreCase(cityName)) // *Section 6 of the home task*
                Assert.assertEquals(country, COUNTRY_IL,
                        "Error: Country is not " + COUNTRY_IL +
                        " as expected, even though we are checking the temperature in " + CITY_TEL_AVIV + "!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An error has occurred: ", e);
            Assert.fail("The test has failed: " + e.getMessage());
        }
    }

    // https://stackoverflow.com/questions/43163626/how-to-create-a-conversion-of-fahrenheit-to-celsius-in-java
    private static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32.0) * 5.0 / 9.0;
    }
}