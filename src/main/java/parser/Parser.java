package parser;

import static modelos.TiposToken.ASTERISCO;
import static modelos.TiposToken.ATE;
import static modelos.TiposToken.ATRIBUICAO;
import static modelos.TiposToken.BARRA;
import static modelos.TiposToken.CADEIA;
import static modelos.TiposToken.CARACTERE;
import static modelos.TiposToken.DE;
import static modelos.TiposToken.DIFERENTE;
import static modelos.TiposToken.DIR_CHAVES;
import static modelos.TiposToken.DIR_COLCHETE;
import static modelos.TiposToken.DIR_PARENTESES;
import static modelos.TiposToken.DOIS_PONTOS;
import static modelos.TiposToken.E;
import static modelos.TiposToken.ENQUANTO;
import static modelos.TiposToken.ENTAO;
import static modelos.TiposToken.EOF;
import static modelos.TiposToken.ESCREVER;
import static modelos.TiposToken.ESQ_CHAVES;
import static modelos.TiposToken.ESQ_COLCHETE;
import static modelos.TiposToken.ESQ_PARENTESES;
import static modelos.TiposToken.FACA;
import static modelos.TiposToken.FALSO;
import static modelos.TiposToken.FIM;
import static modelos.TiposToken.IDENTIFICADOR;
import static modelos.TiposToken.IGUAL;
import static modelos.TiposToken.INICIO;
import static modelos.TiposToken.INTEIRO;
import static modelos.TiposToken.INTERVALO;
import static modelos.TiposToken.LER;
import static modelos.TiposToken.MAIOR_IQUAL;
import static modelos.TiposToken.MAIOR_QUE;
import static modelos.TiposToken.MAIS;
import static modelos.TiposToken.MENOR_IGUAL;
import static modelos.TiposToken.MENOR_QUE;
import static modelos.TiposToken.MENOS;
import static modelos.TiposToken.NAO;
import static modelos.TiposToken.OU;
import static modelos.TiposToken.PARA;
import static modelos.TiposToken.PASSO;
import static modelos.TiposToken.PONTO_VIRGULA;
import static modelos.TiposToken.REAL;
import static modelos.TiposToken.REPITA;
import static modelos.TiposToken.SE;
import static modelos.TiposToken.SENAO;
import static modelos.TiposToken.TIPO_CADEIA;
import static modelos.TiposToken.TIPO_CARACTERE;
import static modelos.TiposToken.TIPO_INTEIRO;
import static modelos.TiposToken.TIPO_LOGICO;
import static modelos.TiposToken.TIPO_MODULO;
import static modelos.TiposToken.TIPO_REAL;
import static modelos.TiposToken.TIPO_VETOR;
import static modelos.TiposToken.VARIAVEIS;
import static modelos.TiposToken.VERDADEIRO;
import static modelos.TiposToken.VIRGULA;
import java.util.ArrayList;
import java.util.List;
import debug.GerenciadorEventos;
import debug.TiposEvento;
import modelos.ParserError;
import modelos.TiposToken;
import modelos.Token;
import tree.Declaracao;
import tree.Expressao;

/**
 * Análise Sintática
 * 
 * @author Kerlyson
 *
 */
public class Parser {
  
  private final List<Token> tokens;
  private int indexTokenAtual = 0;
  private GerenciadorEventos gerenciadorEventos;

  public Parser(List<Token> tokens, GerenciadorEventos eventos) {
    this.gerenciadorEventos = eventos;
    this.tokens = tokens;
  }

  public Declaracao.Programa parse() {
    List<Declaracao> variaveis = new ArrayList<Declaracao>();
    List<Declaracao> corpo = new ArrayList<Declaracao>();
    List<Declaracao> modulos = new ArrayList<Declaracao>();
    
    try {
      Token variaveisToken = consumirToken(VARIAVEIS, "Esperado \"variaveis\"");
      
      while (!isFimDoArquivo() && espiar().type != INICIO) {
        variaveis.add(declaracaoVariaveis());
      }
      
      consumirToken(INICIO, "Esperado \"inicio\"");

      while (!isFimDoArquivo() && espiar().type != FIM) {
        corpo.add(declaracao());
      }
      
      consumirToken(FIM, "Esperado \"fim\"");

      while (!isFimDoArquivo()) {
        modulos.add(declaracaoModulo());
      }
      
      return new Declaracao.Programa(variaveisToken.line, variaveis, corpo, modulos);
    } catch (ParserError e) {
      this.gerenciadorEventos.notificar(TiposEvento.ERRO_PARSE, e);
    }
    
    return null;
  }

  // NAVEGADORES:
  private boolean isFimDoArquivo() {
    return espiar().type == EOF;
  }

  private Token espiar() {
    return tokens.get(indexTokenAtual);
  }

  private Token anterior() {
    return tokens.get(indexTokenAtual - 1);
  }

  private Token avancar() {
    if (!isFimDoArquivo())
      indexTokenAtual++;
    return anterior();
  }

  private boolean checar(TiposToken type) {
    if (isFimDoArquivo())
      return false;
    return espiar().type == type;
  }

  private boolean isTokenTypeIgualA(TiposToken... types) {
    for (TiposToken type : types) {
      if (checar(type)) {
        avancar();
        return true;
      }
    }

    return false;
  }

  private Token consumirToken(TiposToken type, String message) {
    if (checar(type))
      return avancar();

    throw error(espiar(), message);
  }

  /**
   * Permite 'sincronizar' a execução. Ao encontrar um erro a execução continua até todo o arquivo
   * passar pelo parser.
   * 
   * Assim, é possivel achar todos os erros no arquivo em vez de um por um.
   */
  private void sincronizar() {
    avancar();

    while (!isFimDoArquivo()) {
      if (anterior().type == PONTO_VIRGULA)
        return;

      switch (espiar().type) {
        case VARIAVEIS:
        case INICIO:
        case FIM:
        case ENQUANTO:
        case PARA:
        case SE:
        case LER:
        case ESCREVER:
        case REPITA:
          return;
        default: break;
      }

      avancar();
    }
  }

  private ParserError error(Token token, String mensagem) {
    ParserError erro = new ParserError(token, mensagem);
    this.gerenciadorEventos.notificar(TiposEvento.ERRO_PARSE, erro);
    return erro;
  }

  // REGRAS:
  /**
   * declaracaoVariaveis → IDENTIFICADOR ("," IDENTIFICADOR) ":" TIPO_DADO | varArray ";"
   * 
   * @return
   */
  private Declaracao declaracaoVariaveis() {
    List<Declaracao> retorno = new ArrayList<Declaracao>();
    List<Token> nomes = new ArrayList<Token>();
    do {
      nomes.add(consumirToken(IDENTIFICADOR, "Esperado nome da variavel."));
    } while (isTokenTypeIgualA(VIRGULA));
    consumirToken(DOIS_PONTOS, "Esperado ':' ");

    if (isTokenTypeIgualA(TIPO_VETOR)) {
      consumirToken(ESQ_COLCHETE, "Esperado [");
      Token intervaloI = consumirToken(INTEIRO, "Esperado valor inteiro positivo");
      consumirToken(INTERVALO, "Esperado ..");
      Token intervaloF = consumirToken(INTEIRO, "Esperado valor inteiro positivo");
      consumirToken(DIR_COLCHETE, "Esperado ]");
      consumirToken(DE, "Esperado de");
      Token tipoDoVetor = tipoDado();
      for (Token nome : nomes) {
        Declaracao declaracaoVariavelArray =
            declaracaoVariavelArray(nome, intervaloI, intervaloF, tipoDoVetor);
        retorno.add(declaracaoVariavelArray);
      }
    } else {
      Token tipo = tipoDado();
      for (Token nome : nomes) {
        Declaracao.Var declaracao = new Declaracao.Var(nome.line, nome, tipo);
        retorno.add(declaracao);
      }
    }
    consumirToken(PONTO_VIRGULA, "Esperado ;");
    return new Declaracao.VarDeclaracoes(nomes.get(0).line, retorno);
  }

  /**
   * declaracao → expressaoDeclarativa | escrever | ler | bloco | se | enquanto | para | repita
   * 
   * @return
   */
  private Declaracao declaracao() {
    try {
      if (isTokenTypeIgualA(SE))
        return seDeclaracao();
      if (isTokenTypeIgualA(PARA))
        return paraDeclaracao();
      if (isTokenTypeIgualA(ENQUANTO))
        return enquantoDeclaracao();
      if (isTokenTypeIgualA(ESCREVER))
        return escreverDeclaracao();
      if (isTokenTypeIgualA(LER))
        return lerDeclaracao();
      if (isTokenTypeIgualA(ESQ_CHAVES))
        return new Declaracao.Bloco(anterior().line, bloco());
      if (isTokenTypeIgualA(REPITA))
        return repitaDeclaracao();
      return expressaoDeclaracao();
    } catch (ParserError error) {
      sincronizar();
      return null;
    }
  }

  /**
   * "{" (declaracao)* "}"
   * 
   * @return
   */
  private List<Declaracao> bloco() {
    List<Declaracao> declaracoes = new ArrayList<>();

    while (!checar(DIR_CHAVES) && !isFimDoArquivo()) {
      declaracoes.add(declaracao());
    }

    consumirToken(DIR_CHAVES, "Esperado '}' depois do bloco.");
    return declaracoes;
  }

  /**
   * escrever → "escrever" expressao";"
   * 
   * @return
   */
  private Declaracao escreverDeclaracao() {
    List<Expressao> expressoes = new ArrayList<Expressao>();
    do {
      expressoes.add(expressao());
    } while (isTokenTypeIgualA(VIRGULA));
    consumirToken(PONTO_VIRGULA, "Esperado ';'");
    return new Declaracao.Escreva(anterior().line, expressoes);
  }

  /**
   * ler → "ler" variavel ";"
   * 
   * @return
   */
  private Declaracao lerDeclaracao() {
    Expressao expressao = ou();
    Declaracao retorno = null;
    if (expressao instanceof Expressao.VariavelArray) {
      Token nome = ((Expressao.VariavelArray) expressao).nome;
      Expressao index = ((Expressao.VariavelArray) expressao).index;
      retorno = new Declaracao.Ler(nome.line,
          new Expressao.AtribuicaoArray(nome.line, nome, index, null));
    } else if (expressao instanceof Expressao.Variavel) {
      Token nome = ((Expressao.Variavel) expressao).nome;
      retorno = new Declaracao.Ler(nome.line, new Expressao.Atribuicao(nome.line, nome, null));
    } else {
      error(anterior(), "Esperado uma variável");
    }

    consumirToken(PONTO_VIRGULA, "Esperado ';'");
    return retorno;
  }

  /**
   * expressaoDeclarativa → expressao ";"
   * 
   * @return
   */
  private Declaracao expressaoDeclaracao() {
    Expressao expressao = expressao();
    consumirToken(PONTO_VIRGULA, "Esperado ';'");
    if (expressao instanceof Expressao.Variavel) {
      return chamadaModulo(((Expressao.Variavel) expressao).nome);
    }
    return new Declaracao.Expressao(anterior().line, expressao);
  }

  /**
   * expressao → atribuicao
   * 
   * @return
   */
  private Expressao expressao() {
    return atribuicao();
  }

  /**
   * expParentizada → "(" expressao ")"
   * 
   * @return
   */
  private Expressao expParentizada() {
    Expressao expressao = expressao();
    consumirToken(DIR_PARENTESES, "Esperado ')' depois da expressao.");
    return new Expressao.ExpParentizada(anterior().line,
        new Expressao.Grupo(anterior().line, expressao));
  }

  /**
   * atribuicao → (IDENTIFICADOR "<-" atribuicao) | ou
   * 
   * @return
   */
  private Expressao atribuicao() {
    Expressao expressao = ou();
    if (isTokenTypeIgualA(ATRIBUICAO)) {
      Token atribuicao = anterior();
      Expressao valor = atribuicao();

      if (expressao instanceof Expressao.Variavel) {
        Token nome = ((Expressao.Variavel) expressao).nome;
        return new Expressao.Atribuicao(nome.line, nome, valor);
      }
      if (expressao instanceof Expressao.VariavelArray) {
        Token nome = ((Expressao.VariavelArray) expressao).nome;
        Expressao index = ((Expressao.VariavelArray) expressao).index;
        return new Expressao.AtribuicaoArray(nome.line, nome, index, valor);
      }
      error(atribuicao, "Atribuicao inválida");
    }
    return expressao;
  }

  /**
   * ou → e ("ou" e)*
   * 
   * @return
   */
  private Expressao ou() {
    Expressao expressao = e();

    while (isTokenTypeIgualA(OU)) {
      Token operador = anterior();
      Expressao direita = e();
      expressao = new Expressao.Logico(operador.line, expressao, operador, direita);
    }
    return expressao;
  }

  /**
   * e → igualdade ("e" igualdade)*
   * 
   * @return
   */
  private Expressao e() {
    Expressao expressao = igualdade();

    while (isTokenTypeIgualA(E)) {
      Token operador = anterior();
      Expressao direita = igualdade();
      expressao = new Expressao.Logico(operador.line, expressao, operador, direita);
    }
    return expressao;
  }

  /**
   * igualdade → comparacao ( ( "!=" | "==" ) comparacao)*
   * 
   * @return
   */
  private Expressao igualdade() {
    Expressao expressao = comparacao(); // esquerda

    while (isTokenTypeIgualA(DIFERENTE, IGUAL)) {
      Token operador = anterior();
      Expressao direita = comparacao();
      expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
    }

    return expressao; // esquerda ou Binario
  }

  /**
   * comparacao → adicao( ( ">" | ">=" | "<" | "<=" ) adicao)*
   * 
   * @return
   */
  private Expressao comparacao() {
    Expressao expressao = adicao(); // esquerda

    while (isTokenTypeIgualA(MAIOR_QUE, MAIOR_IQUAL, MENOR_QUE, MENOR_IGUAL)) {
      Token operador = anterior();
      Expressao direita = adicao();
      expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
    }

    return expressao; // esquerda ou Binario
  }

  /**
   * adicao → multiplication ( ( "-" | "+" ) multiplication )*
   * 
   * @return
   */
  private Expressao adicao() {
    Expressao expressao = multiplicacao(); // esquerda

    while (isTokenTypeIgualA(MENOS, MAIS)) {
      Token operador = anterior();
      Expressao direita = multiplicacao();
      expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
    }

    return expressao; // esquerda ou Binario
  }

  /**
   * multiplicacao → unario ( ( "/" | "*" ) unario)*
   * 
   * @return
   */
  private Expressao multiplicacao() {
    Expressao expressao = unario(); // esquerda

    while (isTokenTypeIgualA(BARRA, ASTERISCO)) {
      Token operador = anterior();
      Expressao direita = unario();
      expressao = new Expressao.Binario(operador.line, expressao, operador, direita);
    }

    return expressao; // esquerda ou Binario
  }

  /**
   * unario → ( "nao" | "-" ) unario | primario
   * 
   * @return
   */
  private Expressao unario() {
    if (isTokenTypeIgualA(NAO, MENOS)) {
      Token operador = anterior();
      Expressao direita = unario();
      return new Expressao.Unario(operador.line, operador, direita);
    }
    return primario();
  }

  /**
   * primario → INTEIRO | REAL | CADEIA | CARACTERE | VERDADEIRO | FALSO | variavel | expParentizada
   * 
   * @return
   */
  private Expressao primario() {
    if (isTokenTypeIgualA(IDENTIFICADOR)) {
      Token identificador = anterior();
      if (isTokenTypeIgualA(ESQ_COLCHETE)) {
        Expressao index = ou();
        consumirToken(DIR_COLCHETE, "Esperado ]");
        return new Expressao.VariavelArray(identificador.line, identificador, index);
      }
      return new Expressao.Variavel(identificador.line, identificador);
    }
    if (isTokenTypeIgualA(FALSO))
      return new Expressao.Literal(anterior().line, false, anterior());
    if (isTokenTypeIgualA(VERDADEIRO))
      return new Expressao.Literal(anterior().line, true, anterior());

    if (isTokenTypeIgualA(INTEIRO, REAL, CADEIA, CARACTERE)) {
      return new Expressao.Literal(anterior().line, anterior().literal, anterior());
    }

    if (isTokenTypeIgualA(ESQ_PARENTESES)) {
      return expParentizada();
    }
    throw error(espiar(), "Esperado expressao.");
  }

  /**
   * TIPO_DADO → "inteiro" | "real" | "cadeia" | "caractere" | "logico"
   * 
   * @return
   */
  private Token tipoDado() {
    if (isTokenTypeIgualA(TIPO_INTEIRO, TIPO_CADEIA, TIPO_CARACTERE, TIPO_LOGICO, TIPO_REAL,
        TIPO_MODULO)) {
      return anterior();
    }
    throw error(espiar(), "Tipo inválido.");
  }

  /**
   * se → "se" igualdade "entao" bloco ("senao" bloco)*
   * 
   * @return
   */
  private Declaracao seDeclaracao() {
    Expressao condicao = ou();
    Token inicio = consumirToken(ENTAO, "Esperado 'entao' depois da expressao.");
    consumirToken(ESQ_CHAVES, "Esperado '{' depois da expressao.");
    Declaracao.Bloco entaoBloco = new Declaracao.Bloco(anterior().line, bloco());
    Declaracao.Bloco senaoBloco = null;
    if (isTokenTypeIgualA(SENAO)) {
      consumirToken(ESQ_CHAVES, "Esperado '{' depois da expressao.");
      senaoBloco = new Declaracao.Bloco(anterior().line, bloco());
    }
    return new Declaracao.Se(inicio.line, condicao, entaoBloco, senaoBloco);
  }

  /**
   * enquanto → "enquanto" ou "faca" bloco
   * 
   * @return
   */
  private Declaracao enquantoDeclaracao() {
    Expressao condicao = ou();
    Token inicio = consumirToken(FACA, "Esperado 'faca' depois da expressao.");
    consumirToken(ESQ_CHAVES, "Esperado '{' depois da expressao.");
    Declaracao.Bloco corpo = new Declaracao.Bloco(anterior().line, bloco());
    return new Declaracao.Enquanto(inicio.line, condicao, corpo);
  }

  /**
   * para → "para" IDENTIFICADOR "de" INTEGER "ate" INTEGER "passo" INTEGER "faca" bloco
   * 
   * @return
   */
  private Declaracao paraDeclaracao() {

    Token identificador =
        consumirToken(IDENTIFICADOR, "Esperado 'identificador' depois da expressao.");
    consumirToken(DE, "Esperado 'de' depois da expressao.");
    Expressao de = adicao();
    int linhaOperador = consumirToken(ATE, "Esperado 'ate' depois da expressao.").line;
    Expressao ate = adicao();
    consumirToken(PASSO, "Esperado 'passo' depois da expressao.");
    Expressao passo = adicao();
    consumirToken(FACA, "Esperado 'faca' depois da expressao.");
    consumirToken(ESQ_CHAVES, "Esperado '{' depois da expressao.");
    Declaracao.Bloco corpo = new Declaracao.Bloco(identificador.line, bloco());

    Expressao.Variavel variavel = new Expressao.Variavel(identificador.line, identificador);

    // atribuicao
    Expressao.Atribuicao atribuicaoExpressao =
        new Expressao.Atribuicao(identificador.line, identificador, de);

    // condicao
    Expressao.Binario condicao = new Expressao.Binario(identificador.line, variavel,
        new Token(MENOR_IGUAL, "<=", null, linhaOperador), ate);

    // incremento
    Expressao.Binario operacaoIncremento = new Expressao.Binario(identificador.line, variavel,
        new Token(MAIS, "+", null, linhaOperador), passo);
    Expressao.Atribuicao incrementoExpressao =
        new Expressao.Atribuicao(identificador.line, identificador, operacaoIncremento);

    return new Declaracao.Para(identificador.line, atribuicaoExpressao, condicao,
        incrementoExpressao, corpo);
  }

  /**
   * repita → "repita" bloco "ate" ou ";"
   * 
   * @return
   */
  private Declaracao repitaDeclaracao() {
    Token inicio = anterior();
    consumirToken(ESQ_CHAVES, "Esperado '{' depois da expressao.");
    Declaracao.Bloco corpo = new Declaracao.Bloco(anterior().line, bloco());
    consumirToken(ATE, "Esperado 'ate' depois da expressão.");
    Expressao condicao = ou();
    consumirToken(PONTO_VIRGULA, "Esperado ';'");

    return new Declaracao.Repita(inicio.line, corpo, condicao);
  }

  /**
   * varArray → IDENTIFICADOR : "vetor" "[" INTEIRO ".." INTEIRO "]" "de" TIPO_DADO
   * 
   * @return
   */
  private Declaracao declaracaoVariavelArray(Token nome, Token intervaloI, Token intervaloF,
      Token tipo) {

    return new Declaracao.VariavelArray(nome.line, nome,
        new Expressao.Literal(nome.line, intervaloI.literal, nome),
        new Expressao.Literal(nome.line, intervaloF.literal, nome), tipo);
  }

  /**
   * declaracaoModulo → "modulo" IDENTIFICADOR bloco
   * 
   * @return
   */
  private Declaracao declaracaoModulo() {
    consumirToken(TIPO_MODULO, "Esperado 'modulo'");
    Token nome = consumirToken(IDENTIFICADOR, "Esperado nome do módulo");
    consumirToken(ESQ_CHAVES, "Esperado {");
    Declaracao.Bloco corpo = new Declaracao.Bloco(anterior().line, bloco());

    return new Declaracao.Modulo(nome.line, nome, corpo);
  }

  /**
   * chamadaModulo → IDENTIFICADOR ";"
   * 
   * @return
   */
  private Declaracao chamadaModulo(Token nome) {
    return new Declaracao.ChamadaModulo(nome.line, nome);
  }
}
