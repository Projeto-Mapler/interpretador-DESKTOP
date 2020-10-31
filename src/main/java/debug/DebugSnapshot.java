package debug;

import java.util.Map;

import tree.AstDebugNode;

/**
 * Representa o estado do interpretador no node atual sendo debugado
 * 
 * @author Kerlyson
 *
 */
public class DebugSnapshot {
    private AstDebugNode node;
    private Map<String, Object> ambienteSnapshot;

    public DebugSnapshot(AstDebugNode node, Map<String, Object> ambienteSnapshot) {
	super();
	this.node = node;
	this.ambienteSnapshot = ambienteSnapshot;
    }

    public AstDebugNode getNode() {
	return node;
    }

    public Map<String, Object> getAmbienteSnapshot() {
	return ambienteSnapshot;
    }

    public int getLinha() {
	return node.getLinha();
    }

    public String getNodeClassName() {
	return node.getClass().getName();
    }

}
