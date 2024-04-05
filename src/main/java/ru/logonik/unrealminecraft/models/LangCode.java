package ru.logonik.unrealminecraft.models;

// <-- Generated code--->
public enum LangCode {
    UNKNOWN_ERROR("exception.unknown_error"),
    INFO_COMMAND_ARENA("commands_info.info_command_arena"),
    SUCCESS("result.success");

    private final String value;

    LangCode(String code) {
        value = code;
    }

    public String getValue() {
        return value;
    }
}