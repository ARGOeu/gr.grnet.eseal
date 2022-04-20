/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.mapper;

import gr.unisystems.ethemisid.service.dss.exception.DssException;
import gr.unisystems.ethemisid.service.dss.model.DssValidationMappedResult;
import gr.unisystems.ethemisid.service.dss.model.DssValidationPerSignature;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author ZhukovA
 */
public class DssValidationMapper {

   public static DssValidationMappedResult map(String json) {
      DssValidationMappedResult result = new DssValidationMappedResult(json);

      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode;
      try {
         rootNode = objectMapper.readTree(json);

         JsonNode node = rootNode.findValue("ValidationTime");
         if (node != null) {
            result.setValidationTime(node.asText());
         }
         
         node = rootNode.findValue("SignaturesCount");
         if (node != null) {
            result.setSignaturesCount(node.asInt());
         }

         node = rootNode.findValue("ValidSignaturesCount");
         if (node != null) {
            result.setValidSignaturesCount(node.asInt());
         }

         node = rootNode.findValue("SimpleReport");
         if (node == null) {
            throw new DssException("SimpleReport missing");
         }

         List<JsonNode> sigs = node.findValues("signatureOrTimestamp");
         if (sigs != null) {
            for (JsonNode sigNode : sigs) {

               if (!sigNode.isNull()) {
                  
                  DssValidationPerSignature sig = null;   
                  if (sigNode.isArray()) {
                     Iterator<JsonNode> it = sigNode.getElements();
                     while (it.hasNext()) {
                        JsonNode elem = it.next();
                        sig = mapSingleSignature(elem);
                        result.getSignatures().add(sig);
                     }
                  } else {
                     sig = mapSingleSignature(sigNode);
                     result.getSignatures().add(sig);
                  }
                  
               }
            }
         }
      } catch (Exception ex) {
         result.setValidReply(false);
         result.setErrorDescr(ex.getMessage());
      }

      return result;
   }

   private static DssValidationPerSignature mapSingleSignature(JsonNode sigNode) throws DssException {
      JsonNode node;
      DssValidationPerSignature sig = new DssValidationPerSignature();
      node = sigNode.get("Signature");
      if (node != null) {
         sig.setTimestamp(false);
      } else {
         node = sigNode.get("Timestamp");
         if (node != null) {
            sig.setTimestamp(true);
         }
      }
      
      if (node == null) {
         throw new DssException("Neither Signature nor Timestamp in signatureOrTimestamp");
      }
      
      node = node.findValue("SignatureLevel");
      if (node != null) {
         sig.setSignatureLevelValue(node.path("value").asText());
         sig.setSignatureLevelDescription(node.path("description").asText());
      }
      
      node = sigNode.findValue("Indication");
      if (node != null) {
         sig.setIndication(node.asText());
      }
      
      node = sigNode.findValue("SubIndication");
      if (node != null) {
         sig.setSubIndication(node.asText());
      }
      
      node = sigNode.findValue("CertificateChain");
      if (node != null) {
         JsonNode certNode = node.path("Certificate");
         if (certNode.isArray()) {
            certNode = certNode.get(0); // we are interested only in the first (the lowest) certificate in the chain
         }
         
         node = certNode.findValue("qualifiedName");
         if (node != null) {
            sig.setSubjectCN(node.asText());
         }
      }
      return sig;
   }
}
