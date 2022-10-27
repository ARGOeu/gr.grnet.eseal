/*
 * Copyright (C) 2020 Unisystems.gr
 * All rights Reserved
 */
package gr.unisystems.ethemisid.service.dss.exception;

/**
 *
 * @author ZhukovA
 */
public class DssException extends Exception {
   
    gr.unisystems.ethemisid.service.dss.model.Error error;
    
    public DssException() {
      super();
   }
   
   public DssException(String message) {
      super(message);
   }
   
   public DssException(gr.unisystems.ethemisid.service.dss.model.Error error) {
       super(error != null ? error.getMessage() : "");
       this.error = error;
   }
   
   public gr.unisystems.ethemisid.service.dss.model.Error getError() {
       return error;
   }
   
   @Override
   public String toString() {
       if (error != null) {
           return error.toString();
       }
       else if (getMessage() != null && getMessage().trim().length() > 0) {
           return this.getMessage();
       }
       else {
           return "Both error and message fields of the exception are null - Unknown exception";
       }
   }
}
