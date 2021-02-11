package debug;

/**
 * Estados que o Debugador pode assumir.
 * 
 * @author Kerlyson
 *
 */
public enum EstadoDebug {
    /**
     * Debugador está executando o codigo
     */
    EXECUTANDO,
    /**
     * Debugador pausou a execução do código
     */
    PAUSADO,
    /**
     * Debugador conclui a interpretação ou foi interrompido
     */
    FINALIZADO,
    /**
     * Debugador está desativado, não interfere na execução do código
     */
    OFF,
    /**
     * Debugador está ativo, pode interferir na execução do código
     */
    ON
};