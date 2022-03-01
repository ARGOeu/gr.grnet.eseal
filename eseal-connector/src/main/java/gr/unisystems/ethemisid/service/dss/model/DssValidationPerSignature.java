/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.model;

import javax.xml.bind.annotation.XmlTransient;

public class DssValidationPerSignature {
   private String signatureLevelValue;
   private String signatureLevelDescription;
   private String indication;
   private String subIndication;
   private String subjectCN;
   private Boolean timestamp = null; // indeterminate

   @XmlTransient   
   public String getQualification() {
      return getSignatureLevelValue();
   }
   
   @XmlTransient
   public String getIndication() {
      return indication;
   }

   public String getSignatureLevelValue() {
      return signatureLevelValue;
   }

   public void setSignatureLevelValue(String signatureLevelValue) {
      this.signatureLevelValue = signatureLevelValue;
   }

   public String getSignatureLevelDescription() {
      return signatureLevelDescription;
   }

   public void setSignatureLevelDescription(String signatureLevelDescription) {
      this.signatureLevelDescription = signatureLevelDescription;
   }

   public void setIndication(String indication) {
      this.indication = indication;
   }

   public String getSubIndication() {
      return subIndication;
   }

   public void setSubIndication(String subIndication) {
      this.subIndication = subIndication;
   }

   public String getSubjectCN() {
      return subjectCN;
   }

   public void setSubjectCN(String subjectCN) {
      this.subjectCN = subjectCN;
   }

   public Boolean getTimestamp() {
      return timestamp;
   }

   public void setTimestamp(Boolean timestamp) {
      this.timestamp = timestamp;
   }
}
