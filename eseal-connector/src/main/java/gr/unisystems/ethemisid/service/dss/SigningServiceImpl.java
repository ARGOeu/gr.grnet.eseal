/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.unisystems.ethemisid.service.dss.exception.DssException;
import gr.unisystems.ethemisid.service.dss.model.DssSignRequest;
import gr.unisystems.ethemisid.service.dss.model.DssSignResponse;
import gr.unisystems.ethemisid.service.dss.model.ToSignDocument;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ApplicationScoped
public class SigningServiceImpl extends RestClient implements SigningService {

   Logger log = LoggerFactory.getLogger(SigningServiceImpl.class);
   
   public SigningServiceImpl() {
      super();
      log.debug("Created SigningServiceImpl");
   }

   @Override
   public byte[] sign(byte[] bytes, byte[] imageBytes, String filename, String username, String password, String key) throws DssException {
      if (bytes == null) {
         throw new IllegalArgumentException("fileBytes has to be supplied");
      }
      
      if (log.isDebugEnabled()) {
         log.debug("validate called for file of size = " + bytes.length + " and name = " + (filename != null ? filename : "null"));
      }      

      byte[] ret = null;
      DssSignRequest req = prepareInput(bytes, imageBytes, filename, username, password, key);
      debugLogRequest(req);

      DssSignResponse result = getClient().target(getUrl()).request(MediaType.APPLICATION_JSON).post(Entity.json(req), DssSignResponse.class);
      if (result == null) {
         throw new DssException("Call to sign returned null");
      } else if (result.getError() != null) {
         throw new DssException(result.getError());
      } else {
         ret = result.getSignedDocumentBytes();
      }

      return ret;
   }

   private void debugLogRequest(DssSignRequest req) {
      if (log.isDebugEnabled()) {
         try {
            ObjectMapper objectMapper = new ObjectMapper();
            log.debug("Sending : " + objectMapper.writeValueAsString(req));
         } catch (Exception ex) {
            log.debug("Couldn't convert to JSON - error ahead!");
         }
      }
   }

   private DssSignRequest prepareInput(byte[] bytes, byte[] imageBytes, String fileName, String username, String password, String key) {
      DssSignRequest ret = new DssSignRequest();
      ret.setUsername(username);
      ret.setPassword(password);
      ret.setKey(key);
      ret.setImageBytes(imageBytes);

      ToSignDocument doc = new ToSignDocument();
      doc.setName(fileName);
      doc.setBytes(bytes);
      ret.setToSignDocument(doc);

      return ret;
   }

}
