/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import gr.unisystems.ethemisid.service.dss.exception.DssException;
import gr.unisystems.ethemisid.service.dss.model.DssSignRequest;
import gr.unisystems.ethemisid.service.dss.model.DssSignResponse;
import gr.unisystems.ethemisid.service.dss.model.ToSignDocument;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SigningServiceImpl extends RestClient implements SigningService {

   Logger log = LoggerFactory.getLogger(SigningServiceImpl.class);
   
   public SigningServiceImpl() {
      super();
      log.debug("Created SigningServiceImpl");
   }

   @Override
   public byte[] sign(byte[] bytes, byte[] imageBytes, String filename, String username, String password, String key, boolean imageVisibility) throws DssException, IOException {
      if (bytes == null) {
         throw new IllegalArgumentException("fileBytes has to be supplied");
      }
      
      log.debug("sign() called for file of size = " + bytes.length + " and name = " + (filename != null ? filename : "null"));

      byte[] ret = null;
      DssSignRequest req = prepareInput(bytes, imageBytes, filename, username, password, key, imageVisibility);
      // debugLogRequest(req); when log4j is used in main Osddydd backend application, somehow Slf4j isDebugEnabled returns false anyway

      // Jersey 2.x
      //DssSignResponse result = getClient().target(getUrl()).request(MediaType.APPLICATION_JSON).post(Entity.json(req), DssSignResponse.class);
      
      WebResource webResource = getClient().resource(getUrl());
      ObjectMapper mapper = ObjectMapperContextResolver.createMapper();
      String strReq = mapper.writeValueAsString(req);
      log.debug("Sending sign request of length: " + strReq.length());
      ClientResponse response = webResource.type("application/json").post(ClientResponse.class, strReq);
      //DssSignResponse result = response.getEntity(DssSignResponse.class);
      String strRet = response.getEntity(String.class);
      log.debug("Received sign response of length: " + strRet.length());
      DssSignResponse result = mapper.readValue(strRet, DssSignResponse.class);
      
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

   private DssSignRequest prepareInput(byte[] bytes, byte[] imageBytes, String fileName, String username, String password, String key, boolean imageVisibility) {
      DssSignRequest ret = new DssSignRequest();
      ret.setUsername(username);
      ret.setPassword(password);
      ret.setKey(key);
      ret.setImageBytes(imageBytes);
      ret.setImageVisibility(imageVisibility);

      ToSignDocument doc = new ToSignDocument();
      doc.setName(fileName);
      doc.setBytes(bytes);
      ret.setToSignDocument(doc);

      return ret;
   }

}
