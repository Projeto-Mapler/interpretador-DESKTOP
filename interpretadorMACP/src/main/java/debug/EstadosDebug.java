package debug;

public enum EstadosDebug {
	EXECUTANDO, // executando o codigo
	PAUSADO, // parado em algum breakpoint
	DESATIVO, // codigo deve ser executado sem debug
	ATIVO // codigo deve ser executado com debug
	};