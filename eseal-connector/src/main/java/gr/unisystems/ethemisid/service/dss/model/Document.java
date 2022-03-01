/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.model;

public class Document {
   private byte[] bytes;
   private String name;    

   /**
    * @return the bytes
    */
   public byte[] getBytes() {
      return bytes;
   }

   /**
    * @param bytes the bytes to set
    */
   public void setBytes(byte[] bytes) {
      this.bytes = bytes;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }
}
