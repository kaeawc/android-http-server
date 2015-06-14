package io.kaeawc.httpwidgets.network.events;

public class NetworkResponse {

    public boolean success;
    public NetworkRequest request;
    public String response;

    public NetworkResponse(NetworkRequest request, boolean success, String response) {
        this.request = request;
        this.success = success;
        this.response = response;
    }
}
