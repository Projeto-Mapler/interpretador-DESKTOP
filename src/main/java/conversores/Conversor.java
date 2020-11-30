package conversores;

import java.util.HashMap;
import java.util.Map;

import debug.GerenciadorEventos;
import debug.TiposEvento;
import modelos.RuntimeError;
import modelos.TiposToken;
import modelos.VariavelVetor;
import tree.Declaracao.Programa;

/**
 * Base para Classes conversoras para outras linguagens Classes conversoras
 * devem implementar as Interfaces Visitor para Declaração e Expressao também.
 * 
 * @author Kerlyson
 *
 */
public abstract class Conversor {

    protected Escritor escritor;
    protected Programa programa;
    private Map<String, VariavelVetor> mapaVariaveisVetor; // salva as variaveis do tipo vetor
    private GerenciadorEventos gerenciadorEventos;

    public Conversor(Programa programa, GerenciadorEventos ge) {
	this.gerenciadorEventos = ge;
	this.escritor = new Escritor();
	this.programa = programa;
	this.mapaVariaveisVetor = new HashMap<String, VariavelVetor>();
    }

    protected void throwRuntimeErro(RuntimeError erro) {
	this.gerenciadorEventos.notificar(TiposEvento.ERRO_RUNTIME, erro);
    }

    protected void addVariavelVetor(String nome, VariavelVetor variavel) {
	this.mapaVariaveisVetor.put(nome, variavel);
    }

    protected VariavelVetor getVariavelVetor(String nome) {
	if (this.mapaVariaveisVetor.containsKey(nome))
	    return this.mapaVariaveisVetor.get(nome);
	return null;
    }

    protected TiposToken getVariavelVetorTipo(String nome) {
	if (this.mapaVariaveisVetor.containsKey(nome))
	    return this.mapaVariaveisVetor.get(nome).getTipo();
	return null;
    }

    /**
     * Converte o pseudocodigo para a linguagem da Classe
     * 
     * @return - String com o programa convertido
     */
    public abstract String converter();

    /**
     * Retorna o tipo equivalente na linguagem alvo
     * 
     * @param tipo - tipo de dado do pseudoCodigo (cadeia, inteiro, real, ...)
     * @return - String com o tipo equivalente na linguagem alvo
     */
    protected abstract String tipoVariavel(TiposToken tipo);

    /**
     * Retorna o operador lógico equivalente na linguagem alvo
     * 
     * @param op - Operação logica em pseudoCodigo (e, ou, nao)
     * @return - String com o operador lógico equivalente na linguagem alvo
     */
    protected abstract String getOperadorLogico(TiposToken op);
}
