package util;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;
import tree.AstDebugNode;
import tree.Declaracao;
import tree.Declaracao.Bloco;
import tree.Declaracao.ChamadaModulo;
import tree.Declaracao.Enquanto;
import tree.Declaracao.Escreva;
import tree.Declaracao.Ler;
import tree.Declaracao.Modulo;
import tree.Declaracao.Para;
import tree.Declaracao.Programa;
import tree.Declaracao.Repita;
import tree.Declaracao.Se;
import tree.Declaracao.Var;
import tree.Declaracao.VarDeclaracoes;
import tree.Declaracao.VariavelArray;
import tree.Expressao;
import tree.Expressao.Atribuicao;
import tree.Expressao.AtribuicaoArray;
import tree.Expressao.Binario;
import tree.Expressao.ExpParentizada;
import tree.Expressao.Grupo;
import tree.Expressao.Literal;
import tree.Expressao.Logico;
import tree.Expressao.Unario;
import tree.Expressao.Variavel;

public class JGraphTBuilder implements Expressao.Visitor<Void>, Declaracao.Visitor<Void> {


  private Graph graph = new SingleGraph("Test");
  private static int POSICAO_X = 0;
  private static int POSICAO_Y = 0;
  private static final String NODE_ATRB = "AST_NODE_DEBUG";
  private static Integer ID = 0;
  
  public void print(Declaracao.Programa programa) {
    this.visitProgramaDeclaracao(programa);
    System.setProperty("org.graphstream.ui", "swing");
    graph.setAttribute("ui.stylesheet","node { "
        + "shape: box; "
        +"size-mode:dyn-size;"
        + "fill-color: #DEE; "
      //  + "size:400px, 70px; "
        + "stroke-mode: plain; "
        + "stroke-color: #555;"
        + "text-size: 20;"
  +" text-padding: 5px, 4px; "
       // +"size-mode:fit;"
       // +"visibility: 0;"
        + "}");
    
   Layout l = new LinLog();
   graph.addSink(l);
  
   Viewer v =  this.graph.display(false);
   final View view = v.getDefaultView();
   view.getCamera().setViewCenter(0, 0, 0);
   view.getCamera().setViewPercent(0.25);
   
//   ((Component) view).addMouseWheelListener(new MouseWheelListener() {
//       @Override
//       public void mouseWheelMoved(MouseWheelEvent e) {
//           e.consume();
//           int i = e.getWheelRotation();
//           double factor = Math.pow(1.25, i);
//           Camera cam = view.getCamera();
//           double zoom = cam.getViewPercent() * factor;
//           Point2 pxCenter  = cam.transformGuToPx(cam.getViewCenter().x, cam.getViewCenter().y, 0);
//           Point3 guClicked = cam.transformPxToGu(e.getX(), e.getY());
//           double newRatioPx2Gu = cam.getMetrics().ratioPx2Gu/factor;
//           double x = guClicked.x + (pxCenter.x - e.getX())/newRatioPx2Gu;
//           double y = guClicked.y - (pxCenter.y - e.getY())/newRatioPx2Gu;
//           cam.setViewCenter(x, y, 0);
//           cam.setViewPercent(zoom);
//       }
//   });
  }
  
  private void evaluate(Expressao expressao) {
    expressao.accept(this);
  }

  private void execute(Declaracao declaracao) {
    declaracao.accept(this);
  }

  private void addNode(AstDebugNode debugNode, boolean incrementarNivel) {  

    Node node = graph.addNode(ID.toString());
    
    node.setAttribute("xyz", POSICAO_X, POSICAO_Y, 0);
    node.setAttribute(NODE_ATRB, debugNode);
    node.setAttribute("ui.label", getNodeName(debugNode));
   // node.setAttribute("ui.size");
    
    if (incrementarNivel) {
      POSICAO_Y = POSICAO_Y-1;
      POSICAO_X=0;
    } else {
      POSICAO_X=POSICAO_X+1;
    }
    ID++;
  }
  
   private String getNodeName(AstDebugNode debugNode) {
     String pacote = debugNode.getClass().getSimpleName();
     String[] nomeClasse = pacote.split("$");
     for (int i = 0; i < nomeClasse.length; i++) {
      System.out.println(nomeClasse[i]);
    }
     System.out.println("----------");
     return "[id: "+ID+"][linha: "+debugNode.getLinha()+"]" + pacote;
  }

  @Override
  public Void visitBlocoDeclaracao(Bloco declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitExpressaoDeclaracao(tree.Declaracao.Expressao declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitEscrevaDeclaracao(Escreva declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitSeDeclaracao(Se declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitLerDeclaracao(Ler declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitVarDeclaracao(Var declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitVarDeclaracoesDeclaracao(VarDeclaracoes declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitVariavelArrayDeclaracao(VariavelArray declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitParaDeclaracao(Para declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitEnquantoDeclaracao(Enquanto declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitRepitaDeclaracao(Repita declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitModuloDeclaracao(Modulo declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitChamadaModuloDeclaracao(ChamadaModulo declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitProgramaDeclaracao(Programa declaracao) {
    // variaveis
    for (Declaracao variaveis : declaracao.variaveis) {
      addNode(variaveis, false);
      execute(variaveis);
    }
    // modulos-funcoes
    for (Declaracao modulo : declaracao.modulos) {
      addNode(modulo, false);
      execute(modulo);
    }
    // inicio-fim
    for (Declaracao corpo : declaracao.corpo) {
      execute(corpo);
    }
    return null;
  }

  @Override
  public Void visitBinarioExpressao(Binario expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitGrupoExpressao(Grupo expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitExpParentizadaExpressao(ExpParentizada expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitLiteralExpressao(Literal expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitLogicoExpressao(Logico expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitUnarioExpressao(Unario expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitAtribuicaoExpressao(Atribuicao expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitAtribuicaoArrayExpressao(AtribuicaoArray expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitVariavelArrayExpressao(tree.Expressao.VariavelArray expressao) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitVariavelExpressao(Variavel expressao) {
    // TODO Auto-generated method stub
    return null;
  }

}
