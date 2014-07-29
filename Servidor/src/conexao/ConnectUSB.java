package conexao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import defines.Message;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;


/** Classe responsável por abrir a conexão USB do servidor, enviar dados a porta USB e receber
 * dados dos sensores */

public class ConnectUSB {

	private String porta;
	private CommPortIdentifier portId;
	private SerialPort port;
	private OutputStream serialOut;
	private InputStream serialIn;
	
	
	/** @param porta - porta USB que será utilizada para o envio de dados ao BE900
	 *  neste caso é utilizado o /dev/ttyUSB0 */
	public ConnectUSB (String porta) {
		this.porta = porta;
		
		try {
			portId = CommPortIdentifier.getPortIdentifier(porta);
			
			port = (SerialPort)portId.open("ConnectUSB", 2);
			serialOut = port.getOutputStream();
			port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			System.out.println("ERRO - Porta USB!");
			
		} catch (PortInUseException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Método responsável por zerar todas as posições do 
	 * pacote de dados a ser enviado para os sensores
	 */
	public char[] zeraData() {
		
		char[] data = new char[52]; 
		
		for(int i = 0; i < Message.TAM_PKT; i++) {
			data[i] = 0;
		}
		
		return data;
	}
	
	public void sendData(char[] data) {
		
		try {
			for(int i = 0; i < Message.TAM_PKT; i++)
				serialOut.write(data[i]);
			
			serialOut.flush();
		
		} catch (IOException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
		}
	}
	
	public void recData() {
		char [] data = new char[52];
		for(int i = 0; i < Message.TAM_PKT; i++) {
			try {
				data[i] = (char) serialIn.read();
			} catch (IOException e) {
				// TODO Bloco catch gerado automaticamente
				e.printStackTrace();
			}
		}
	}
	
	public void closeSerial() {
		try {
			serialOut.close();
		} catch (IOException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
		}
	}
}


/* colocar o vetor de char[52] - 1 char é 1 byte */