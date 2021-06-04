package modelos.tree;

/**
 * Super classe de todos os nodes
 * 
 * @author Kerlyson
 *
 */
public class AstDebugNode {
	private int linha; // linha da instrução

	public AstDebugNode(int linha) {
		this.linha = linha;
	}

	public int getLinha() {
		return linha;
	}

	@Override
	public String toString() {
		return "Linha: " + this.linha + " || Node: " + this.getClass().getName();
	}

}
