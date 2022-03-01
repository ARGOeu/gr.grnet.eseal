/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.exception;

/**
 *
 * @author ZhukovA
 */
public enum AttachmentValidationResult {
    VALID(0, "file.validation.valid"), 
    EMPTY_FILE(1, "file.validation.zero_size"), // file is null or size = 0 
    NO_SIGNATURES(2, "file.validation.no_signatures"), // no signatures present
    INVALID_SIGNATURES(3, "file.validation.invalid_signatures"), // there are invalid signatures
    INVALID_SIGNATURES_INDICATION(4, "file.validation.invalid_signatures_indication"), // there are invalid signatures (due to check on individual signatures Indication field)
    NON_QUAL_SIGNATURES(5, "file.validation.non_qual"), // there are signatures present that are non eIDAS qualified
    INVALID_COUNT_ACT_JOINTLY(6, "file.validation.invalid_count_act_jointly"), // business rule, signatures count is not what we expect
    INVALID_COUNT_SINGLE(7, "file.validation.invalid_count_single"), // business rule, signatures count is not what we expect
    NON_QUAL_NON_ADV_SIGNATURES(8, "file.validation.non_qual_neither_advanced"), // The uploaded document contains digital signatures which are not qualified or advanced.
    FAILED_CALL(99, "file.validation.failed_call"); // failure while calling validation web-service, or web-service returned unparseable result
    
    private final int value;
    private final String messageId;
    
    AttachmentValidationResult(int value, String messageId) {
        this.value = value;
        this.messageId = messageId;
    }

    public int getValue() {
        return value;
    }
    
    public String getMessageId() {
        return messageId;
    }
}
