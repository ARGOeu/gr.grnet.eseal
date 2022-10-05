/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.unisystems.ethemisid.service.dss;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;


public class RestClient {
   private String _url;
   private Client client = null;
   private int _connectionTimeoutMs = 0; //5000;
   private int _socketTimeoutMs = 0;//10000;
   
   public void config(String url, int connectionTimeout, int socketTimeout) {
      _url = url;
      _connectionTimeoutMs = connectionTimeout;
      _socketTimeoutMs = socketTimeout;
   }
   
   protected Client getClient() {
      if (client == null) {
         client = buildClient(); // 2.x : ClientBuilder.newClient();
      }
      return client;
   }
   
   protected String getUrl() {
      return _url;
   }
   
   public Client buildClient() {
      MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
      connectionManager.getParams().setConnectionTimeout(_connectionTimeoutMs);
      connectionManager.getParams().setSoTimeout(_socketTimeoutMs);
      connectionManager.getParams().setDefaultMaxConnectionsPerHost(50);
      HttpClient httpClient = new HttpClient(connectionManager);
      ApacheHttpClientHandler clientHandler = new ApacheHttpClientHandler(httpClient);
      ClientHandler root = new ApacheHttpClient(clientHandler);
      ClientConfig config = new DefaultApacheHttpClientConfig();
      
      // the following is necessary for the automatic marshalling of classes into JSON representation
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      config.getSingletons().add(new JacksonJsonProvider(mapper));

      // TODO: neither the above code, nor the below line, seem to work - will do manual convertion using new ObjectMapper() before sending.
      //config.getSingletons().add(new ObjectMapperContextResolver());
      
      Client client = new Client(root, config);
      return client;
   }
}
