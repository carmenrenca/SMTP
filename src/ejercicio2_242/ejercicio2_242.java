package ejercicio2_242;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDateTime;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import utiles.trycatch;

public class ejercicio2_242 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		trycatch t = new trycatch();
		FTPClient cliente = new FTPClient();
		String usuario;
		int n=0;
		do {
			
		
		String servFTP= "localhost";
		System.out.println("Nos conectamos a: "+servFTP);
		System.out.println("Introduce el usuario");
		
		 usuario= t.try_String();
		 System.out.println("Introduce la contraseña");
		String clave=t.try_String();
	
		try {
			//CREAMOS LA CONEXION AL SERVIDOR FTP
			cliente.connect(servFTP);
			cliente.enterLocalPassiveMode(); //mode pasivo
			boolean login =cliente.login(usuario, clave);
			if(login) {
				//NOS MOVEMOS A EL DIRECTORIO /LOG 
				System.out.println("Login correcto...");
				n++;
				cliente.changeWorkingDirectory("/LOG");
				FTPFile[] files= cliente.listFiles();
				System.out.println("Ficheros en el directorio actual: "+files.length);
				//array para visualozar el tipo de fichero
				String tipos[]= {"Fichero","Directorio", "Enlace simb."};
				for(int i=0; i<files.length; i++) {
					System.out.println("\t"+files[i].getName()+" =>"+tipos[files[i].getType()]);
				}
				System.out.println("Directorio actual "+ cliente.printWorkingDirectory());
			files =cliente.listFiles();
				String HoraConexion= "\nHora de conexión: "+LocalDateTime.now();
				//Le pasamos al el fichero LOG.TXT.txt la hora de conexion 

				ByteArrayInputStream local = new ByteArrayInputStream(HoraConexion.getBytes("UTF-8"));
			 cliente.appendFile(files[0].getName(), local);
				
			
				
				
				boolean logout =cliente.logout();
				if(logout) System.out.println("Logout del servidor ftp");
				else System.out.println("Error al hacer Logout...");
				//CERRAMOS LA CONEXION AL SERVIDOR
				cliente.disconnect();
				
				System.out.println("Desconectado...");
				
				
				
			}else {
				System.out.println("Login incorrecto...");
		
			}
			}catch(IOException io) {
				io.printStackTrace();
			}
			
			
		}while(!usuario.equalsIgnoreCase("*"));
System.out.println("salimos");


		AuthenticatingSMTPClient client=new AuthenticatingSMTPClient();
			
			String server="smtp.gmail.com";
			String username="carmenrenca3d@gmail.com";
			String password="------";
			int puerto=587;
			String remitente="carmenrenca3d@gmail.com";
			
			try {
				int respuesta;
				
				KeyManagerFactory kmf=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());				
				kmf.init(null, null);
				KeyManager km=kmf.getKeyManagers()[0];
				//CREAMOS LA CONEXION SMTP
				client.connect(server,puerto);
				System.out.println("1 - "+client.getReplyString());
				client.setKeyManager( km);
				
				respuesta=client.getReplyCode();
				if(!SMTPReply.isPositiveCompletion(respuesta)) {
					client.disconnect();
					System.err.println("Conexion rechazada");
					System.exit(1);
				}
					
					client.ehlo(server);
					
					
					if(client.execTLS()) {
				
						
						if(client.auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, username, password)) {
							System.out.println("4 - "+client.getReplyString());
							//Creamos el asunto del correo y el mensaje con el numero de conexiones aprobadas
							String destino1="carmenrenca3d@gmail.com";
							String asunto="Nº Conexiones Aprobadas";
							String mensaje="El numero de conexiones aprobadas es de "+n;
							//creamos la cabecera del correo
							SimpleSMTPHeader cabecera=new SimpleSMTPHeader(remitente,destino1,asunto);
							
							client.setSender(remitente);
							client.addRecipient(destino1);
						
							
							Writer writer=client.sendMessageData();
							
							if(writer==null) {
								System.out.println("FALLO AL ENVIAR DATA");
								System.exit(1);
							}
							
							writer.write(cabecera.toString());
							writer.write(mensaje);
							writer.close();
						
							
							boolean exito=client.completePendingCommand();
			
							
							if(!exito) {
								System.out.println("FALLO AL FINALIZAR TRANSACCION");
								System.exit(1);
							}else
								System.out.println("Mensaje enviado con EXITO");
						}else
								System.out.println("USUARIO NO AUTENTICADO");
					}
					
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println("Could not connect to server");
				e.printStackTrace();
				System.exit(1);
			}
			try {
				client.disconnect();
			} catch (IOException f) {
				// TODO: handle exception
				f.printStackTrace();
			}
			
			System.out.println("Fin de envio");
			System.exit(0);
		}
	
}

