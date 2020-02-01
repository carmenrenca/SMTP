package ejercicio_4;

import java.io.IOException;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

import utiles.trycatch;

public class ClienteSMTP3 {
  
	 public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, UnrecoverableKeyException, KeyStoreException, IOException{
		 trycatch t = new trycatch();
	        //se crea cliente SMTP seguro
	        AuthenticatingSMTPClient client= new AuthenticatingSMTPClient();
	        //datos del usuario y del servidor

	        System.out.println("Introduce el servidor SMTP");
	        
	        String server=t.try_String();
	        System.out.println("Necesta negociacion TLS (S,N)");
	        System.out.println("usuario");
	        String username=t.try_String();
	        System.out.println("contraseña");

	        String password=t.try_String();
	        System.out.println("puerto");

	        int puerto = t.try_int();
	        System.out.println("correo del remitente");

	        String remitente=t.try_String();
	        System.out.println("correo del destinatario");

	        String destinatario=t.try_String();
	        System.out.println("Asunto");
	        String asunto=t.try_String();
	        System.out.println("Introduce el mensaje");
	        String mensaje= t.try_String();
	        try{
	            int respuesta;
	            //creacion de la clave para establecer un canal seguro
	            KeyManagerFactory kmf= KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	           kmf.init(null, null);
	           
	            KeyManager km= kmf.getKeyManagers()[0];
	            //conectamos con el servidor smtp

	            client.connect(server,puerto);
	            System.out.println("1 -"+client.getReplyString());
	            //se establece la clave para la comunicacion segura
	            client.setKeyManager(km);
	            respuesta=client.getReplyCode();
	            if(!SMTPReply.isPositiveCompletion(respuesta)){
	                client.disconnect();
	                System.out.println("CONEXION RECHAZADA.");
	                System.exit(1);
	            }
	            //se envia el comando EHLO
	            client.ehlo(server);
	            System.out.println("2 - "+client.getReplyStrings());
	            
	            //NECESITA NEGOCIACION TLS-MODO NO INPLICITO
	            //Se ejecuta el comando STARTTLS y se comprueba si es true
	            if(client.execTLS()) {
	            	System.out.println("3 - "+client.getReplyString());
	           
	            //comprobamos la autentificacion de nuestra cuenta de correo
	            if(client.auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, username, password)) {
	            	System.out.println("4 - "+client.getReplyStrings());
	            
	            	
	            	//creamos la cabecera del mensaje
	            	SimpleSMTPHeader cabecera = new SimpleSMTPHeader(remitente, destinatario, asunto);
	            
	            	client.setSender(remitente);
	            	client.addRecipient(destinatario);
	            	System.out.println("5 - "+client.getReplyStrings());
	         
	            	Writer writer = client.sendMessageData();
	            	if(writer==null) {//fallo
	            		System.out.println("FALLO AL ENVIAR DATA.");
	            		System.exit(1);
	            		
	            	}
	            	writer.write(cabecera.toString());//cabecera
	            	writer.write(mensaje);//luego mensaje
	            	writer.close();
	            	System.out.println("6 - "+client.getReplyString());
	            	
	            	boolean exito = client.completePendingCommand();
	            	System.out.println("7 - "+client.getReplyStrings());
	            	
	            	if(!exito) {//fallo
	            		System.out.println("FALLO AL FINALIZAR TRANSACCION.");
	            		System.exit(1);
	            		
	            	}else {
	            		System.out.println("MENSAJE ENVIADO CON EXITO.....");
	            	}
	            }else {
	            	System.out.println("USUARIO NO AUTENTICADO.");
	            }
	            }else {
	            	System.out.println("FALLO AL EJECUTAR STARTTLS.");
	            }
	        
	         
	            
		        }catch(IOException e){
		            System.err.println("Could not connect to server.");
		            e.printStackTrace();
		            System.exit(1);
		        }
		        try{
		            client.disconnect();
	
		        }catch(IOException f){f.printStackTrace();}
		        System.out.println("Fin de envío");
		        System.exit(0);
		    }
	 
	 

}
