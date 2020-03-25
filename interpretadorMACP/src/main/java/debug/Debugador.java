package debug;

import tree.AstDebugNode;

public class Debugador implements EventoListener{

	@Override
	public void update(Object payload) {
		if(payload instanceof AstDebugNode) {
			AstDebugNode node = (AstDebugNode) payload;
			if(node.getLinha() < 1 ) return;
			System.err.println("[debug] linha: " + node.getLinha());
		}
		
	}

	

}
