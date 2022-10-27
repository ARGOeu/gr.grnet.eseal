/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.exception;

/**
 *
 * @author ZhukovA
 */
public class AttachmentValidationException extends Exception {
    public AttachmentValidationException() {
        super();
    }
    
    public AttachmentValidationException(Exception cause) {
        super(cause.getMessage(), cause);
    }
}
