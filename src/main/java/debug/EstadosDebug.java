package debug;

/**
 * Estados que o Debugador pode assumir.
 * 
 * @author Kerlyson
 *
 */
public enum EstadosDebug {
    /**
     * Debugador está executando o codigo
     */
    EXECUTANDO,
    /**
     * Debugador pausou a execução do código
     */
    PAUSADO,
    /**
     * Debugador está desativado, não interfere na execução do código
     */
    DESATIVO,
    /**
     * Debugador está ativo, pode interferir na execução do código
     */
    ATIVO
};