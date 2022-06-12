import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONObject;

public class AuthenticationManager {

    private static final int TOKEN_LIFETIME = 3600000;

    private static String accessToken = "";
    private static long accessTokenRetrievalTime = 0;
    private static String entitlementToken = "";
    private static long entitlementTokenRetrievalTime = 0;

    public static String getAccessToken() {
        if (System.currentTimeMillis() - accessTokenRetrievalTime < TOKEN_LIFETIME && !accessToken.equals("")) {
            return accessToken;
        }

        accessToken = getNewAccessToken();

        return accessToken;
    }

    public static String getEntitlementToken() {
        if ((System.currentTimeMillis() - entitlementTokenRetrievalTime < TOKEN_LIFETIME) && !entitlementToken.equals("")) {
            return entitlementToken;
        }

        entitlementToken = getNewEntitlementToken();

        return entitlementToken;
    }

    private static String getNewAccessToken() {
        HttpResponse<String> response = Unirest.put("https://auth.riotgames.com/api/v1/authorization")
                .header("cookie", getAuthCookies())
                .header("Content-Type", "application/json")
                .body("{\n    \"type\": \"auth\",\n    \"username\": \"USERNAME\",\n    \"password\": \"PASSWORD\",\n    \"remember\": true,\n    \"language\": \"en_US\"\n}")
                .asString();

        JSONObject jsonObject = new JSONObject(response.getBody());
        String uri = jsonObject.getJSONObject("response").getJSONObject("parameters").getString("uri");
        accessTokenRetrievalTime = System.currentTimeMillis();
        return uri.substring(uri.indexOf("access_token=") + 13, uri.indexOf('&'));
    }

    public static String getAuthCookies() {
        HttpResponse<String> response = Unirest.post("https://auth.riotgames.com/api/v1/authorization")
                .header("Content-Type", "application/json")
                .body("{\"client_id\":\"play-valorant-web-prod\",\"nonce\":\"1\",\"redirect_uri\":\"https://playvalorant.com/opt_in\",\"response_type\":\"token id_token\"}")
                .asString();

        String cookies = response.getHeaders().get("set-cookie").toString();

        return cookies;
    }

    private static String getNewEntitlementToken() {
        HttpResponse<String> response = Unirest.post("https://entitlements.auth.riotgames.com/api/token/v1")
                .header("cookie", getAuthCookies())
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAccessToken())
                .asString();

        JSONObject jsonObject = new JSONObject(response.getBody());
        entitlementTokenRetrievalTime = System.currentTimeMillis();
        return jsonObject.getString("entitlements_token");
    }


}
