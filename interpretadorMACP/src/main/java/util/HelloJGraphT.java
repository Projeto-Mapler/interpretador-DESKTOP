package util;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.Viewer;

public final class HelloJGraphT {
    public static void main(String[] args) {
	 
	 System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

	        Graph graph = new MultiGraph("Test");
	      

	        Node A = graph.addNode("A");
	        Node B = graph.addNode("B");
	        Node C = graph.addNode("C");
	        Node D = graph.addNode("D");
	        Node E = graph.addNode("E");

	        A.setAttribute("xyz", 0,0,0);
	        B.setAttribute("xyz", -1,-1,0);
	        C.setAttribute("xyz", 1,-1,0);
	        D.setAttribute("xyz", -2,-2,0);
	        E.setAttribute("xyz", 0,-2,0);

	        for(Node n : graph.getNodeSet()) {
	            n.addAttribute("ui.label", n.getId());
	        }

	        Edge a = graph.addEdge("1", "A", "B");
	        Edge b = graph.addEdge("2", "A", "B");
	        Edge c = graph.addEdge("3", "A", "C");
	        Edge d = graph.addEdge("4", "B", "D");
	        Edge e = graph.addEdge("5", "B", "E");

	        Viewer viewer = graph.display(false);
	 
	
}
}