package modelos;

/**
 * Simbolos e palavras válidas na linguagem
 * 
 * @author Kerlyson
 *
 */
public enum TiposToken {
  // tokens de um caractere
  ESQ_PARENTESES, DIR_PARENTESES, ESQ_CHAVES, DIR_CHAVES, VIRGULA, PONTO, MENOS, MAIS, PONTO_VIRGULA, BARRA, ASTERISCO, DOIS_PONTOS, ESQ_COLCHETE, DIR_COLCHETE,

  // tokens de ate dois caracteres
  DIFERENTE, IGUAL, MENOR_IGUAL, MAIOR_QUE, MAIOR_IQUAL, MENOR_QUE, ATRIBUICAO,

  // representa um nome de vaiavel ou nome de um modulo
  IDENTIFICADOR,

  // palavras-reservadas
  E, OU, NAO, FALSO, VERDADEIRO, SE, ENTAO, CASO, SENAO, FACA, ENQUANTO, PARA, DE, ATE, PASSO, ESCREVER, LER, VARIAVEIS, INICIO, FIM, INTERVALO, REPITA, CADEIA, REAL, INTEIRO, CARACTERE, VETOR,
  
  // representa o tipo de uma variavel ou valor no programa
  TIPO_INTEIRO, TIPO_REAL, TIPO_LOGICO, TIPO_CADEIA, TIPO_CARACTERE, TIPO_VETOR, TIPO_MODULO,
  
  // fim do arquivo
  EOF
}
