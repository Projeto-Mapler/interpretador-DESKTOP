# Interpretador

Baseado no trabalho no capítulo II do livro online *Crafting Interpreters*, foi possível desenvolver até o momento um interpretador para a linguagem que suporta:
- operações aritimeticas e logicas
- declaração e utilização de variaveis (tipagem dinamica)
- controle de fluxo com "se entao"
- laços de repetição com "enquanto"


#Gramática

- programa   → "variaveis" (declaracaoVariaveis)* "inicio" (declaracao)* "fim" EOF 
- declaracaoVariaveis → IDENTIFICADOR ("," IDENTIFICADOR) ":" TIPO_DADO ";"
- declaracao → expressaoDeclarativa | escrever | ler | bloco |se | enquanto | para
- expressaoDeclarativa → expressao ";" 
- escrever → "escrever" expressao ";" 
- ler → "ler" IDENTIFICADOR ";" 
- se  → "se" ou "entao" bloco ("senao" bloco)*
- enquanto → "enquanto" ou "faca" bloco
- para → "para" IDENTIFICADOR "de" INTEGER "ate" INTEGER "passo" INTEGER "faca" bloco
- bloco → "{" (declaracao)* "}" 
- expressao → atribuicao
- atribuicao → (IDENTIFICADOR "<-" atribuicao) | ou
- ou → e ("ou" e)*
- e → igualdade ("e" igualdade)*
- igualdade → comparacao ( ( "<>" | "=" ) comparacao)*
- comparacao → adicao( ( ">" | ">=" | "<" | "<=" ) adicao)* 
- adicao → multiplication ( ( "-" | "+" ) multiplication )* 
- multiplicacao → unario ( ( "/" | "*" ) unario)* 
- unario → ( "nao" | "-" ) unario | primario
- primario → INTEIRO | REAL | CADEIA | CARACTERE | VERDADEIRO | FALSO | "(" expressao ")" | IDENTIFICADOR
- TIPO_DADO → "inteiro" | "real" | "cadeia" | "caractere" | "logico" | "vetor[" INTEIRO "..." INTEIRO "] de" TIPO_DADO

## Fontes

[Ruslan's Blog](https://ruslanspivak.com/lsbasi-part1/ "Ruslan's Blog"):  Série de posts documentando o desenvolvimento de um interpretador Pascal em Python.

[Crafting Interpreters](https://www.craftinginterpreters.com/ "Crafting Interpreters"): Livro para construir um interpretador para um pseudolinguagem utilizando Java (Capítulo 2) e C (Capítulo 3).

