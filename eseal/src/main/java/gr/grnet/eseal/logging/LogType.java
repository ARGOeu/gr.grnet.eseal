package gr.grnet.eseal.logging;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LogType {

    REQUEST_LOG("request_log"),
    BACKEND_LOG("backend_log"),
    SERVICE_LOG("service_log");

    private final String type;

     LogType(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

}
