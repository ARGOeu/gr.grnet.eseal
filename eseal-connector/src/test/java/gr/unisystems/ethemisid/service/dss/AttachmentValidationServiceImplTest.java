/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.unisystems.ethemisid.service.dss;

import gr.unisystems.ethemisid.service.dss.exception.AttachmentValidationFoundException;
import gr.unisystems.ethemisid.service.dss.model.DssValidationMappedResult;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ZhukovA
 */
public class AttachmentValidationServiceImplTest {
   
   public AttachmentValidationServiceImplTest() {
   }

   @Test(expected = AttachmentValidationFoundException.class)
   public void testValidateNoConfig() throws Exception {
      System.out.println("testValidateNoConfig");
      byte[] file = null;
      String filename = "";
      AttachmentValidationService instance = new AttachmentValidationServiceImpl(false);
      DssValidationMappedResult result = instance.validate(file, filename);
      assertNotNull(result);
   }
   
   @Test(expected = AttachmentValidationFoundException.class)
   public void testValidateConfig() throws Exception {
      System.out.println("testValidateConfig");
      byte[] file = null;
      String filename = "";
      AttachmentValidationService instance = new AttachmentValidationServiceImpl(false);
      instance.config("http://localhost:8080/esig");
      DssValidationMappedResult result = instance.validate(file, filename);
      assertNotNull(result);
   }
   
}
