/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.unisystems.ethemisid.service.dss;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class RestClient {
   private String url;
   private Client client = null;
   
   public void config(String url) {
      this.url = url;
   }
   
   protected Client getClient() {
      if (client == null) {
         client = ClientBuilder.newClient();
      }
      return client;
   }
   
   protected String getUrl() {
      return url;
   }
}
