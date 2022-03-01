/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.model;

public class DssValidationSignatureRequest {
   private SignedDocument signedDocument;

   public SignedDocument getSignedDocument() {
      return signedDocument;
   }

   public void setSignedDocument(SignedDocument signedDocument) {
      this.signedDocument = signedDocument;
   }
}
