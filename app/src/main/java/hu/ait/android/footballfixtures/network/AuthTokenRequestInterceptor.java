package hu.ait.android.footballfixtures.network;

import retrofit.RequestInterceptor;

public class AuthTokenRequestInterceptor implements RequestInterceptor {
    private static final String AUTH_HEADER = "X-Auth-Token";
    private static final String AUTH_TOKEN = "e0f8b895ea55413c9d1e3b754e8f261b";

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader(AUTH_HEADER, AUTH_TOKEN);
    }

}
