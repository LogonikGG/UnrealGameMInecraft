package ru.logonik.unrealminecraft.models;

public class TakeProductsEvent {

    private final Gamer gamer;
    private boolean success = false;

    public TakeProductsEvent(Gamer gamer) {
        this.gamer = gamer;
    }

    public Gamer getGamer() {
        return gamer;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
