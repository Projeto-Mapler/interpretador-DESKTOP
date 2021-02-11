package debug;

import modelos.tree.AstDebugNode;

/**
 * Padrão Strategy.
 * 
 * @author Kerlyson
 *
 */
public interface DebugStrategy {
    /**
     * Encapsula a lógica que deve ser executada para a estratégia de debug
     * 
     * @param node - NodeAstDebug
     * @return linha analisada
     */
    int executar(AstDebugNode node, Debugador debugador);
}
