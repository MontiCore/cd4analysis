/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.matchresult;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.cdmerge.util.CDUtils;
import java.util.*;
import java.util.function.Predicate;

/**
 * Stores a Match for similar, matching Elements with their respective parent nodes such as class
 * diagrams or classes. Its internal structure is a Graph with bidirectional match-edgdes. As the
 * Graph is most likely not completely connected, all nodes can be accessed by their parents
 */
public class ASTMatchGraph<E extends ASTNode, P extends ASTNode> {

  private List<P> parents;

  private Map<P, List<MatchNode<E, P>>> matches;

  public ASTMatchGraph(Collection<P> parents) {
    this.parents = new ArrayList<P>(parents);
    this.matches = new HashMap<P, List<MatchNode<E, P>>>();
    for (P parent : parents) {
      matches.put(parent, new ArrayList<MatchNode<E, P>>());
    }
  }

  private int getIndexForParent(P parent) {
    for (int i = 0; i < parents.size(); i++) {
      // We deliberately check equals only via object id
      if (parents.get(i) == parent) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Adds a Node {@link MatchNode} to this MatchResult containing the specified ModelElement. No new
   * node is created if this MatchResult already contains a node for the specified element. Thus,
   * this method is idempotent.
   *
   * @param parent The parent (e.g. a ClassDiagramm or Class for this model element
   * @param elemens the model element
   * @return the node containing this element
   */
  public MatchNode<E, P> addElement(E element, P parent) {
    return this.addElement(element, parent, Optional.empty());
  }

  /**
   * Adds a Node {@link MatchNode} to this MatchResult containing the specified ModelElement. No new
   * node is created if this MatchResult already contains a node for the specified element. Thus,
   * this method is idempotent.
   *
   * @param parent The parent (e.g. a ClassDiagramm or Class for this model element
   * @param elemens the model element
   * @return the node containing this element
   */
  public MatchNode<E, P> addElement(E element, P parent, Optional<String> cdPackage) {
    Preconditions.checkNotNull(element);
    Preconditions.checkNotNull(parent);
    int parentIndex = getIndexForParent(parent);
    if (parentIndex < 0) {
      throw new IllegalArgumentException("No such parent " + parent);
    }
    if (!this.matches.containsKey(parent)) {
      this.matches.put(parent, new ArrayList<MatchNode<E, P>>());
    }
    for (MatchNode<E, P> node : this.matches.get(parent)) {
      if (node.getElement() == element) {
        // We already have a node for this element
        return node;
      }
    }
    MatchNode<E, P> node = new MatchNode<E, P>(element, parent, cdPackage);
    this.matches.get(parent).add(node);
    return node;
  }

  /**
   * Returns the node containing the specified Element or Optional.empty() if no Node with this
   * element was found
   *
   * @param element - the element to search for
   * @param parent - the element's parent
   * @return either the node (Boxed in an Optional) or Optional.empty() if no Node was found
   */
  public Optional<MatchNode<E, P>> getNode(E element, P parent) {
    Preconditions.checkNotNull(element);
    Preconditions.checkNotNull(parent);
    int parentIndex = getIndexForParent(parent);
    if (parentIndex < 0) {
      throw new IllegalArgumentException("No such parent " + parent);
    }
    if (this.matches.containsKey(parent)) {
      for (MatchNode<E, P> node : this.matches.get(parent)) {
        if (node.getElement() == element) {
          return Optional.of(node);
        }
      }
    }
    return Optional.empty();
  }

  /** @return the sequence of Parents for this match result */
  public ImmutableList<P> getParents() {
    return ImmutableList.copyOf(this.parents);
  }

  /**
   * Returns the matching nodes for the specified parent node (thus a vertical slice through this
   * match-result graph) or Optional.empty() if the match could not be resolved.
   *
   * @param p as stored in this MatchResult
   * @return the matching nodes for the specified parent node and the matchIdentifier or
   *     Optional.empty() if the match could not be resolved
   */
  public List<MatchNode<E, P>> getAllNodesForParent(P parent) {

    int idx = getIndexForParent(parent);
    if (idx < 0) {
      throw new IllegalArgumentException("No such parent " + parent);
    }
    return this.matches.get(parent);
  }

  /** Returns an iterator that allows to iterate over all matchings nodes */
  public Iterator<List<MatchNode<E, P>>> getMatchNodeIterator() {
    return this.matches.values().iterator();
  }

  /** Returns an iterator that allows to iterate over all matchings nodes */
  public Iterator<MatchNode<E, P>> getMatchNodeIterator(P parent) {
    int idx = getIndexForParent(parent);
    if (idx < 0) {
      throw new IllegalArgumentException("No such parent " + parent);
    }
    return this.matches.get(parent).iterator();
  }

  /**
   * Computes the parent of a match
   *
   * @param match Describes the match to be searched
   * @return A parent or an Optional.empty() if the match is not present
   */
  public Optional<P> getParent(Predicate<P> match) {
    for (P parent : this.parents) {
      if (match.test(parent)) {
        return Optional.of(parent);
      }
    }
    return Optional.empty();
  }

  /**
   * Computes the elements of a match
   *
   * @param match Describes the match to be searched
   * @return A list of elements satisfying the match predicate
   */
  public List<E> findElements(Predicate<E> match) {
    List<E> matches = new ArrayList<E>();
    for (List<MatchNode<E, P>> nodes : this.matches.values()) {
      for (MatchNode<E, P> node : nodes) {
        if (match.test(node.getElement())) {
          matches.add(node.getElement());
        }
      }
    }
    return matches;
  }

  /**
   * Computes the nodes of a match
   *
   * @param match Describes the match to be searched
   * @return A set of nodes satisfying the match predicate
   */
  public List<MatchNode<E, P>> findNodes(Predicate<MatchNode<E, P>> match) {
    List<MatchNode<E, P>> matchedNodes = new ArrayList<MatchNode<E, P>>();
    for (List<MatchNode<E, P>> nodes : this.matches.values()) {
      for (MatchNode<E, P> node : nodes) {
        if (match.test(node) && !matchedNodes.contains(node)) {
          matchedNodes.add(node);
        }
      }
    }
    return matchedNodes;
  }

  /**
   * Checks if the parent of a match is present
   *
   * @param match Describes the match to be searched
   * @return If a match is present
   */
  public boolean hasParent(Predicate<P> match) {
    return getParent(match).isPresent();
  }

  /**
   * Checks if a given parent is present
   *
   * @param parent The parent to be searched
   * @return True, if the parent is contained in the MatchResult
   */
  public boolean hasParent(P parent) {
    return getIndexForParent(parent) >= 0;
  }

  @Override
  public String toString() {
    Set<MatchNode<E, P>> visitedNodes = new HashSet<MatchNode<E, P>>();
    StringBuilder sb = new StringBuilder();
    sb.append("MatchGraph for");
    this.parents.forEach(parent -> sb.append(" [" + CDUtils.getName(parent) + "]"));
    sb.append("\n");
    for (P parent : this.parents) {
      for (MatchNode<E, P> node : this.matches.get(parent)) {
        if (!visitedNodes.contains(node)) {
          visitedNodes.add(node);
          sb.append("[" + CDUtils.getName(parent) + "] " + CDUtils.getName(node.getElement()));
          for (MatchNode<E, P> matchNode : node.getMatchedNodes()) {
            visitedNodes.add(matchNode);
            sb.append(
                " -> ["
                    + CDUtils.getName(matchNode.getParent())
                    + "] "
                    + CDUtils.getName(matchNode.getElement()));
          }
          sb.append("\n");
        }
      }
      sb.append("\n");
    }
    if (visitedNodes.size() == 0) {
      return "";
    }
    return sb.toString();
  }
}
