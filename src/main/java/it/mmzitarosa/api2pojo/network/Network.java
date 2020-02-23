package it.mmzitarosa.api2pojo.network;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class Network {

    private OkHttpClient client;

    public Network() {
        client = new OkHttpClient();
    }

    public void doGet(@NotNull String url, @NotNull NetworkCallback responseCallback) {
        doGet(url, null, responseCallback);
    }


    private void doGet(@NotNull String url, @Nullable Map<String, Object> parameters, @NotNull NetworkCallback responseCallback) {
        Verb verb = Verb.GET;
        try {
            Request request = buildRequest(verb, url, parameters);
            callAndResponse(request, responseCallback);
        } catch (Exception e) {
            onError(verb, url, responseCallback, e);
        }
    }

    public void doPost(@NotNull String url, @Nullable Map<String, Object> parameters, @NotNull NetworkCallback responseCallback) {
        Verb verb = Verb.POST;
        try {
            Request request = buildRequest(verb, url, parameters);
            callAndResponse(request, responseCallback);
        } catch (Exception e) {
            onError(verb, url, responseCallback, e);
        }
    }

    private void onError(Verb verb, String url, NetworkCallback networkCallback, Exception e) {
        networkCallback.onError("Error building the " + verb.name() + " request to: " + url, e);
    }

    private void callAndResponse(Request request, NetworkCallback responseCallback) {
        call(request, responseCallback);
    }

    private void call(Request request, NetworkCallback responseCallback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                responseCallback.onError("Call to: " + call.request().url().toString() + " failed.", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String responseString = responseBody.string();
                    responseCallback.onSuccess(responseString, call.request().url().toString());
                } else {
                    responseCallback.onError("Call to: " + call.request().url().toString() + " failed. Empty response body.", null);
                }
            }
        });
    }

    private Request buildRequest(Verb verb, @NotNull String url, @Nullable Map<String, Object> parametersMap) throws MalformedURLException, JSONException, NetworkException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        Request.Builder requestBuilder = new Request.Builder();

        switch (verb) {
            case GET:
                if (parametersMap != null) {
                    for (String key : parametersMap.keySet()) {
                        urlBuilder.addQueryParameter(key, (String) parametersMap.get(key));
                    }
                }
                requestBuilder.get();
                break;
            case POST:
                JSONObject jsonBody = new JSONObject();
                if (parametersMap != null) {
                    for (String key : parametersMap.keySet()) {
                        jsonBody.put(key, parametersMap.get(key));
                    }
                }
                RequestBody requestBody = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));
                requestBuilder.post(requestBody);
                break;
            default:
                throw new NetworkException("\"" + verb.name() + "\" verb not yet implemented.");
        }
        URL requestUrl = new URL(urlBuilder.build().toString());
        requestBuilder.url(requestUrl);
        return requestBuilder.build();
    }

    private enum Verb {
        GET,
        POST,
        ;
    }

}
