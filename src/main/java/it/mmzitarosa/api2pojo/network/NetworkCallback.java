package it.mmzitarosa.api2pojo.network;

import org.jetbrains.annotations.Nullable;

public interface NetworkCallback {

    public void onSuccess(String responseString, String url);

    public void onError(String s, @Nullable Exception e);

}
