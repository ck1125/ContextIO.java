package at.tomtasche.contextio;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;


/**
 * Class to manage Context.IO API access
 *
 * @author Thomas Taschauer | tomtasche.at
 */
public class ContextIOV2 {

    static final String ENDPOINT = "api.context.io";

    String key;
    String secret;
    String apiVersion;
    boolean ssl;
    boolean saveHeaders;
    boolean authHeaders;
    ContextIOResponse lastResponse;

    /**
     * Instantiate a new ContextIO object. Your OAuth consumer key and secret can be
     * found under the "settings" tab of the developer console (https://console.context.io/#settings)
     *
     * @param key    Your Context.IO OAuth consumer key
     * @param secret Your Context.IO OAuth consumer secret
     */
    public ContextIOV2(String key, String secret) {
        this.key = key;
        this.secret = secret;
        this.ssl = true;
        this.saveHeaders = false;
        this.apiVersion = "2.0";
    }


    /**
     * Returns the 25 most recent messages for a given account id. Use limit to change that number.
     * This is useful if you're polling a mailbox for new messages and want all new messages
     * indexed since a given timestamp.
     *
     * @param id     Unique id of an account accessible through your API key
     * @param params Query parameters for the API call:
     *               limit
     * @return ContextIOResponse
     * @link http://context.io/docs/2.0/accounts/messages
     */
    public ContextIOResponse getMessages(String id, Map<String, String> params) {
        params = filterParams(params, new String[]{"limit"});
        return get(null, format("accounts/%s/messages", id), params);
    }


    public boolean isSsl() {
        return ssl;
    }

    /**
     * Specify whether or not API calls should be made over a secure connection.
     * HTTPS is used on all calls by default.
     *
     * @param ssl Set to false to make calls over HTTP, true to use HTTPS
     */
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Set the API version. By default, the latest official version will be used
     * for all calls.
     *
     * @param apiVersion Context.IO API version to use
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public boolean isAuthHeaders() {
        return authHeaders;
    }

    /**
     * Specify whether OAuth parameters should be included as URL query parameters
     * or sent as HTTP Authorization headers. The default is URL query parameters.
     *
     * @param authHeaders Set to true to use HTTP Authorization headers, false to use URL query params
     */
    public void setAuthHeaders(boolean authHeaders) {
        this.authHeaders = authHeaders;
    }

    /**
     * Returns the ContextIOResponse object for the last API call.
     *
     * @return ContextIOResponse
     */
    public ContextIOResponse getLastResponse() {
        return lastResponse;
    }

    public String build_baseurl() {
        String url = "http";
        if (ssl) {
            url = "https";
        }

        return url + "://" + ENDPOINT + "/" + apiVersion + '/';
    }

    public String build_url(String action) {
        return build_baseurl() + action;
    }

    public boolean isSaveHeaders() {
        return saveHeaders;
    }

    public void setSaveHeaders(boolean saveHeaders) {
        this.saveHeaders = saveHeaders;
    }

//	public ContextIOResponse[] get(String[] accounts, String action, Map<String, String> params) {
//		ContextIOResponse[] responses = new ContextIOResponse[accounts.length];
//		for (int i = 0; i < accounts.length; i++) {
//			responses[i] = doCall("GET", accounts[i], action, params);
//		}
//
//		return responses;
//	}

    public ContextIOResponse get(String account, String action, Map<String, String> params) {
        return doCall("GET", account, action, params);
    }

    public ContextIOResponse post(String account, String action, Map<String, String> params) {
        return doCall("POST", account, action, params);
    }

    public ContextIOResponse doCall(String method, String account, String action, Map<String, String> params) {
        if (account != null && !account.equals("")) {
            if (params == null) {
                params = new HashMap<String, String>();
            }

            params.put("account", account);
        }

        String baseUrl = build_url(action);
        if ("GET".equals(method)) {
            baseUrl = new ParameterList(params).appendTo(baseUrl);
        }

        OAuthService service = new ServiceBuilder().provider(ContextIOApi.class).apiKey(key).apiSecret(secret).debugStream(System.out).build();

        OAuthRequest request = new OAuthRequest(Verb.GET, baseUrl);

        Token nullToken = new Token("", "");
        service.signRequest(nullToken, request);

        Response oauthResponse = request.send();

        lastResponse = new ContextIOResponse(oauthResponse.getCode(), request.getHeaders(), oauthResponse.getHeaders(), oauthResponse);
        if (lastResponse.hasError) {
            return null;
        } else {
            return lastResponse;
        }
    }

    public Map<String, String> filterParams(Map<String, String> givenParams, String[] validParams) {
        Map<String, String> filteredParams = new HashMap<String, String>();

        for (String validKey : validParams) {
            for (String givenKey : givenParams.keySet()) {
                if (givenKey.equalsIgnoreCase(validKey)) {
                    filteredParams.put(validKey, givenParams.get(givenKey));
                }
            }
        }

        return filteredParams;
    }
}
