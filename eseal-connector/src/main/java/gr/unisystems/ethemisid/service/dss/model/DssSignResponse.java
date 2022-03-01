/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.model;

public class DssSignResponse {
    private byte[] signedDocumentBytes;
    private Error error;

   public byte[] getSignedDocumentBytes() {
      return signedDocumentBytes;
   }

   public void setSignedDocumentBytes(byte[] signedDocumentBytes) {
      this.signedDocumentBytes = signedDocumentBytes;
   }

   public Error getError() {
      return error;
   }

   public void setError(Error error) {
      this.error = error;
   }
}
