package serverAutomacao;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import conexao.ConnectUSB;
import conexao.DAO;
import conexao.ServidorRede;

public class ServidorAutomacao {

	public static void main(String[] args) {
		// TODO Stub de método gerado automaticamente
		
		ServidorRede.start();
		
		/*ConnectUSB connect = new ConnectUSB("/dev/ttyUSB0");
		
		char data[] = connect.zeraData();
		
		data[8] = 1;
		data[10] = 0;
		data[34] = 1;	//liga LED vermelho
		data[49] = 0;	//liga LED amarelo
		data[43] = 0;	//rele
		data[46] = 0;
		
		connect.sendData(data);
		//connect.recData();
		
		connect.closeSerial();*/
	}
}
