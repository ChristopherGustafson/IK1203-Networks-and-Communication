import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

public class MyRunnable implements Runnable{

  Socket connection;

  //HTTP responses
  String HTTPHeader = "HTTP/1.1 200 OK\r\n\r\n";
  String BadRequest = "HTTP/1.1 400 Bad Request\r\n";
  String NotFound = "HTTP/1.1 404 Not Found\r\n";

  //Initiate variables
  String GET;
  String host;
  String port;
  String data;
  String response;
  String[] parameters;

  public MyRunnable(Socket connection){
    this.connection = connection;
  }

  public void run(){
    System.out.println("New runnable");
    try{
      BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      DataOutputStream output = new DataOutputStream(connection.getOutputStream());

      //Read GET request from client
      GET = input.readLine();

      host = null;
      port = null;
      data = null;
      response = null;

      //If request form client isn't null, proceed to examine request
      if(GET != null){

        parameters = GET.split("[?\\&\\=\\ ]");

        //Split GET request into parts and check for host, port or string parameters
        for(int i = 0; i < parameters.length; i++){
          if(parameters[i].equals("hostname")){
            host = parameters[++i];
          }
          else if(parameters[i].equals("port")){
            port = parameters[++i];
          }
          else if(parameters[i].equals("string")){
            data = parameters[++i];
          }
        }

        System.out.println("Host: " + host);
        System.out.println("Port: " + port);

        if(parameters[1].equals("/ask") && host != null && port != null){

          //If client has sent a string, send it with the request to TCPClient
          if(data != null){
            try{
              response = TCPClient.askServer(host, Integer.parseInt(port), data);
              output.writeBytes(HTTPHeader + response);
            }
            catch(Exception e){
              output.writeBytes(NotFound);
            }
          }
          else{
            try{
              response = TCPClient.askServer(host, Integer.parseInt(port));
              output.writeBytes(HTTPHeader + response);
            }
            catch(Exception e){
              output.writeBytes(NotFound);
            }
          }
        }
        else{
          //If url isn't written properly, send 400 Bad Request to client
          output.writeBytes(BadRequest);
        }
      }
    connection.close();
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }

}