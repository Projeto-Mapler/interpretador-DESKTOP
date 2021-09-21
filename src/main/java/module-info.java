module interpretadorMACP {
	exports conversores;
	exports evento;
	exports debug;
	exports modelos.tree;
	exports analisador;
	exports log;
	exports interpretador;
	exports modelos.excecao;
	exports modelos;
	exports tool;

	requires JColor;
	requires cloning;
	requires gs.core;
	requires java.desktop;
	requires pherd;
	requires scala.library;
	
	opens modelos to cloning;
}