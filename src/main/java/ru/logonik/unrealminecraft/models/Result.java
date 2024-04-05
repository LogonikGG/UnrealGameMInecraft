package ru.logonik.unrealminecraft.models;

import java.util.Map;

public class Result {
    private final boolean success;
    private final LangCode code;

    public Result(boolean success, LangCode code) {
        this.success = success;
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public LangCode getCode() {
        return code;
    }

    public Map<String, String> getExternalData() {
        return null;
    }
}
