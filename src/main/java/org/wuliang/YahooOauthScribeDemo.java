package org.wuliang;

import java.util.Scanner;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
/**
 *
 * @author wuliang
 */
public class YahooOauthScribeDemo {
    public static void main(String[] args)
    {
        OAuthService service = new ServiceBuilder()
                                    .provider(YahooApi.class)
                                    .apiKey("dj0yJmk9U2FpR0U5UmNUcU05JmQ9WVdrOWVFUlpSSFpQTkdVbWNHbzlNVFkyTVRnNU5URTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1lMw--")
                                    .apiSecret("baa7bdb6e4e1b609c94d7e48b27c30d65fccc6b3")
                                    .build();



        Scanner in = new Scanner(System.in);

        System.out.println("=== Yahoo's OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        Token requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println();

        System.out.println("Now go and authorize Scribe here:");
        System.out.println(service.getAuthorizationUrl(requestToken));
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(requestToken, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        String yahooGuid = getYahooGuid(accessToken.getRawResponse());
        String protectedUrl = String.format(PROTECTED_RESOURCE_URL, yahooGuid);
        requestUrl(service, accessToken, protectedUrl);
    }
    
    public static void requestUrl(OAuthService service, Token accessToken, String url) {
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, request);
        System.out.println("\r\n####################request header:");
        for (String key : request.getHeaders().keySet()) {
            System.out.println("key:" + key + " value:" + request.getHeaders().get(key));
        }
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println("code"+response.getCode());
        System.out.println("body"+response.getBody());
        System.out.println("\r\n####################response header:");
        for (String key : response.getHeaders().keySet()) {
            System.out.println("key:" + key + " value:" + response.getHeaders().get(key));
        }
        System.out.println();
        System.out.println("Done!!!");
    }
    
    private static final String PROTECTED_RESOURCE_URL = "http://social.yahooapis.com/v1/user/%1s/contacts?format=json";
    private static final String YAHOO_GUID = "xoauth_yahoo_guid";
    private static final int GUID_LENGTH = 26;
    private static String getYahooGuid(String response) {
        String yahoo_guid = null;
        int yahoo_guid_location = response.indexOf(YAHOO_GUID);
        if ( yahoo_guid_location > 0) {
            yahoo_guid = response.substring(yahoo_guid_location + YAHOO_GUID.length() + 1,
                    yahoo_guid_location + YAHOO_GUID.length() + GUID_LENGTH + 1);
        }
        return yahoo_guid;
    }
}
