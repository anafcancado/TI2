import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;

import dao.ClienteDAO;
import models.Cliente;
import static spark.Spark.*;


public class Aplicacao {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		 ClienteDAO clienteDAO = new ClienteDAO();
		
		System.out.println("Bem-vindo!");
		int resposta = 0;   
		port(8080);
		get("/hello", (req, res) -> "Hello World");
		
		
		
		post("/clients", (req, res) -> {
		    // Extract the fields from the request's form data or JSON body

		    // For form data (e.g., HTML form submission)
		    int codigo = Integer.parseInt(req.queryParams("codigo"));
		    String nome = req.queryParams("nome");
		    String email = req.queryParams("email");
		    int idade = Integer.parseInt(req.queryParams("idade"));

		    // Create a Cliente object
		    Cliente cliente = new Cliente(codigo, nome, email, idade);

		    // Insert the Cliente object into the database using clienteDAO
		    clienteDAO.insert(cliente);

		    // Return a success message or the created cliente
		    return "Cliente created successfully"; // You can return JSON or HTML response as needed
		});
		
		get("/clientes", (req, res) -> {
		    // Retrieve the list of clients from your data source (e.g., database)
		    List<Cliente> clientes = clienteDAO.getAll();
		    
		    // Convert the list of clients to JSON using a library like Gson
		    Gson gson = new Gson();
		    String jsonClients = gson.toJson(clientes);

		    // Set the response content type to JSON
		    res.type("application/json");

		    // Return the list of clients as JSON
		    return jsonClients;
		});


		
		/*while(resposta != -1) {
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
		
	*/
	}
	
}