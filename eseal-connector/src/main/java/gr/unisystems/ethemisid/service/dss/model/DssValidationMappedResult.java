/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DssValidationMappedResult {
   private String json;
   Logger log = LoggerFactory.getLogger(DssValidationMappedResult.class);
   private boolean validReply = true;
   private String errorDescr = "";
   private String validationTime = "";
   private Integer signaturesCount = 0;
   private Integer validSignaturesCount = 0;
   private List<DssValidationPerSignature> signatures = new ArrayList<DssValidationPerSignature>();
   
   private static Set<String> allowedQualifications = new HashSet<>(Arrays.asList("QESig", "QESeal", "QES?"));
   private static Set<String> allowedQualificationsInclAdvanced = new HashSet<>(Arrays.asList("QESig", "QESeal", "QES?", "AdESig-QC", "AdESeal-QC", "AdES?-QC"));
   
   public DssValidationMappedResult(String json) {
      this.json = json;
   }
   
   public String asHTML() {
      String originalJson = getJson();
      String result = "";
      
      try {
         
         ObjectMapper o = new ObjectMapper();
         setJson("");
         result = o.writerWithDefaultPrettyPrinter().writeValueAsString(this);
         
         result = "<pre>" + result + "</pre><br/><br/><pre>" + originalJson + "</pre>";
         
         this.setJson(originalJson);
         
      } catch (Exception ex) {
         String logJson = "";
         if (getJson() != null && getJson().length() > 100) {
            logJson = getJson().substring(0, 100);
         } else {
            logJson = getJson();
         }
         log.warn("Couldn't pretty print JSON (" + logJson + "...) : " + ex.getMessage());
      }
      
      return result;
   } 
   
   public boolean isContainsNonQualifiedSignatures() {
       boolean ret = false;
       int index = 0;
       for (DssValidationPerSignature sig : getSignatures()) {
           if (!allowedQualifications.contains(sig.getQualification())) {
               log.debug("found non-qualified signature on index " + index + " with qualification = " + sig.getQualification());
               ret = true;
               break;
           }
           index++;
       }
       
       return ret;
   }
   
   public boolean isContainsNonQualifiedAndNonAdvancedSignatures() {
       boolean ret = false;
       int index = 0;
       for (DssValidationPerSignature sig : getSignatures()) {
           if (!allowedQualificationsInclAdvanced.contains(sig.getQualification())) {
               log.debug("found non-qualified and non-advanced  signature on index " + index + " with qualification = " + sig.getQualification());
               ret = true;
               break;
           }
           index++;
       }
       
       return ret;
   }   
   
   // there is another way to determine this with validSignaturesCount
   public boolean isContainsInvalidSignaturesDueToIndicationCheck() {
       boolean ret = false;
       int index = 0;
       for (DssValidationPerSignature sig : getSignatures()) {
           if (!"TOTAL_PASSED".equals(sig.getIndication())) {
               log.debug("found invalid signature on index " + index + " with indication = " + sig.getIndication());
               ret = true;
               break;
           }
           index++;
       }
       
       return ret;
   }   

   public String getErrorDescr() {
      return errorDescr;
   }

   public void setErrorDescr(String errorDescr) {
      this.errorDescr = errorDescr;
   }

   public String getValidationTime() {
      return validationTime;
   }

   public void setValidationTime(String validationTime) {
      this.validationTime = validationTime;
   }

   public Integer getSignaturesCount() {
      return signaturesCount;
   }

   public void setSignaturesCount(Integer signaturesCount) {
      this.signaturesCount = signaturesCount;
   }

   public Integer getValidSignaturesCount() {
      return validSignaturesCount;
   }

   public void setValidSignaturesCount(Integer validSignaturesCount) {
      this.validSignaturesCount = validSignaturesCount;
   }

   public List<DssValidationPerSignature> getSignatures() {
      return signatures;
   }

   public void setSignatures(List<DssValidationPerSignature> signatures) {
      this.signatures = signatures;
   }

   public static Set<String> getAllowedQualifications() {
      return allowedQualifications;
   }

   public static void setAllowedQualifications(Set<String> aAllowedQualifications) {
      allowedQualifications = aAllowedQualifications;
   }

   public static Set<String> getAllowedQualificationsInclAdvanced() {
      return allowedQualificationsInclAdvanced;
   }

   public static void setAllowedQualificationsInclAdvanced(Set<String> aAllowedQualificationsInclAdvanced) {
      allowedQualificationsInclAdvanced = aAllowedQualificationsInclAdvanced;
   }

   public String getJson() {
      return json;
   }

   public void setJson(String json) {
      this.json = json;
   }

   public boolean isValidReply() {
      return validReply;
   }

   public void setValidReply(boolean validReply) {
      this.validReply = validReply;
   }
}
