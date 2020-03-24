package tree;

import java.util.List;

import model.Token;

public abstract class Expressao extends AstDebugNode {
public interface Visitor<R> {
public R visitBinarioExpressao(Binario expressao);
public R visitGrupoExpressao(Grupo expressao);
public R visitExpParentizadaExpressao(ExpParentizada expressao);
public R visitLiteralExpressao(Literal expressao);
public R visitLogicoExpressao(Logico expressao);
public R visitUnarioExpressao(Unario expressao);
public R visitAtribuicaoExpressao(Atribuicao expressao);
public R visitAtribuicaoArrayExpressao(AtribuicaoArray expressao);
public R visitVariavelArrayExpressao(VariavelArray expressao);
public R visitVariavelExpressao(Variavel expressao);
  }
public static class Binario extends Expressao {
    public Binario(Expressao esquerda, Token operador, Expressao direita) {
      this.esquerda = esquerda;
      this.operador = operador;
      this.direita = direita;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinarioExpressao(this);
    }

    public final Expressao esquerda;
    public final Token operador;
    public final Expressao direita;
  }
public static class Grupo extends Expressao {
    public Grupo(Expressao expressao) {
      this.expressao = expressao;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitGrupoExpressao(this);
    }

    public final Expressao expressao;
  }
public static class ExpParentizada extends Expressao {
    public ExpParentizada(Grupo grupo) {
      this.grupo = grupo;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpParentizadaExpressao(this);
    }

    public final Grupo grupo;
  }
public static class Literal extends Expressao {
    public Literal(Object valor) {
      this.valor = valor;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpressao(this);
    }

    public final Object valor;
  }
public static class Logico extends Expressao {
    public Logico(Expressao esquerda, Token operador, Expressao direita) {
      this.esquerda = esquerda;
      this.operador = operador;
      this.direita = direita;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicoExpressao(this);
    }

    public final Expressao esquerda;
    public final Token operador;
    public final Expressao direita;
  }
public static class Unario extends Expressao {
    public Unario(Token operador, Expressao direira) {
      this.operador = operador;
      this.direira = direira;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnarioExpressao(this);
    }

    public final Token operador;
    public final Expressao direira;
  }
public static class Atribuicao extends Expressao {
    public Atribuicao(Token nome, Expressao valor) {
      this.nome = nome;
      this.valor = valor;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitAtribuicaoExpressao(this);
    }

    public final Token nome;
    public final Expressao valor;
  }
public static class AtribuicaoArray extends Expressao {
    public AtribuicaoArray(Token nome, Expressao index, Expressao valor) {
      this.nome = nome;
      this.index = index;
      this.valor = valor;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitAtribuicaoArrayExpressao(this);
    }

    public final Token nome;
    public final Expressao index;
    public final Expressao valor;
  }
public static class VariavelArray extends Expressao {
    public VariavelArray(Token nome, Expressao index) {
      this.nome = nome;
      this.index = index;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariavelArrayExpressao(this);
    }

    public final Token nome;
    public final Expressao index;
  }
public static class Variavel extends Expressao {
    public Variavel(Token nome) {
      this.nome = nome;
    }

    public <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariavelExpressao(this);
    }

    public final Token nome;
  }

  public abstract <R> R accept(Visitor<R> visitor);
}
