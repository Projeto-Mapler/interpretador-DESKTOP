package tree;

import java.util.List;

import model.Token;

public abstract class Declaracao {
public interface Visitor<R> {
public R visitBlocoDeclaracao(Bloco declaracao);
public R visitExpressaoDeclaracao(Expressao declaracao);
public R visitPrintDeclaracao(Print declaracao);
public R visitSeDeclaracao(Se declaracao);
public R visitLerDeclaracao(Ler declaracao);
public R visitVarDeclaracao(Var declaracao);
public R visitParaDeclaracao(Para declaracao);
public R visitEnquantoDeclaracao(Enquanto declaracao);
public R visitProgramaDeclaracao(Programa declaracao);
  }
public static class Bloco extends Declaracao {
    public Bloco(List<Declaracao> declaracoes) {
      this.declaracoes = declaracoes;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlocoDeclaracao(this);
    }

    public final List<Declaracao> declaracoes;
  }
public static class Expressao extends Declaracao {
    public Expressao(tree.Expressao expressao) {
      this.expressao = expressao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressaoDeclaracao(this);
    }

    public final tree.Expressao expressao;
  }
public static class Print extends Declaracao {
    public Print(tree.Expressao expressao) {
      this.expressao = expressao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintDeclaracao(this);
    }

    public final tree.Expressao expressao;
  }
public static class Se extends Declaracao {
    public Se(tree.Expressao condicao, Bloco entaoBloco, Bloco senaoBloco) {
      this.condicao = condicao;
      this.entaoBloco = entaoBloco;
      this.senaoBloco = senaoBloco;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitSeDeclaracao(this);
    }

    public final tree.Expressao condicao;
    public final Bloco entaoBloco;
    public final Bloco senaoBloco;
  }
public static class Ler extends Declaracao {
    public Ler(tree.Expressao expressao) {
      this.expressao = expressao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLerDeclaracao(this);
    }

    public final tree.Expressao expressao;
  }
public static class Var extends Declaracao {
    public Var(Token nome, Token tipo) {
      this.nome = nome;
      this.tipo = tipo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarDeclaracao(this);
    }

    public final Token nome;
    public final Token tipo;
  }
public static class Para extends Declaracao {
    public Para(tree.Expressao atribuicao, tree.Expressao condicao, tree.Expressao incremento, Bloco facaBloco) {
      this.atribuicao = atribuicao;
      this.condicao = condicao;
      this.incremento = incremento;
      this.facaBloco = facaBloco;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitParaDeclaracao(this);
    }

    public final tree.Expressao atribuicao;
    public final tree.Expressao condicao;
    public final tree.Expressao incremento;
    public final Bloco facaBloco;
  }
public static class Enquanto extends Declaracao {
    public Enquanto(tree.Expressao condicao, Bloco corpo) {
      this.condicao = condicao;
      this.corpo = corpo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitEnquantoDeclaracao(this);
    }

    public final tree.Expressao condicao;
    public final Bloco corpo;
  }
public static class Programa extends Declaracao {
    public Programa(List<Declaracao> variaveis, List<Declaracao> corpo) {
      this.variaveis = variaveis;
      this.corpo = corpo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitProgramaDeclaracao(this);
    }

    public final List<Declaracao> variaveis;
    public final List<Declaracao> corpo;
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
