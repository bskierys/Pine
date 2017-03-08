package com.github.bskierys.pine;

/**
 * Interface to make a action on output log message.
 */
public interface LogAction {
    /**
     * Invoked the same way as {@link Pine#log} is.
     */
    void action(int priority, String tag, String message, Throwable t);
}
