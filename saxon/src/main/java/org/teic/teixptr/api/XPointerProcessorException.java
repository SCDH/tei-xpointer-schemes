package org.teic.teixptr.api;

/**
 * An exception that occurred while processing an XPointer.
 */
public class XPointerProcessorException extends Exception {
    public XPointerProcessorException(String msg) {
        super(msg);
    }
    public XPointerProcessorException(Throwable cause) {
        super(cause);
    }
    public XPointerProcessorException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
