package debug;

import tree.AstDebugNode;

public class BreakpointsDebugStrategy implements DebugStrategy{

	@Override
	public int executar(AstDebugNode node, Debugador debugador) {
		
		if (debugador.checkContainsBreakPoint(node.getLinha())) {
			if (!(debugador.getBreakpoint() == node.getLinha() && debugador.getLinha() == debugador.getBreakpoint())) {				
				debugador.setBreakPoint(node.getLinha());
				debugador.pausaExecucao();
			}

		}
		System.out.println("[debug] linha: " + node.getLinha() + " .. " + node.getClass().getName());
		return node.getLinha();
	}

	

}
