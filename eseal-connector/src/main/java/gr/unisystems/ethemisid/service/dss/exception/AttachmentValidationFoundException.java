/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.exception;

/**
 *
 * @author ZhukovA
 * this one is supposed to happen when we have found AttachmentLog entry and are able to update it with the latest validation result
 */
public class AttachmentValidationFoundException extends AttachmentValidationException {
    private final AttachmentValidationResult _result;
    public AttachmentValidationFoundException(AttachmentValidationResult result) {
        _result = result;
    }
    
    public AttachmentValidationFoundException(AttachmentValidationResult result, Exception cause) {
        super(cause);
        _result = result;
    }
    
    public AttachmentValidationResult getResult() {
        return _result;
    }
}
