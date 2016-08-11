package org.epiccraft.dev.digitalstorm;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Project DigitalStorm
 */
public class DigitalFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[DigitalStorm] [" + record.getLevel().getName() + "] ");
        stringBuffer.append(record.getMessage());
        stringBuffer.append("\n");
        return stringBuffer.toString();
    }

}
