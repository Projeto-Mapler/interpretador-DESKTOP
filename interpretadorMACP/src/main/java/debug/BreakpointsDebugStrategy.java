package debug;

import java.util.HashSet;
import java.util.Set;

import tree.AstDebugNode;

public class BreakpointsDebugStrategy implements DebugStrategy{

	private Set<Integer> breakpoints;
	private Integer breakpoint = 0;
	
	public BreakpointsDebugStrategy() {
		this.breakpoints = new HashSet<Integer>();
	}
	
	public void addBreakPoint(int linha) {
		this.breakpoints.add(linha);
	}

	public void removeBreakPoint(int linha) {
		this.breakpoints.remove(linha);
	}

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
