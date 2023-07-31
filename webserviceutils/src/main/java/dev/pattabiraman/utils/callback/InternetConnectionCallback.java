package dev.pattabiraman.utils.callback;

public interface InternetConnectionCallback {
    void onInternetConnected(boolean isConnected);
    void onInternetDisconnected(boolean isConnected);
}
