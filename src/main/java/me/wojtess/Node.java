package me.wojtess;

public interface Node {


    Status tick(BehaviorContext context) throws Throwable;

    enum Status {
        SUCCESS,
        FAILURE,
        RUNNING
    }
}
