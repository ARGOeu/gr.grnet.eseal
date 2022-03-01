/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.model;

public class DssSignRequest {
    private String username;
    private String password;
    private String key;
    private byte[] imageBytes;
    private ToSignDocument toSignDocument;

   public ToSignDocument getToSignDocument() {
      return toSignDocument;
   }

   public void setToSignDocument(ToSignDocument toSignDocument) {
      this.toSignDocument = toSignDocument;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public byte[] getImageBytes() {
      return imageBytes;
   }

   public void setImageBytes(byte[] imageBytes) {
      this.imageBytes = imageBytes;
   }
}
