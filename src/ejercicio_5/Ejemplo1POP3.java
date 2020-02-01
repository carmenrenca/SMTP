package ejercicio_5;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.commons.net.pop3.POP3MessageInfo;
import org.apache.commons.net.pop3.POP3SClient;

public class Ejemplo1POP3 {
	private static void RecuperarCabeceras(POP3MessageInfo[] men, POP3SClient pop3) throws IOException {
		for (int i = 0; i < men.length; i++) {
			System.out.println("Mensaje: "+(i+1));
			POP3MessageInfo msginfo=men[i];
			
			System.out.println("Cabecera del mensaje: ");
			BufferedReader reader=(BufferedReader) pop3.retrieveMessageTop(msginfo.number,0);
			String linea;
			while((linea=reader.readLine())!=null)
				System.out.println(linea.toString());
			reader.close();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String server ="pop.gmail.com", username = "carmenrenca3d@gmail.com",
		password="-------";
		
		int puerto =995;
		POP3SClient pop3 = new POP3SClient(true);
		try {
			//nos conectamos la servidor
			
			pop3.connect(server, puerto);
			
			System.out.println("conexion realizada al servidor POP3 "+server);
			//iniciamos sesion 
			System.out.println(pop3.getReplyString());
			if(!pop3.login(username, password))System.err.println("Error al hacer login");
			else {
				//obtenemos todos los mensajes de un array 
				POP3MessageInfo[] men= pop3.listMessages();
				if(men== null) System.out.println("Imposible recuperar mensajes.");
				else System.out.println("Nº de mensajes "+men.length);
				
				//finalizar sesion
				RecuperarCabeceras(men,pop3);
				//System.out.println(pop3.getReplyString().);
				pop3.logout();
			}
		
			pop3.disconnect();
		}catch(IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
			
		}
		System.exit(0);
	}

}
