package conversores;

import java.util.List;
import evento.EventosService;
import modelos.TiposToken;
import modelos.Token;
import modelos.VariavelVetor;
import modelos.excecao.RuntimeError;
import modelos.tree.Declaracao;
import modelos.tree.Expressao;
import modelos.tree.Declaracao.Bloco;
import modelos.tree.Declaracao.ChamadaModulo;
import modelos.tree.Declaracao.Enquanto;
import modelos.tree.Declaracao.Escreva;
import modelos.tree.Declaracao.Fim;
import modelos.tree.Declaracao.Ler;
import modelos.tree.Declaracao.Modulo;
import modelos.tree.Declaracao.Para;
import modelos.tree.Declaracao.Programa;
import modelos.tree.Declaracao.Repita;
import modelos.tree.Declaracao.Se;
import modelos.tree.Declaracao.Var;
import modelos.tree.Declaracao.VarDeclaracoes;
import modelos.tree.Declaracao.VariavelArray;
import modelos.tree.Expressao.Atribuicao;
import modelos.tree.Expressao.AtribuicaoArray;
import modelos.tree.Expressao.Binario;
import modelos.tree.Expressao.ExpParentizada;
import modelos.tree.Expressao.Grupo;
import modelos.tree.Expressao.Literal;
import modelos.tree.Expressao.Logico;
import modelos.tree.Expressao.Unario;
import modelos.tree.Expressao.Variavel;

/**
 * Converte pseudoCodigo para Pascal
 * 
 * @author Kerlyson
 *
 */
public class ConversorPascal extends Conversor
    implements Expressao.Visitor<Void>, Declaracao.Visitor<Void> {

  public ConversorPascal(Declaracao.Programa programa, EventosService gerenciadorEventos) {
    super(programa, gerenciadorEventos);
  }

  private void evaluate(Expressao expressao) {
    expressao.accept(this);
  }

  private void execute(Declaracao declaracao) {
    declaracao.accept(this);
  }

  @Override
  protected String getOperadorLogico(TiposToken op) {
    switch (op) {
      case OU:
        return "or";
      case E:
        return "and";
      case NAO:
        return "not";

      default:
        return "";
    }
  }

  @Override
  protected String tipoVariavel(TiposToken tipo) {
    switch (tipo) {
      case TIPO_CADEIA:
        return "string";
      case TIPO_INTEIRO:
        return "integer";
      case TIPO_REAL:
        return "real";
      case TIPO_CARACTERE:
        return "char";
      // case TIPO_VETOR : return "arrya";
      case TIPO_LOGICO:
        return "boolean";
      default:
        // throw error?
        return "";
    }
  }

  @Override
  public String converter() {
    try {
      visitProgramaDeclaracao(programa);
      String programa = escritor.getResultado();

      if (programa.length() > 0) {
        return programa;
      }
    } catch (RuntimeError error) {
      super.throwRuntimeErro(error);
    } finally {
      escritor.reset();
    }
    return null;
  }

  @Override
  public Void visitBlocoDeclaracao(Bloco declaracao) {
    for (Declaracao statement : declaracao.declaracoes) {
      execute(statement);
    }
    return null;
  }

  @Override
  public Void visitExpressaoDeclaracao(modelos.tree.Declaracao.Expressao declaracao) {
    escritor.concatenarNaLinha("");
    evaluate(declaracao.expressao);
    escritor.concatenarNaLinha(";").addQuebraLinha();
    return null;
  }

  @Override
  public Void visitEscrevaDeclaracao(Escreva declaracao) {
    escritor.concatenarNaLinha("write(");
    List<modelos.tree.Expressao> expressoes = declaracao.expressoes;
    for (int i = 0; i < expressoes.size(); i++) {
      evaluate(expressoes.get(i));
      if (i < (expressoes.size() - 1)) {
        escritor.concatenarNaLinha(", ");
      }
    }
    escritor.concatenarNaLinha(");").addQuebraLinha();
    return null;
  }

  @Override
  public Void visitSeDeclaracao(Se declaracao) {

    escritor.concatenarNaLinha("if ");

    evaluate(declaracao.condicao);

    escritor.concatenarNaLinha(" then").addQuebraLinha().concatenarNaLinha("begin").addQuebraLinha()
        .indentar();

    execute(declaracao.entaoBloco);

    escritor.removerIdentacao();

    if (declaracao.senaoBloco == null) {
      escritor.concatenarNaLinha("end;").addQuebraLinha();
      return null;
    }

    escritor.concatenarNaLinha("else").addQuebraLinha().concatenarNaLinha("begin").addQuebraLinha()
        .indentar();

    execute(declaracao.senaoBloco);

    escritor.removerIdentacao().concatenarNaLinha("end;").addQuebraLinha();
    return null;
  }

  @Override
  public Void visitLerDeclaracao(Ler declaracao) {
    Expressao atribuicao = declaracao.atribuicao;
    if (atribuicao instanceof Expressao.Atribuicao) {
      String lexeme = ((Expressao.Atribuicao) atribuicao).nome.lexeme;
      escritor.concatenarNaLinha("readln(" + lexeme + ");").addQuebraLinha();

    }
    if (atribuicao instanceof Expressao.AtribuicaoArray) {
      String lexeme = ((Expressao.AtribuicaoArray) atribuicao).nome.lexeme;
      escritor.concatenarNaLinha("readln(" + lexeme + "[");
      evaluate(((Expressao.AtribuicaoArray) atribuicao).index);

      escritor.concatenarNaLinha("]);").addQuebraLinha();
    }
    return null;
  }

  @Override
  public Void visitVarDeclaracao(Var declaracao) {
    if (declaracao.tipo.type == TiposToken.TIPO_MODULO) {
      return null;
    }
    String tipo = this.tipoVariavel(declaracao.tipo.type);
    escritor.concatenarNaLinha(declaracao.nome.lexeme + " : " + tipo + ";").addQuebraLinha();
    return null;
  }

  @Override
  public Void visitVarDeclaracoesDeclaracao(VarDeclaracoes declaracao) {
    if (declaracao.variaveis.size() <= 1) {
      execute(declaracao.variaveis.get(0));
      return null;
    }
    List<Declaracao> lista = declaracao.variaveis;
    Declaracao primeiro = lista.get(0);

    if (primeiro instanceof Declaracao.Var) {
      Token tipoVar = ((Declaracao.Var) primeiro).tipo;
      if (tipoVar.type == TiposToken.TIPO_MODULO) {
        return null;
      }
      String tipo = this.tipoVariavel(tipoVar.type);

      for (int i = 0; i < lista.size(); i++) {
        Declaracao.Var varriavel = (Declaracao.Var) lista.get(i);
        escritor.concatenarNaLinha(varriavel.nome.lexeme);
        if (i < lista.size() - 1) {
          escritor.concatenarNaLinha(", ");
        }
      }

      escritor.concatenarNaLinha(" : " + tipo + ";").addQuebraLinha();
    } else if (primeiro instanceof Declaracao.VariavelArray) {
      Token tipoVar = ((Declaracao.VariavelArray) primeiro).tipo;
      String tipo = this.tipoVariavel(tipoVar.type);

      for (int i = 0; i < lista.size(); i++) {
        Declaracao.VariavelArray varriavel = (Declaracao.VariavelArray) lista.get(i);
        VariavelVetor vv = new VariavelVetor(varriavel.tipo.type,
            (int) ((Expressao.Literal) varriavel.intervaloI).valor,
            (int) ((Expressao.Literal) varriavel.intervaloF).valor);
        addVariavelVetor(varriavel.nome.lexeme, vv);
        escritor.concatenarNaLinha(varriavel.nome.lexeme + "[" + vv.getTamanho() + "]");
        if (i < lista.size() - 1) {
          escritor.concatenarNaLinha(", ");
        }
      }
      escritor.concatenarNaLinha(" : " + tipo + ";").addQuebraLinha();

    }

    return null;
  }

  @Override
  public Void visitVariavelArrayDeclaracao(VariavelArray declaracao) {
    String tipo = this.tipoVariavel(declaracao.tipo.type);
    VariavelVetor vv = new VariavelVetor(declaracao.tipo.type,
        (int) ((Expressao.Literal) declaracao.intervaloI).valor,
        (int) ((Expressao.Literal) declaracao.intervaloF).valor);

    escritor.concatenarNaLinha(declaracao.nome.lexeme + " : array[" + vv.getIntervaloI() + ".."
        + vv.getIntervaloF() + "] of " + tipo + ";").addQuebraLinha();
    addVariavelVetor(declaracao.nome.lexeme, vv);
    return null;
  }

  @Override
  public Void visitParaDeclaracao(Para declaracao) {
    evaluate(declaracao.atribuicao);
    escritor.concatenarNaLinha(";").addQuebraLinha().concatenarNaLinha("while "); 
    evaluate(declaracao.condicao); 
    escritor.concatenarNaLinha(" do").addQuebraLinha().concatenarNaLinha("begin").addQuebraLinha()
    .indentar();
    execute(declaracao.facaBloco);
    evaluate(declaracao.incremento);   
    escritor.removerIdentacao().addQuebraLinha().concatenarNaLinha("end;").addQuebraLinha();
    return null;
  }

  @Override
  public Void visitEnquantoDeclaracao(Enquanto declaracao) {
    escritor.concatenarNaLinha("while ");
    evaluate(declaracao.condicao);
    escritor.concatenarNaLinha(" do").addQuebraLinha().concatenarNaLinha("begin").addQuebraLinha()
        .indentar();
    execute(declaracao.corpo);
    escritor.removerIdentacao().concatenarNaLinha("end;").addQuebraLinha();

    return null;
  }

  @Override
  public Void visitRepitaDeclaracao(Repita declaracao) {
    escritor.concatenarNaLinha("repeat").indentar().addQuebraLinha();
    execute(declaracao.corpo);
    escritor.removerIdentacao().concatenarNaLinha("until ");
    evaluate(declaracao.condicao);
    escritor.concatenarNaLinha(";").addQuebraLinha();
    return null;
  }

  @Override
  public Void visitModuloDeclaracao(Modulo declaracao) {
    escritor.concatenarNaLinha("procedure " + declaracao.nome.lexeme + " (); ").addQuebraLinha()
        .concatenarNaLinha("begin").addQuebraLinha().indentar();
    execute(declaracao.corpo);
    escritor.removerIdentacao().concatenarNaLinha("end;").addQuebraLinha(2);
    return null;
  }

  @Override
  public Void visitChamadaModuloDeclaracao(ChamadaModulo declaracao) {
    escritor.concatenarNaLinha(declaracao.identificador.lexeme + "();").addQuebraLinha();
    return null;
  }

  @Override
  public Void visitProgramaDeclaracao(Programa declaracao) {

    escritor.concatenarNaLinha("program MeuPrograma;").addQuebraLinha().concatenarNaLinha("var")
        .addQuebraLinha().indentar();

    // converte variaveis
    for (Declaracao variaveis : declaracao.variaveis) {
      execute(variaveis);
    }
    escritor.addQuebraLinha().removerIdentacao();

    // converte modulos-funcoes
    for (Declaracao modulo : declaracao.modulos) {
      execute(modulo);
    }
    escritor.addQuebraLinha().removerIdentacao().concatenarNaLinha("begin").addQuebraLinha()
        .indentar();

    // converte inicio-fim
    for (Declaracao corpo : declaracao.corpo) {
      execute(corpo);
    }

    escritor.removerIdentacao().concatenarNaLinha("end."); // fim programa

    return null;
  }

  @Override
  public Void visitBinarioExpressao(Binario expressao) {
    evaluate(expressao.esquerda);
    switch (expressao.operador.type) {
      case MENOS:
        escritor.concatenarNaLinha(" - ");
        break;
      case BARRA:
        escritor.concatenarNaLinha(" / ");
        break;
      case ASTERISCO:
        escritor.concatenarNaLinha(" * ");
        break;
      case MAIS:
        escritor.concatenarNaLinha(" + ");
        break;
      case MAIOR_QUE:
        escritor.concatenarNaLinha(" > ");
        break;
      case MAIOR_IQUAL:
        escritor.concatenarNaLinha(" >= ");
        break;
      case MENOR_QUE:
        escritor.concatenarNaLinha(" < ");
        break;
      case MENOR_IGUAL:
        escritor.concatenarNaLinha(" <= ");
        break;
      case DIFERENTE:
        escritor.concatenarNaLinha(" <> ");
        break;
      case IGUAL:
        escritor.concatenarNaLinha(" == ");
        break;
      default:
        break; // throw erro?
    }
    evaluate(expressao.direita);
    return null;
  }

  @Override
  public Void visitGrupoExpressao(Grupo expressao) {
    evaluate(expressao.expressao);
    return null;
  }

  @Override
  public Void visitExpParentizadaExpressao(ExpParentizada expressao) {
    escritor.concatenarNaLinha("(");
    evaluate(expressao.grupo);
    escritor.concatenarNaLinha(")");
    return null;
  }

  @Override
  public Void visitLiteralExpressao(Literal expressao) {
    Object valor = expressao.valor;
    if (valor instanceof String) {
      valor = "'".concat((String) valor).concat("'");
    } else if (valor instanceof Character) {
      valor = "'".concat(((Character) valor).toString()).concat("'");
    }
    escritor.concatenarNaLinha(valor.toString());
    return null;
  }

  @Override
  public Void visitLogicoExpressao(Logico expressao) {
    evaluate(expressao.esquerda);
    escritor.concatenarNaLinha(" " + getOperadorLogico(expressao.operador.type) + " ");

    evaluate(expressao.direita);
    return null;
  }

  @Override
  public Void visitUnarioExpressao(Unario expressao) {
    switch (expressao.operador.type) {
      case NAO:
        escritor.concatenarNaLinha("not ");
        break;
      case MENOS:
        escritor.concatenarNaLinha("-");
        break;

      default:
        break; // throw error?
    }
    evaluate(expressao.direira);
    return null;
  }

  @Override
  public Void visitAtribuicaoExpressao(Atribuicao expressao) {
    escritor.concatenarNaLinha(expressao.nome.lexeme + " := ");
    evaluate(expressao.valor);
    return null;
  }

  @Override
  public Void visitAtribuicaoArrayExpressao(AtribuicaoArray expressao) {
    escritor.concatenarNaLinha(expressao.nome.lexeme + "[");

    evaluate(expressao.index);
    escritor.concatenarNaLinha("] := ");
    evaluate(expressao.valor);
    return null;
  }

  @Override
  public Void visitVariavelArrayExpressao(modelos.tree.Expressao.VariavelArray expressao) {
    String nome = expressao.nome.lexeme;
    if (expressao.index == null) {
      escritor.concatenarNaLinha(nome);
    } else {
      escritor.concatenarNaLinha(nome + "[");
      evaluate(expressao.index);
      escritor.concatenarNaLinha("]");
    }

    return null;
  }

  @Override
  public Void visitVariavelExpressao(Variavel expressao) {
    escritor.concatenarNaLinha(expressao.nome.lexeme);
    return null;
  }

  @Override
  public Void visitFimDeclaracao(Fim declaracao) {
    // TODO Auto-generated method stub
    return null;
  }

}
