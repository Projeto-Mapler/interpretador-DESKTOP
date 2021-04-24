package log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import evento.EventoInterpretador;

public class LogService {
  private Set<EventoInterpretador> eventosObservados;
  private boolean ativo = false;
  private boolean colorido = false;
  
  private LogPrinter printer;
  
  public LogService() {
    this.eventosObservados = new HashSet<EventoInterpretador>();
    printer = new LogPrinter();
  }
  
  public String printLog(EventoInterpretador evento, Object payload) {
    if(!this.ativo) return null;
    if(!this.eventosObservados.contains(evento)) return null;
    return this.printer.criarMsgLog(payload, evento, this.colorido);
  }
  
  public void addEvento(EventoInterpretador evento) {
    this.eventosObservados.add(evento);
  }
  
  public void addEventos(List<EventoInterpretador> eventos) {
    this.eventosObservados.addAll(eventos);
  }
  
  public void removerEvento(EventoInterpretador evento) {
    this.eventosObservados.remove(evento);
  }
  
  public void removerEventos(List<EventoInterpretador> eventos) {
    this.eventosObservados.removeAll(eventos);
  }
  
  public void removerEventos() {
    this.eventosObservados.clear();
  }
  
  public boolean isAtivo() {
    return ativo;
  }
  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }

  public boolean isColorido() {
    return colorido;
  }

  public void setColorido(boolean colorido) {
    this.colorido = colorido;
  }
  
   
}
