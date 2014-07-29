package conexao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorRede {
	
	private static ServerSocket server;
	
	public static void start () {
		try {
			
			server = new ServerSocket(5000);
			
			while (true) {
				System.out.println("Aguardando conexão com o cliente");
				Socket client = server.accept();
				System.out.println("Nova conexão com o cliente");
				
				/*cria nova thread para tratar a conexão*/
				TrataCliente tc = new TrataCliente(client);
				new Thread(tc).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeServer(){
		try {
			server.close();
		} catch (IOException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
		}
	}
} 
