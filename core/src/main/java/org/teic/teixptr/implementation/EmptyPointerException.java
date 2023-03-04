package org.teic.teixptr.implementation;

/**
 * A pointer selects an empty node set.
 */
public class EmptyPointerException extends Exception {
    public EmptyPointerException(String msg) {
        super(msg);
    }
    public EmptyPointerException(Throwable cause) {
        super(cause);
    }
    public EmptyPointerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
