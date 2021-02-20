package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Gera todas as classes node do programa
 * @author Kerlyson
 *
 */
class GeradorAST {

	public static void main(String[] args) throws IOException {
		String path = "src/main/java";
		// if (args.length != 1) {
		// System.err.println("Usage: generate_ast <output directory>");
		// System.exit(1);
		// }
		// String outputDir = args[0];
		defineAst(path, "Expressao", Arrays.asList(
				"Binario   			: Expressao esquerda, Token operador, Expressao direita",
				"Grupo     			: Expressao expressao",
				"ExpParentizada 	: Grupo grupo", 
				"Literal  			: Object valor, Token token",
				"Logico  			: Expressao esquerda, Token operador, Expressao direita",
				"Unario    			: Token operador, Expressao direira",
				"Atribuicao			: Token nome, Expressao valor",
				"AtribuicaoArray	: Token nome, Expressao index, Expressao valor",
				"VariavelArray		: Token nome, Expressao index", 
				"Variavel 			: Token nome"));
		defineAst(path, "Declaracao", Arrays.asList(
				"Bloco 				: List<Declaracao> declaracoes",
				"Expressao 			: tree.Expressao expressao", 
				"Escreva      		: List<tree.Expressao> expressoes",
				"Se        			: tree.Expressao condicao, Bloco entaoBloco, Bloco senaoBloco",
				"Ler      			: tree.Expressao atribuicao",
				"Var      			: Token nome, Token tipo",
				"VarDeclaracoes 	: List<Declaracao> variaveis",
				"VariavelArray 		: Token nome, tree.Expressao intervaloI, tree.Expressao intervaloF, Token tipo",
				"Para         		: tree.Expressao atribuicao, tree.Expressao condicao, tree.Expressao incremento, Bloco facaBloco",
				"Enquanto      		: tree.Expressao condicao, Bloco corpo", 
				"Repita				: Bloco corpo, tree.Expressao condicao",
				"Modulo				: Token nome, Bloco corpo", 
				"ChamadaModulo		: Token identificador",
				"Programa			: List<Declaracao> variaveis, List<Declaracao> corpo, List<Declaracao> modulos"));

	}

	private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
		String path = outputDir + "modelos/tree/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");

		writer.println("package modelos.tree;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("import modelos.Token;");
		writer.println();
		writer.println("public abstract class " + baseName + " extends AstDebugNode {");

		// Constructor.
		writer.println("    public " + baseName + "( int linha) {");
		writer.println("super(linha);");
		writer.println("}");
		
		defineVisitor(writer, baseName, types);

		// The AST classes.
		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			defineType(writer, baseName, className, fields);
		}
		// The base accept() method.
		writer.println();
		writer.println("  public abstract <R> R accept(Visitor<R> visitor);");

		writer.println("}");
		writer.close();
		System.out.println("Classe " + baseName + " definida");
	}

	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		writer.println("public interface Visitor<R> {");

		for (String type : types) {
			String typeName = type.split(":")[0].trim();
			writer.println(
					"public R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}

		writer.println("  }");
		System.out.println("Interface Visitor definida");
	}

	private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
		writer.println("public static class " + className + " extends " + baseName + " {");
		
		String campos = fieldList.length() > 0 ? ", "+fieldList : "";
		// Constructor.
		writer.println("    public " + className + "( int linha" + campos + ") {");
		writer.println("super(linha);");

		// Store parameters in fields.
		String[] fields = fieldList.split(", ");
		for (String field : fields) {
			String name = field.split(" ")[1];
			writer.println("      this." + name + " = " + name + ";");
		}

		writer.println("    }");

		// Visitor pattern.
		writer.println();
		writer.println("    public <R> R accept(Visitor<R> visitor) {");
		writer.println("      return visitor.visit" + className + baseName + "(this);");
		writer.println("    }");

		// Fields.
		writer.println();
		for (String field : fields) {
			writer.println("    public final " + field + ";");
		}

		writer.println("  }");
		System.out.println("SubClasse " + className + " definida");
	}
}
