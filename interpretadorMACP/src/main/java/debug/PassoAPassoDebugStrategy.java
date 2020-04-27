package debug;

import tree.AstDebugNode;

public class PassoAPassoDebugStrategy implements DebugStrategy{

	@Override
	public int executar(AstDebugNode node, Debugador debugador) {
		int linhaAnterior = debugador.getLinha();
		
		if(linhaAnterior == node.getLinha()) return linhaAnterior;
		System.out.println("[debug] linha: " + node.getLinha() + " .. " + node.getClass().getName());
		debugador.pausaExecucao();
		return node.getLinha();
	}

}
