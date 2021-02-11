package debug;

import java.util.HashSet;
import java.util.Set;
import modelos.tree.AstDebugNode;

/**
 * Estratégia para pausar a execução do código apenas nas linhas (breakpoints) informadas
 * @author Kerlyson
 *
 */
public class BreakpointsDebugStrategy implements DebugStrategy{

	private Set<Integer> breakpoints;
	private Integer breakpoint = 0;// ultimo breakpoint análisado
	
	public BreakpointsDebugStrategy() {
		this.breakpoints = new HashSet<Integer>();
	}
	
	/**
	 * Adiciona um ponto onde o código será pausado quando o debug for executado.
	 * @param linha - Numero da linha onde o código deve ser pausado
	 */
	public void addBreakPoint(int linha) {
		this.breakpoints.add(linha);
	}
	
	/**
	 * Remove um breakpoint
	 * @param linha
	 */
	public void removeBreakPoint(int linha) {
		this.breakpoints.remove(linha);
	}
	
	/**
	 * Remove todos os breakpoints
	 */
	public void removeTodosBreakPoins() {
		this.breakpoints.clear();
	}
	
	@Override
	public int executar(AstDebugNode node, Debugador debugador) {
		
		if (breakpoints.contains(node.getLinha())) {
			if (!(breakpoint == node.getLinha() && debugador.getLinha() == breakpoint)) {				
				breakpoint = node.getLinha();
				debugador.pausarExecucao();
			}

		}
		System.out.println("[debug] linha: " + node.getLinha() + " .. " + node.getClass().getName());
		return node.getLinha();
	}

	

}
