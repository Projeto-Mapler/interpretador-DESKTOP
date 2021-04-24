package log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import debug.DebugSnapshot;
import debug.EstadoDebug;
import evento.EventoInterpretador;
import modelos.tree.AstDebugNode;

public class LogPrinter {

  private Map<EventoInterpretador, AnsiFormat> eventoTagMap;

  public LogPrinter() {
    eventoTagMap = new HashMap<EventoInterpretador, AnsiFormat>();
    eventoTagMap.put(EventoInterpretador.DEBUG_MUDANCA_ESTADO, new AnsiFormat(Attribute.CYAN_TEXT()));
    eventoTagMap.put(EventoInterpretador.DEBUG_PASSO_EXECUTADO, new AnsiFormat(Attribute.CYAN_TEXT()));
    eventoTagMap.put(EventoInterpretador.VISITA_NODE_AST, new AnsiFormat(Attribute.CYAN_TEXT()));
    eventoTagMap.put(EventoInterpretador.ERRO, new AnsiFormat(Attribute.BRIGHT_RED_TEXT()));
    eventoTagMap.put(EventoInterpretador.INPUT, new AnsiFormat(Attribute.GREEN_TEXT()));
    eventoTagMap.put(EventoInterpretador.OUTPUT, new AnsiFormat(Attribute.GREEN_TEXT()));
    eventoTagMap.put(EventoInterpretador.INTERPRETACAO_CONCLUIDA, new AnsiFormat(Attribute.BLUE_TEXT()));
    eventoTagMap.put(EventoInterpretador.INTERPRETACAO_INTERROMPIDA, new AnsiFormat(Attribute.BRIGHT_RED_TEXT()));
  }

  public String criarMsgLog(Object payload, EventoInterpretador evento, boolean colorido) {
    String msgLog = getPayloadString(payload, evento);
    if (colorido)
      return criaMsgLogComCor(msgLog, evento);
    return criaMsgLogSemCor(msgLog, evento);
  }

  private String criaMsgLogComCor(String msgLog, EventoInterpretador evento) {
    AnsiFormat ansi = this.eventoTagMap.get(evento);
    return "[LOG]" + ansi.format("[" + evento.toString() + "]") + msgLog;
  }

  private String criaMsgLogSemCor(String msgLog, EventoInterpretador evento) {
    return "[LOG][" + evento.toString() + "]" + msgLog;
  }

  private String getPayloadString(Object payload, EventoInterpretador evento) {
    if (payload == null) {
      return null;
    }
    switch (evento) {
      case DEBUG_MUDANCA_ESTADO:
        return (((EstadoDebug) payload).toString());
      case DEBUG_PASSO_EXECUTADO:
        return (((DebugSnapshot) payload).toString());
      case ERRO:

        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        ((RuntimeException) payload).printStackTrace(pw);
        return sw.getBuffer().toString();

      case INPUT:
        return (((String) payload));
      case INTERPRETACAO_CONCLUIDA:
        return (((Double) payload)).toString();
      case INTERPRETACAO_INTERROMPIDA:
        return (((Double) payload)).toString();
      case OUTPUT:
        return (((String) payload));
      case VISITA_NODE_AST:
        return (((AstDebugNode) payload).toString());
      default:
        return null;
    }
  }


}
