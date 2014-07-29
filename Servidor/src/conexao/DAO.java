package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAO {

	private Connection connect;
	
	public void connectBD() {
		try{
			String driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);
			
			String user = "root";
			String pass = "!lh4ma@#$!";
			String url = "jdbc:mysql://127.0.0.1:3306/DBAutomacao";
			
			connect = DriverManager.getConnection(url, user, pass);
			
			if(connect != null) {
				System.out.println("CONECTADO COM SUCESSO");
			}
			
			else {
				System.out.println("NÃO COM BANCO DE DADOS FALHOU!");
			}
		
		} catch (ClassNotFoundException e) {
			System.out.println("ERRO DRIVER");
		} catch (SQLException e) {
			System.out.println("ERRO SQL");
		}
	}
	
	/** método para inserir no banco de dados os comodos e itens que são cadastrados */
	public void insereComodo(List<String> itens) throws SQLException {

		int i;
		String comodo = itens.get(0);
		itens.remove(0);
		
		/* monta a query para inserir o comodo no banco de dados */
		String sql = "INSERT INTO Comodo (nomeComodo) VALUES ('"+comodo+"')";
		
		/* executa a query */
		this.insereBanco(sql);
	
		/* monta a query para consultar o id do comodo cadastrado e utilizar como fk dos itens que serão adicionados */
		sql = "SELECT idComodo FROM Comodo WHERE nomeComodo = '"+comodo+"'";
		ResultSet rs = this.consultaBanco(sql);
		
		if(rs.next()) {
			/* pega o id do comodo cadastrado */
			int idComodo = rs.getInt("idComodo");
			
			i = 0;
			
			/* insere todos os itens cadastrados no comodo no banco de dados */
			do {
				sql = "INSERT INTO Itens (nome_item, num_item, tipo_item, id_comodo_fk)"
						+ " VALUES ('"+itens.get(i)+"',"+Integer.parseInt(itens.get(i+1))+",'"+itens.get(i+2)+"',"+idComodo+")";
				
				i += 3;
				
				this.insereBanco(sql);
				
			} while (i < itens.size());
		}
	}
	
	/** método para consultar no banco de dados os usuário que já estão cadastrados */
	public boolean consultaUsuario(String deviceId) throws SQLException {
		
		/* monta a query para consultar se o morador ja esta cadastrado */
		String sql = "SELECT imei FROM Usuario WHERE imei = "+deviceId;
		
		ResultSet rs = this.consultaBanco(sql);
		
		/* se usuário ainda não foi cadastrado*/
		if(!rs.next())
			return false;
		
		/* usuário ja está cadastrado */
		else
			return true;
	}
	
	
	
	/** método para inserão de novo usuário */
	public void novoUsuario(String nome, String senha, String imei, String email) {
		String sql = "INSERT INTO Usuario (nome_usuario, senha, imei, email)"
				+ " VALUES ('"+nome+"','"+senha+"','"+imei+"','"+email+"')";
		
		this.insereBanco(sql);
	}
	
	
	
	/** método para consultar todos os comodos cadastrados no banco de dados */
	public List<ArrayList<String>> consultaComodo() throws SQLException {
		int idComodo, num_item, id_comodo_fk;
		int i = 0;
		String comodo;
		String item, tipo_item;
		//List<String> listComodo = new ArrayList<String>();
		List<ArrayList<String>> lista = new ArrayList<ArrayList<String>>();
		
		String sql = "SELECT * FROM Comodo";
		ResultSet rsComodo = this.consultaBanco(sql);
		
		while(rsComodo.next()) {
			idComodo = rsComodo.getInt("idComodo");
			comodo = rsComodo.getString("nomeComodo");
			
			lista.add(new ArrayList<String>());
			lista.get(i).add(comodo);
			System.out.println("comodo -> "+comodo);
			
			sql = "SELECT nome_item FROM Itens WHERE id_comodo_fk = "+idComodo;
			ResultSet rsItens = this.consultaBanco(sql);
			
			while(rsItens.next()) {
				item = rsItens.getString("nome_item");
				//tipo_item = rsItens.getString("tipo_item");
				//comodo = comodo.concat("-"+item);
				lista.get(i).add(item);
				System.out.println("item -> "+item);
			}
			//listComodo.add(comodo);
			i++;
		}
		return lista;
	}
	
	
	
	/** método para fazer consulta no banco de dados */
	public ResultSet consultaBanco (String sql) {
		
		System.out.println("Consulta BD --> "+sql);
		ResultSet rs = null;
		
		try {
			Statement st = connect.createStatement();
			rs = st.executeQuery(sql);
			
			return rs;
			
		} catch (SQLException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
			return rs;
		}
	}
	
	/** método para inserir dados no banco de dados */
	private boolean insereBanco(String sql) {
		
		System.out.println("Execução BD --> "+sql);
		
		try {
			Statement st = connect.createStatement();
			st.executeUpdate(sql);
			
			return true;
			
		} catch (SQLException e) {
			// TODO Bloco catch gerado automaticamente
			e.printStackTrace();
			return false;
		}
	}
	
	/** fecha conexão com o banco de dados */
	public boolean closeCon() {
		try {
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
}