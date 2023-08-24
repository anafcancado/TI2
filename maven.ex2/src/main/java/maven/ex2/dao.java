package dao;

import java.sql.*;
import java.security.*;
import java.math.*;

 class DAO {
	protected Connection conexao;
	
		public DAO() {
		conexao = null;
	}
	
	public boolean conectar() {
		String driverName = "org.postgresql.Driver";                    
		String serverName = "localhost";
		String mydatabase = "ti2";
		int porta = 5432;
		String url = "jdbc:postgresql://" + serverName + ":" + porta +"/" + mydatabase;
		String username = "ana";
		String password = "ai12897845";
		boolean status = false;

		try {
			Class.forName(driverName);
			conexao = DriverManager.getConnection(url, username, password);
			status = (conexao == null);
			System.out.println("Conexão efetuada com o postgres!");
		} catch (ClassNotFoundException e) { 
			System.err.println("Conexão NÃO efetuada com o postgres -- Driver não encontrado -- " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Conexão NÃO efetuada com o postgres -- " + e.getMessage());
		}

		return status;
	}
	
	public boolean close() {
		boolean status = false;
		
		try {
			conexao.close();
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}
	
	public class Cliente {

		private int id;
		private String nome;
		private String curso;
		
		public Cliente() {
			super();
			this.id = -1;
			this.nome = "";
			this.curso = "";
		}
		
		public Cliente(int id, String nome, String curso) {
			this.id = id;
			this.nome = nome;
			this.curso = curso;
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public String getCurso() {
			return curso;
		}
		public void setCurso(String curso) {
			this.curso = curso;
		}
		

		@Override
		public String toString() {
			return "Aluno [id=" + id + ", nome=" + nome + ", curso=" + curso + "]";
		}
		
		
		
	
	
	public String toMD5(String senha) throws Exception {
		MessageDigest m=MessageDigest.getInstance("MD5");
		m.update(senha.getBytes(),0, senha.length());
		return new BigInteger(1,m.digest()).toString(16);
	}
}