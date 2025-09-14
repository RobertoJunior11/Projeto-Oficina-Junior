package com.impacta.oficina.exceptions;

import static java.lang.String.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class ExceptionResolver {

    public static String getRootException(Throwable ex) {
        return format("%s in class: %s Line: %s",
                getRootCauseMessage(ex),
                getRootCause(ex).getStackTrace()[0].getClassName(),
                getRootCause(ex).getStackTrace()[0].getLineNumber()
        );
    }
}
