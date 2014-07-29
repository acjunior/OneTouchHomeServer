package conexao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import defines.Message;

/**
 * SERVIDOR - Classe responsável por padronizar o envio e recebimento de dados do smartphone
 */

public class DatagramServer {

	private byte tipo;
	private byte acao;
	private byte[] data;
	private List<String> listData;
	
	public DatagramServer() {
		listData = new ArrayList<String>();
		//this.data = data;
		//this.setDatagram();
	}
	
	private void setDatagram() {
		tipo = data[0];
		System.out.println("tipo -> "+tipo);
		acao = data[1];
		System.out.println("acao -> "+acao);
		
		/* mensagens de resposta não tem string de dados */
		if(acao != Message.GET_COMODO)
			this.lerString();
	}
	
	public void setTipo(byte tipo) {
		this.tipo = tipo;
	}
	
	public byte getTipo() {
		return tipo;
	}
	
	public void setAcao(byte acao) {
		this.acao = acao;
	}
	
	public byte getAcao() {
		return acao;
	}
	
	public void addData(String data) {
		listData.add(data);
	}
	
	public List<String> getLista() {
		return listData;
	}
	
	public String getData(int position) {
		return listData.get(position);
	}
	
	/** Método responsável por converter os bytes recebidos em string */
	private void lerString() {
		
		int j, i = 2, k = 0;
		int lenght = 0;
		
		/* percorre o vetor de bytes */
		while (i < data.length) {
			
			/* indice j aponta para o próximo byte 
			 * o primeiro byte contém o número de bytes que deve ser lido */
			lenght = data[i];
			j = i + 1;
			
			/* cria nova string com o numero de bytes que deve ser lido e armazena no array */
			listData.add(new String(data, j, lenght));
			System.out.println("lenght string -> "+lenght);
			System.out.println("string -> "+listData.get(k));
			k++;
			
			i = (data[i] + i) + 1;			/* atualiza os indices para ler a proxima string */
		}
	}
}
