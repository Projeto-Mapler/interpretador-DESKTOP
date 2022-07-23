package tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import de.elnarion.util.plantuml.generator.PlantUMLClassDiagramGenerator;

public class PlantUmlGenerator {
  public static void main(String[] args) throws ClassNotFoundException, IOException {

    // configurações de layout
    StringBuilder configBuilder = new StringBuilder();
    configBuilder.append("left to right direction\n");
    configBuilder.append("skinparam linetype ortho\n");
    configBuilder.append("skinparam ParticipantPadding 10\n");
    configBuilder.append("skinparam BoxPadding 10\n");
    configBuilder.append("skinparam Nodesep 100\n");
    configBuilder.append("skinparam Ranksep 100\n");

    // pacotes que dever ser adicionados no diagrama
    List<String> scanpackages = new ArrayList<>();
//    scanpackages.add("analisador");
//    scanpackages.add("conversores");
//    scanpackages.add("debug");
//    scanpackages.add("evento");
    scanpackages.add("interpretador");
//    scanpackages.add("log");
//    scanpackages.add("modelos");
//    scanpackages.add("tool");

    // gerando declarações
    List<String> hideClasses = new ArrayList<>();
    PlantUMLClassDiagramGenerator generator = new PlantUMLClassDiagramGenerator(
        PlantUmlGenerator.class.getClassLoader(), scanpackages, null, hideClasses, false, true);

    String result = generator.generateDiagramText();
    // adicionando configurações
    String[] resultSplit = result.split("@startuml");
    result = "@startuml\n\n" + configBuilder.toString() + resultSplit[1];
    // string gerada
    System.out.println(result);
  }
}
