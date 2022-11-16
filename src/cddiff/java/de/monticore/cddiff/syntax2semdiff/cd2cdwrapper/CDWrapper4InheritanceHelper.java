package de.monticore.cddiff.syntax2semdiff.cd2cdwrapper;

import com.google.common.graph.MutableGraph;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapper;
import de.monticore.cddiff.syntax2semdiff.cd2cdwrapper.metamodel.CDTypeWrapperKind;
import java.util.*;
import java.util.stream.Collectors;

public class CDWrapper4InheritanceHelper {

  /** get all inheritance path for each top class by backtracking */
  public List<List<String>> getAllInheritancePath4CDTypeWrapper(
      CDTypeWrapper cDTypeWrapper, MutableGraph<String> inheritanceGraph) {
    String root = cDTypeWrapper.getName();
    List<List<String>> pathList = new ArrayList<>();
    getAllInheritancePath4CDTypeWrapperHelper(root, new LinkedList<>(), pathList, inheritanceGraph);
    return pathList;
  }

  /** backtracking helper */
  private void getAllInheritancePath4CDTypeWrapperHelper(
      String root,
      LinkedList<String> path,
      List<List<String>> pathList,
      MutableGraph<String> inheritanceGraph) {
    if (inheritanceGraph.successors(root).isEmpty()) {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      pathList.add(newPath);
      return;
    } else {
      LinkedList<String> newPath = new LinkedList<>(path);
      newPath.addFirst(root);
      for (String parentNode : inheritanceGraph.successors(root)) {
        getAllInheritancePath4CDTypeWrapperHelper(parentNode, newPath, pathList, inheritanceGraph);
      }
    }
  }

  /** getting all bottom class in inheritance graph */
  public static Set<String> getAllBottomCDTypeWrapperNode(MutableGraph<String> inheritanceGraph) {
    Set<String> result = new HashSet<>();
    inheritanceGraph
        .nodes()
        .forEach(
            s -> {
              if (inheritanceGraph.predecessors(s).isEmpty()) {
                result.add(s);
              }
            });
    return result;
  }

  /** getting inherited CDTypeWrapper name by given CDTypeWrapper name */
  public static LinkedHashSet<String> getInheritedClassSet(
      MutableGraph<String> inheritanceGraph, String cDTypeWrapperName) {
    LinkedHashSet<String> result = new LinkedHashSet<>();
    result.add(cDTypeWrapperName);
    Deque<String> currentCDTypeWrapperNameQueue = new LinkedList<>();
    currentCDTypeWrapperNameQueue.offer(cDTypeWrapperName);
    while (!currentCDTypeWrapperNameQueue.isEmpty()) {
      String currentNode = currentCDTypeWrapperNameQueue.poll();
      if (!inheritanceGraph.predecessors(currentNode).isEmpty()) {
        inheritanceGraph
            .predecessors(currentNode)
            .forEach(
                e -> {
                  result.add(e);
                  currentCDTypeWrapperNameQueue.offer(e);
                });
      }
    }
    return result;
  }

  /** getting super CDTypeWrapper name by given CDTypeWrapper name */
  public static LinkedHashSet<String> getSuperClassSet(
      MutableGraph<String> inheritanceGraph, String cDTypeWrapperName) {
    List<String> temp = new ArrayList<>();
    Deque<String> currentCDTypeWrapperNameQueue = new LinkedList<>();
    currentCDTypeWrapperNameQueue.offer(cDTypeWrapperName);
    temp.add(cDTypeWrapperName);
    while (!currentCDTypeWrapperNameQueue.isEmpty()) {
      String currentNode = currentCDTypeWrapperNameQueue.poll();
      if (!inheritanceGraph.successors(currentNode).isEmpty()) {
        inheritanceGraph
            .successors(currentNode)
            .forEach(
                e -> {
                  temp.add(e);
                  currentCDTypeWrapperNameQueue.offer(e);
                });
      }
    }
    Collections.reverse(temp);
    return new LinkedHashSet<>(temp);
  }

  /** getting all super- and sub-CDTypeWrapper name by given CDTypeWrapper name */
  public static LinkedHashSet<String> getAllSuperClassAndSubClassSet(
      MutableGraph<String> inheritanceGraph, String cDTypeWrapperName) {
    LinkedHashSet<String> result = new LinkedHashSet<>();
    result.addAll(getSuperClassSet(inheritanceGraph, cDTypeWrapperName));
    result.addAll(getInheritedClassSet(inheritanceGraph, cDTypeWrapperName));
    return result;
  }

  /**
   * return all simple super-classes about given CDTypeWrapper except abstract class and interface
   */
  public static List<CDTypeWrapper> getAllSimpleSuperClasses4CDTypeWrapper(
      CDTypeWrapper cDTypeWrapper, Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    List<CDTypeWrapper> result = new LinkedList<>();
    cDTypeWrapper
        .getSuperclasses()
        .forEach(
            e -> {
              if (cDTypeWrapperGroup.get(e).getCDWrapperKind()
                  == CDTypeWrapperKind.CDWRAPPER_CLASS) {
                result.add(cDTypeWrapperGroup.get(e));
              }
            });
    return result;
  }

  /** return all simple subclasses about given CDTypeWrapper except abstract class and interface */
  public static List<CDTypeWrapper> getAllSimpleSubClasses4CDTypeWrapper(
      CDTypeWrapper cDTypeWrapper, Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    List<CDTypeWrapper> result = new LinkedList<>();
    cDTypeWrapper
        .getSubclasses()
        .forEach(
            e -> {
              if (cDTypeWrapperGroup.get(e).getCDWrapperKind()
                  == CDTypeWrapperKind.CDWRAPPER_CLASS) {
                result.add(cDTypeWrapperGroup.get(e));
              }
            });
    return result;
  }

  /**
   * return all simple subclasses about given CDTypeWrapper with OPEN status except abstract class
   * and interface
   */
  public static List<CDTypeWrapper> getAllSimpleSubClasses4CDTypeWrapperWithStatusOpen(
      CDTypeWrapper cDTypeWrapper, Map<String, CDTypeWrapper> cDTypeWrapperGroup) {
    return getAllSimpleSubClasses4CDTypeWrapper(cDTypeWrapper, cDTypeWrapperGroup).stream()
        .filter(CDTypeWrapper::isOpen)
        .collect(Collectors.toList());
  }
}
