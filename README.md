# Interpretador

Baseado no trabalho no capítulo II do livro online *Crafting Interpreters*, foi possível desenvolver até o momento um interpretador para a linguagem que suporta:
- operações aritimeticas (+, -, *, / )
- operações lógicas (verdadeiro, falso, não, e, ou )
- declaração e utilização de variaveis (tipagem dinâmica)
- controle de fluxo com "se entao senao"
- laços de repetição com "enquanto" e "faca"
- blocos utilizam "{ }" em vez de  "[[ ]]"

# Como rodar

Executar classe Principal.java no pacote main.
Passar o caminho do arquivo de texto contendo o pseudocódigo no args[] do método main.

# Exemplo de código

```
variaveis
	x:inteiro;
inicio
	x<-1+2*3+(4/2);
	escrever x;
	
	se x = 9 e x > 8 e nao(x<=7) entao{
		escrever "dentro do se";
	} senao {
		escrever "dentro do senao";
	}
	
	para x de 1 ate 5 passo 1 faca {
		escrever x;
	}
fim
```

# Gramática
```

programa   → "variaveis" (declaracaoVariaveis)* "inicio" (declaracao)* "fim" EOF 

declaracaoVariaveis → IDENTIFICADOR ("," IDENTIFICADOR) ":" TIPO_DADO ";"
declaracao → expressaoDeclarativa | escrever | ler | bloco |se | enquanto | para
expressaoDeclarativa → expressao ";" 

bloco → "{" (declaracao)* "}" 
escrever → "escrever" expressao ";" 
ler → "ler" IDENTIFICADOR ";" (?)
se  → "se" ou "entao" bloco ("senao" bloco)*
enquanto → "enquanto" ou "faca" bloco
para → "para" IDENTIFICADOR "de" INTEGER "ate" INTEGER "passo" INTEGER "faca" bloco


expressao → atribuicao
atribuicao → (IDENTIFICADOR "<-" atribuicao) | ou
ou → e ("ou" e)*
e → igualdade ("e" igualdade)*
igualdade → comparacao ( ( "<>" | "=" ) comparacao)*
comparacao → adicao( ( ">" | ">=" | "<" | "<=" ) adicao)* 
adicao → multiplicacao ( ( "-" | "+" ) multiplicacao )* 
multiplicacao → unario ( ( "/" | "*" ) unario)* 
unario → ( "nao" | "-" ) unario | primario
primario → INTEIRO | REAL | CADEIA | CARACTERE | VERDADEIRO | FALSO | "(" expressao ")" | IDENTIFICADOR

TIPO_DADO → "inteiro" | "real" | "cadeia" | "caractere" | "logico" | "vetor" "[" INTEIRO "..." INTEIRO "]" "de" TIPO_DADO
```
## Fontes

[Ruslan's Blog](https://ruslanspivak.com/lsbasi-part1/ "Ruslan's Blog"):  Série de posts documentando o desenvolvimento de um interpretador Pascal em Python.

[Crafting Interpreters](https://www.craftinginterpreters.com/ "Crafting Interpreters"): Livro para construir um interpretador para um pseudolinguagem utilizando Java (Capítulo 2) e C (Capítulo 3).

