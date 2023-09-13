import java.util.List;
import java.util.Scanner;

import dao.ClienteDAO;
import models.Cliente;

public class Aplicacao {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		 ClienteDAO clienteDAO = new ClienteDAO();
		
		System.out.println("Bem-vindo!");
		int resposta = 0;   
		
		while(resposta != -1) {
			System.out.println(" Menu: ");
			System.out.println("-1 - Encerrar programa");
			System.out.println("1 - Inserir cliente");
			System.out.println("2- Listar clientes");
			System.out.println("3- Excluir cliente");
			System.out.println("4- Atualizar cliente");
			System.out.println("Escolha sua opcao: ");
			resposta = scanner.nextInt();
			
			switch(resposta) {
			case(1):
				System.out.println("Codigo: ");
			
				int codigo = scanner.nextInt();
				scanner.nextLine();
				
				System.out.println("Nome: ");
				String nome = scanner.nextLine();
				
				System.out.println("Email: ");
				String email = scanner.nextLine();
				
				System.out.println("Idade: ");
				int idade = scanner.nextInt();
				scanner.nextLine();
				
				Cliente cliente = new Cliente(codigo, nome, email, idade);
				
				clienteDAO.insert(cliente);
				break;
				
			case(2):
				List<Cliente> clientes =  clienteDAO.getAll();
				System.out.println("Clientes registrados: ");
				System.out.println(clientes);
				break;
				
			case(3):
				System.out.println("Digite o codigo do cliente que deseja excluir: ");
				int codigoAExcluir = scanner.nextInt();
				scanner.nextLine();
				clienteDAO.delete(codigoAExcluir);
				break;
			
			case(4):
				System.out.println("Digite o codigo do cliente que deseja atualizar informações: ");
				System.out.println("Obs: mpossivel alterar codigo e idade");
				int codigoAtt = scanner.nextInt();
				scanner.nextLine();
				
				System.out.println("Novo nome: ");
				String nomeAtt = scanner.nextLine();
				
				System.out.println("Novo email: ");
				String emailAtt = scanner.nextLine();
				
				Cliente cAntigo = clienteDAO.getByCodigo(codigoAtt);
						
	
				Cliente clienteAtt = new Cliente(cAntigo.getCodigo(), nomeAtt, emailAtt, cAntigo.getCodigo());
				
				clienteDAO.update(clienteAtt);
				
				
				break;
			default:
				System.out.println("Digite um numero que está no menu.");
				break;
			}
		}
		System.out.println("Programa encerrado.");
		scanner.close();
		
	
	}
	
}
