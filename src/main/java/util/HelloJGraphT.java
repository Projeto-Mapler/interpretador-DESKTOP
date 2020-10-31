package util;

import java.util.Iterator;
import java.util.stream.Stream;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;
import tree.AstDebugNode;

/**
 * Teste de visualização da AST
 * não concluido...
 * 
 * @author Kerlyson
 *
 */
public final class HelloJGraphT {
  public static void main(String[] args) {

//    System.setProperty("org.graphstream.ui.renderer",
//        "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    System.setProperty("org.graphstream.ui", "swing");

    Graph graph = new MultiGraph("Test");

   
    Node A = graph.addNode("A");
    Node B = graph.addNode("B");
    Node C = graph.addNode("C");
    Node D = graph.addNode("D");
    Node E = graph.addNode("E");
   

    A.setAttribute("xy", 0, 0);
    B.setAttribute("xy", 1, 0);
    C.setAttribute("xy", 2, 0);
    D.setAttribute("xy", 3, 0);
    E.setAttribute("xy", 4, 0);

    graph.nodes().forEach(node -> node.setAttribute("ui.label", node.getId()));

//    Edge a = graph.addEdge("1", "A", "B");
//    Edge b = graph.addEdge("2", "A", "B");
//    Edge c = graph.addEdge("3", "A", "C");
//    Edge d = graph.addEdge("4", "B", "D");
//    Edge e = graph.addEdge("5", "B", "E");

    graph.setAttribute("ui.stylesheet","node { shape: box; fill-color: #DEE; size: 100px, 50px; stroke-mode: plain; stroke-color: #555;}");
    graph.setAttribute("ui.antialias");
    Viewer viewer = graph.display(false);
    
  
}}
