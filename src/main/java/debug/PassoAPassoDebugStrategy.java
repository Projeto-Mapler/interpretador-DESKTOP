package debug;

import modelos.tree.AstDebugNode;

/**
 * Estratégia para pausar a execução do código em cada linha
 * 
 * @author Kerlyson
 *
 */
public class PassoAPassoDebugStrategy implements DebugStrategy {

    @Override
    public int executar(AstDebugNode node, Debugador debugador) {
	int linhaAnterior = debugador.getLinha();

	System.out.println("[debug] linha: " + node.getLinha() + " .. " + node.getClass().getName());
	if (linhaAnterior == node.getLinha())
	    return linhaAnterior;
	debugador.pausarExecucao();
	return node.getLinha();
    }

}
