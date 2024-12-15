package com.aflr.spring_batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.item.file.FlatFileParseException;

public class TxnItemListener implements ItemReadListener {

    private final static Logger logger = LoggerFactory.getLogger(TxnItemListener.class);

    @Override
    public void onReadError(Exception ex) {
        if (ex instanceof FlatFileParseException parseException) {

            String builder = "an error has occurred when reading "
                    + parseException.getLineNumber()
                    + " the line. Here are its details about the bad input\n "
                    + parseException.getInput()
                    + "\n";

            logger.error(builder, parseException);
        } else {
            logger.error("An error occur ", ex);
        }
    }
}
