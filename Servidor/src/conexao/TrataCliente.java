package conexao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import defines.Message;

public class TrataCliente implements Runnable {
	
	private Socket cliente;
	private DataInputStream dataInput;
	private DAO dao;
	private String nome, senha, deviceId, email;
	private List<String> listDataOut, listDataIn;
	private int tipo, acao;
	
	public TrataCliente (Socket client) {
		this.cliente = client;
		dao = new DAO();
		listDataOut = new ArrayList<String>();
		listDataIn = new ArrayList<String>();
		try {
			dataInput = new DataInputStream(cliente.getInputStream());
		} catch (IOException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		try {
			DatagramServer datagram = new DatagramServer();
			datagram = this.readData();
			
			//dataInput = new DataInputStream(cliente.getInputStream());
			
			/* recebe o número de bytes que serão lidos, desconsiderando o byte de tipo e acao */
			//int lenght = dataInput.readInt()-2;
			//System.out.println("lenght -> "+lenght);
			
			//byte[] buf = new byte[lenght];
			
			//tipo = (int) dataInput.readByte();			/* guarda o código da operação - SQL ou acionamento */
			//System.out.println("tipo -> "+tipo);
			
			/* se for um comando sql */
			if(datagram.getTipo() == Message.SQL) {
				
				switch(datagram.getAcao()) {
				
					case Message.NEW_USER:				/* se for cadastro de usuário */
														
						dao.connectBD();				/* conecta ao banco de dados */
						
						listDataIn = datagram.getLista();	/* recebe lista de dados enviados pelo celular */
						
						nome = listDataIn.get(0);
						senha = listDataIn.get(1);
						deviceId = listDataIn.get(2);
						email = listDataIn.get(3);
					
						/* se usuário ainda não foi cadastrado*/
						if(!dao.consultaUsuario(deviceId)) {
							
							/* cadastra usuário */
							dao.novoUsuario(nome, senha, deviceId, email);
						
							/* envia mensagem para o celular que o usuário foi cadastrado */
							this.sendMessage(makeData(Message.RESPONSE, Message.OK));
						}
				
						/* usuário ja está cadastrado */
						else {
							/* envia mensagem para o celular que usuário ja está cadastrado */
							this.sendMessage(makeData(Message.RESPONSE, Message.NOK));
						}
						
						listDataIn.clear();
						break;
				
					case Message.LOGIN:					
						
						//listDataIn = datagram.getLista();
						
						/* pega o endereço IMEI do celular */
						deviceId = datagram.getData(0);
						
						/* conecta ao banco de dados */
						dao.connectBD();
					
						/* se usuário ainda não foi cadastrado*/
						if(!dao.consultaUsuario(deviceId)) {
							
							/* envia mensagem para o celular de que o usuário ainda não está cadastrado */
							this.sendMessage(makeData(Message.RESPONSE, Message.NOK));
						}
					
						else {
							/* envia mensagem para o celular de que usuário ja está cadastrado */
							this.sendMessage(makeData(Message.RESPONSE, Message.OK));
						}
						
						//listDataIn.clear();
						break;
						
					case Message.NEW_COMODO:						
						
						//String comodo = listItem.get(0);
						//listDataIn.clear();
						
						//datagram = this.readData();				//recebe os itens do comodo
						
						//listDataIn = datagram.getLista();
						
						dao.connectBD();						/* conecta ao banco de dados */
						
						dao.insereComodo(datagram.getLista());	//insere no banco os itens
						
						/* envia a mensagem de confirmação */
						this.sendMessage(makeData(Message.RESPONSE, Message.OK));
						
						break;
						
					case Message.GET_COMODO:
						dao.connectBD();
						
						List<ArrayList<String>> listComodo = new ArrayList<ArrayList<String>>();
						
						/* o método consultaComodo retorna uma matriz de string
						 * cada array da matriz contém o nome do comodo na primeira posição e 
						 * nas demais poisições contém os intens dos comodos */
						listComodo = dao.consultaComodo();
						
						/* para cada array da matriz o servidor envia ao cliente os bytes */
						for(int i = 0; i < listComodo.size(); i++) {
							this.sendMessage(makeData(listComodo.get(i), Message.SQL, Message.GET_COMODO));
						}
						//datagram.setParam(Message.SQL, Message.GET_COMODO, listComodo);
						this.sendMessage(makeData(Message.RESPONSE, Message.FIM));
						
						break;
					}
				}
			
			/* fecha conexão com o banco */
			dao.closeCon();
			//datagramInput.close();
			
		} catch (IOException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
		}
	}
	
	private DatagramServer readData() throws IOException {

		int lenghtString, k = 0;
		DatagramServer datagram = new DatagramServer();
	
		/* recebe o número de bytes que serão lidos, desconsiderando o byte de tipo e acao */
		int lenghtData = dataInput.readInt();
		System.out.println("lenght data -> "+lenghtData);
		
		datagram.setTipo(dataInput.readByte());				//lê o tipo do dado recebedido
		System.out.println("tipo -> "+datagram.getTipo());
		lenghtData--;
		
		datagram.setAcao(dataInput.readByte());				//lê a ação a ser tomada com o dado recebido
		System.out.println("acao -> "+datagram.getAcao());
		lenghtData--;
		
		while(lenghtData > 0) {
		
			lenghtString = dataInput.readByte();
			lenghtData--;
			System.out.println("tam string -> "+lenghtString);
			
			byte[] buf = new byte[lenghtString];		//lê o número de caracteres que a string irá conter
		
			dataInput.read(buf);
			lenghtData = lenghtData - lenghtString;			
			
			datagram.addData(new String(buf));			//adiciona a string lida na lista de dados
			
			System.out.println("String convertida -> "+datagram.getData(k));
			k++;
		}
		return datagram;
	}

	/* envia mensagem para o celular */
	private void sendMessage(ByteArrayOutputStream data) {
		
		DataOutputStream out;
		
		try {
			out = new DataOutputStream (cliente.getOutputStream());
			out.writeInt(data.toByteArray().length);
			out.write(data.toByteArray());
			out.flush();
			
		} catch (IOException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
		}
	}
	
	public ByteArrayOutputStream makeData(byte tipo, byte acao) {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		bos.write(tipo);
		bos.write(acao);
					
		return bos;
	}
	
	public ByteArrayOutputStream makeData(List<String> data, int tipo, int acao) {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		try {
			bos.write(tipo);
			bos.write(acao);
			
			/* converte todos os dados para bytes, para serem enviados */
			for(int i = 0; i < data.size(); i ++) {
				bos.write(data.get(i).length());
				bos.write(data.get(i).getBytes());
			}
			
			return bos;
			
		} catch (IOException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
			return null;
		}
	}
}
