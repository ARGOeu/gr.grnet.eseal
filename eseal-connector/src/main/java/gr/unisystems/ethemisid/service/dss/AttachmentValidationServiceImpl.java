/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss;

import com.sun.jersey.api.client.ClientRequest.Builder;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import gr.unisystems.ethemisid.service.dss.exception.AttachmentValidationException;
import gr.unisystems.ethemisid.service.dss.exception.AttachmentValidationFoundException;
import gr.unisystems.ethemisid.service.dss.exception.AttachmentValidationResult;
import gr.unisystems.ethemisid.service.dss.mapper.DssValidationMapper;
import gr.unisystems.ethemisid.service.dss.model.DssValidationMappedResult;
import gr.unisystems.ethemisid.service.dss.model.DssValidationSignatureRequest;
import gr.unisystems.ethemisid.service.dss.model.SignedDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentValidationServiceImpl extends RestClient implements AttachmentValidationService {

   DssValidationMappedResult validationResult = null;
   boolean skipValidations = false;
   Logger log = LoggerFactory.getLogger(SigningServiceImpl.class);
   
   public AttachmentValidationServiceImpl(boolean skipValidations) {
      super();
      this.skipValidations = skipValidations; // if true will just make call to the service, but will not validate
      log.debug("Created service with skipValidations = " + skipValidations);
   }

   @Override
   public DssValidationMappedResult validate(byte[] file, String filename) throws AttachmentValidationException {
      if (log.isDebugEnabled()) {
         String fileSize = file != null ? "" + file.length : "null";
         log.debug("validate called for file of size = " + fileSize + " and name = " + (filename != null ? filename : "null"));
      }
      
      try {
         DssValidationSignatureRequest req = prepareValidationInput(file, filename);
         
         WebResource webResource = getClient().resource(getUrl());
         ClientResponse response = webResource.type("application/json").post(ClientResponse.class, req);
         // Response response = getClient().target(getUrl()).request(MediaType.APPLICATION_JSON).post(Entity.entity(req, MediaType.APPLICATION_JSON), DssValidationSignatureRequest.class);
         String body = response.getEntity(String.class);
         log.debug("Validation call returned with response of length = " + (body != null ? body.length() : "null"));
         validationResult = DssValidationMapper.map(body);
      } catch (Exception ex) {
         throw new AttachmentValidationFoundException(AttachmentValidationResult.FAILED_CALL, ex);
      }

      analyzeResult(validationResult);

      // return the result in case of success, in case we need to analyse it further
      return validationResult;
   }

   private DssValidationSignatureRequest prepareValidationInput(byte[] bytes, String name) {
      SignedDocument doc = new SignedDocument();
      doc.setBytes(bytes);
      doc.setName(name);
      DssValidationSignatureRequest req = new DssValidationSignatureRequest();
      req.setSignedDocument(doc);
      return req;
   }

   private void analyzeResult(DssValidationMappedResult validationResult) throws AttachmentValidationException {

      if (validationResult == null) {
         throw new AttachmentValidationFoundException(AttachmentValidationResult.FAILED_CALL);
      }

      if (!skipValidations) {
         if (validationResult.getSignaturesCount() == 0) {
            // einai egkyro na mhn periexei katholou ypografes - tote empiptei sthn periptwsh tou apostyllized file...
            return;
         }

         if (validationResult.getValidSignaturesCount() < validationResult.getSignaturesCount()) {
            throw new AttachmentValidationFoundException(AttachmentValidationResult.INVALID_SIGNATURES);
         }

         if (validationResult.isContainsNonQualifiedAndNonAdvancedSignatures()) {
            throw new AttachmentValidationFoundException(AttachmentValidationResult.NON_QUAL_NON_ADV_SIGNATURES);
         }

         if (validationResult.isContainsInvalidSignaturesDueToIndicationCheck()) {
            throw new AttachmentValidationFoundException(AttachmentValidationResult.INVALID_SIGNATURES_INDICATION);
         }
      }
   }

   public DssValidationMappedResult getValidationResult() {
      return validationResult;
   }
}
