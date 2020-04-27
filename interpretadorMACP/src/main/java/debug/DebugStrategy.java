package debug;

import tree.AstDebugNode;

public interface DebugStrategy {
	/**
	 * 
	 * @param node - Node 
	 * @return linha analisada
	 */
	int executar(AstDebugNode node, Debugador debugador);
}
