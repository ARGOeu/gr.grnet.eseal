/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss;

import gr.unisystems.ethemisid.service.dss.exception.AttachmentValidationException;
import gr.unisystems.ethemisid.service.dss.model.DssValidationMappedResult;

/**
 *
 * @author ZhukovA
 */
public interface AttachmentValidationService {
   public void config(String url);
   public DssValidationMappedResult validate(byte[] file, String filename) throws AttachmentValidationException;
}
