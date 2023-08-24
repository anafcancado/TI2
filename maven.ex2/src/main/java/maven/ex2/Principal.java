package maven.ex2;

import java.util.List;
import java.util.Scanner;


public class Principal {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		 AnlunoDAO clienteDAO = new AlunoDAO();
		
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
				System.out.println("Id: ");
			
				int codigo = scanner.nextInt();
				scanner.nextLine();
				
				System.out.println("Nome: ");
				String nome = scanner.nextLine();
				
				System.out.println("Curso: ");
				String email = scanner.nextLine();
				
				scanner.nextLine();
				
				Aluno aluno = new Aluno(id, nome, curso);
				
				clienteDAO.insert(aluno);
				break;
				
			case(2):
				List<Aluno> alunos =  clienteDAO.getAll();
				System.out.println("Clientes registrados: ");
				System.out.println(alunos);
				break;
				
			case(3):
				System.out.println("id do aluno que deseja excluir: ");
				int codigoAExcluir = scanner.nextInt();
				scanner.nextLine();
				alunoDAO.delete(codigoAExcluir);
				break;
			
			case(4):
				System.out.println("id do aluno que atualizar informações: ");
				System.out.println("Obs: impossivel alterar codigo e idade");
				int codigoAtt = scanner.nextInt();
				scanner.nextLine();
				
				System.out.println("Novo nome: ");
				String nomeAtt = scanner.nextLine();
				
				System.out.println("Novo curso: ");
				String cursoAtt = scanner.nextLine();
				
				Aluno cAntigo = clienteDAO.getByCodigo(codigoAtt);
						
	
				Aluno clienteAtt = new Aluno(cAntigo.getId(), nomeAtt, cursoAtt, cAntigo.getCodigo());
				
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
