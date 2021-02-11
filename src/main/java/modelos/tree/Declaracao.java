package modelos.tree;

import java.util.List;

import modelos.Token;

public abstract class Declaracao extends AstDebugNode {
    public Declaracao( int linha) {
super(linha);
}
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
    public Bloco( int linha, List<Declaracao> declaracoes) {
super(linha);
      this.declaracoes = declaracoes;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlocoDeclaracao(this);
    }

    public final List<Declaracao> declaracoes;
  }
public static class Expressao extends Declaracao {
    public Expressao( int linha, modelos.tree.Expressao expressao) {
super(linha);
      this.expressao = expressao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressaoDeclaracao(this);
    }

    public final modelos.tree.Expressao expressao;
  }
public static class Escreva extends Declaracao {
    public Escreva( int linha, List<modelos.tree.Expressao> expressoes) {
super(linha);
      this.expressoes = expressoes;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitEscrevaDeclaracao(this);
    }

    public final List<modelos.tree.Expressao> expressoes;
  }
public static class Se extends Declaracao {
    public Se( int linha, modelos.tree.Expressao condicao, Bloco entaoBloco, Bloco senaoBloco) {
super(linha);
      this.condicao = condicao;
      this.entaoBloco = entaoBloco;
      this.senaoBloco = senaoBloco;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitSeDeclaracao(this);
    }

    public final modelos.tree.Expressao condicao;
    public final Bloco entaoBloco;
    public final Bloco senaoBloco;
  }
public static class Ler extends Declaracao {
    public Ler( int linha, modelos.tree.Expressao atribuicao) {
super(linha);
      this.atribuicao = atribuicao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLerDeclaracao(this);
    }

    public final modelos.tree.Expressao atribuicao;
  }
public static class Var extends Declaracao {
    public Var( int linha, Token nome, Token tipo) {
super(linha);
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
    public VarDeclaracoes( int linha, List<Declaracao> variaveis) {
super(linha);
      this.variaveis = variaveis;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarDeclaracoesDeclaracao(this);
    }

    public final List<Declaracao> variaveis;
  }
public static class VariavelArray extends Declaracao {
    public VariavelArray( int linha, Token nome, modelos.tree.Expressao intervaloI, modelos.tree.Expressao intervaloF, Token tipo) {
super(linha);
      this.nome = nome;
      this.intervaloI = intervaloI;
      this.intervaloF = intervaloF;
      this.tipo = tipo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariavelArrayDeclaracao(this);
    }

    public final Token nome;
    public final modelos.tree.Expressao intervaloI;
    public final modelos.tree.Expressao intervaloF;
    public final Token tipo;
  }
public static class Para extends Declaracao {
    public Para( int linha, modelos.tree.Expressao atribuicao, modelos.tree.Expressao condicao, modelos.tree.Expressao incremento, Bloco facaBloco) {
super(linha);
      this.atribuicao = atribuicao;
      this.condicao = condicao;
      this.incremento = incremento;
      this.facaBloco = facaBloco;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitParaDeclaracao(this);
    }

    public final modelos.tree.Expressao atribuicao;
    public final modelos.tree.Expressao condicao;
    public final modelos.tree.Expressao incremento;
    public final Bloco facaBloco;
  }
public static class Enquanto extends Declaracao {
    public Enquanto( int linha, modelos.tree.Expressao condicao, Bloco corpo) {
super(linha);
      this.condicao = condicao;
      this.corpo = corpo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitEnquantoDeclaracao(this);
    }

    public final modelos.tree.Expressao condicao;
    public final Bloco corpo;
  }
public static class Repita extends Declaracao {
    public Repita( int linha, Bloco corpo, modelos.tree.Expressao condicao) {
super(linha);
      this.corpo = corpo;
      this.condicao = condicao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitRepitaDeclaracao(this);
    }

    public final Bloco corpo;
    public final modelos.tree.Expressao condicao;
  }
public static class Modulo extends Declaracao {
    public Modulo( int linha, Token nome, Bloco corpo) {
super(linha);
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
    public ChamadaModulo( int linha, Token identificador) {
super(linha);
      this.identificador = identificador;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitChamadaModuloDeclaracao(this);
    }

    public final Token identificador;
  }
public static class Programa extends Declaracao {
    public Programa( int linha, List<Declaracao> variaveis, List<Declaracao> corpo, List<Declaracao> modulos) {
super(linha);
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
