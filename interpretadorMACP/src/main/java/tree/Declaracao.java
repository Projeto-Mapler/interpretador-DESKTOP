package tree;

import java.util.List;

import model.Token;

public abstract class Declaracao {
public interface Visitor<R> {
public R visitBlocoDeclaracao(Bloco declaracao);
public R visitExpressaoDeclaracao(Expressao declaracao);
public R visitEscrevaDeclaracao(Escreva declaracao);
public R visitSeDeclaracao(Se declaracao);
public R visitLerDeclaracao(Ler declaracao);
public R visitVarDeclaracao(Var declaracao);
public R visitVarDeclaracoesDeclaracao(VarDeclaracoes declaracao);
public R visitVariavelArrayDeclaracao(VariavelArray declaracao);
public R visitParaDeclaracao(Para declaracao);
public R visitEnquantoDeclaracao(Enquanto declaracao);
public R visitRepitaDeclaracao(Repita declaracao);
public R visitModuloDeclaracao(Modulo declaracao);
public R visitChamadaModuloDeclaracao(ChamadaModulo declaracao);
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
public static class Escreva extends Declaracao {
    public Escreva(List<tree.Expressao> expressoes) {
      this.expressoes = expressoes;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitEscrevaDeclaracao(this);
    }

    public final List<tree.Expressao> expressoes;
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
    public Ler(tree.Expressao atribuicao) {
      this.atribuicao = atribuicao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLerDeclaracao(this);
    }

    public final tree.Expressao atribuicao;
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
public static class VarDeclaracoes extends Declaracao {
    public VarDeclaracoes(List<Declaracao> variaveis) {
      this.variaveis = variaveis;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarDeclaracoesDeclaracao(this);
    }

    public final List<Declaracao> variaveis;
  }
public static class VariavelArray extends Declaracao {
    public VariavelArray(Token nome, tree.Expressao intervaloI, tree.Expressao intervaloF, Token tipo) {
      this.nome = nome;
      this.intervaloI = intervaloI;
      this.intervaloF = intervaloF;
      this.tipo = tipo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariavelArrayDeclaracao(this);
    }

    public final Token nome;
    public final tree.Expressao intervaloI;
    public final tree.Expressao intervaloF;
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
public static class Repita extends Declaracao {
    public Repita(Bloco corpo, tree.Expressao condicao) {
      this.corpo = corpo;
      this.condicao = condicao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitRepitaDeclaracao(this);
    }

    public final Bloco corpo;
    public final tree.Expressao condicao;
  }
public static class Modulo extends Declaracao {
    public Modulo(Token nome, Bloco corpo) {
      this.nome = nome;
      this.corpo = corpo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitModuloDeclaracao(this);
    }

    public final Token nome;
    public final Bloco corpo;
  }
public static class ChamadaModulo extends Declaracao {
    public ChamadaModulo(Token identificador) {
      this.identificador = identificador;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitChamadaModuloDeclaracao(this);
    }

    public final Token identificador;
  }
public static class Programa extends Declaracao {
    public Programa(List<Declaracao> variaveis, List<Declaracao> corpo, List<Declaracao> modulos) {
      this.variaveis = variaveis;
      this.corpo = corpo;
      this.modulos = modulos;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitProgramaDeclaracao(this);
    }

    public final List<Declaracao> variaveis;
    public final List<Declaracao> corpo;
    public final List<Declaracao> modulos;
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
