package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring. Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author falvojr
 */

/**
 * Neste código, as dependências (clienteRepository, enderecoRepository, viaCepService)
 * são injetadas por meio de um construtor. Essa abordagem segue o princípio de Injeção de 
 * Dependência, tornando sua classe mais flexível e facilitando a realização de testes unitários, 
 * já que as dependências podem ser facilmente mockadas durante os testes.
   Observe que a classe agora não possui mais as anotações @Service ou @Autowired, uma vez que não é mais gerenciada pelo Spring.
 * */
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ViaCepService viaCepService;

    public ClienteServiceImpl(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository, ViaCepService viaCepService) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.viaCepService = viaCepService;
    }

    // Restante do código não foi alterado.

    @Override
    public Iterable<Cliente> buscarTodos() {
        // Buscar todos os Clientes.
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        // Buscar Cliente por ID.
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.get();
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        // Buscar Cliente por ID, caso exista:
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if (clienteBd.isPresent()) {
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar(Long id) {
        // Deletar Cliente por ID.
        clienteRepository.deleteById(id);
    }

    private void salvarClienteComCep(Cliente cliente) {
    	// Verificar se o Endereco do Cliente já existe (pelo CEP).
    			String cep = cliente.getEndereco().getCep();
    			Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
    				// Caso não exista, integrar com o ViaCEP e persistir o retorno.
    				Endereco novoEndereco = viaCepService.consultarCep(cep);
    				enderecoRepository.save(novoEndereco);
    				return novoEndereco;
    			});
    			cliente.setEndereco(endereco);
    			// Inserir Cliente, vinculando o Endereco (novo ou existente).
    			clienteRepository.save(cliente);
    }
}
