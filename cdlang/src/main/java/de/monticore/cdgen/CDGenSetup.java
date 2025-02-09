/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdgen.creators.CopyCreator;
import de.monticore.cdgen.decorators.data.DecoratorData;
import de.monticore.cdgen.decorators.IDecorator;
import de.monticore.cdgen.decorators.matcher.ICLIMatcher;
import de.monticore.cdgen.decorators.matcher.IStereoMatcher;
import de.monticore.cdgen.decorators.matcher.ITagMatcher;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.ObjectFactory;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.regex.Pattern;

public class CDGenSetup {

  protected DecoratorData decoratorData = new DecoratorData();
  protected String[][] cliConfig = new String[][]{};

  protected final Collection<IDecorator<?>> decorators = new ArrayList<>();

  public void withDecorator(IDecorator<?> decorator) {
    this.decorators.add(decorator);
  }

  public ChainableGenSetup withDecorator(String className) {
    IDecorator<?> newObj = (IDecorator<?>) ObjectFactory.createObject(className);
    this.withDecorator(newObj);
    return new ChainableGenSetup((Class<? extends IDecorator<?>>) newObj.getClass());
  }

  /**
   * This class is a chainable setup helper for the {@link #withDecorator(String)}
   * It is used by the config templates
   */
  @SuppressWarnings("unused")
  public class ChainableGenSetup {
    Class<? extends IDecorator<?>> dec;

    ChainableGenSetup(Class<? extends IDecorator<?>> dec) {
      this.dec = dec;
    }

    public ChainableGenSetup applyOnName(String name) {
      configApplyMatchName(this.dec, name);
      return this;
    }

    public ChainableGenSetup ignoreOnName(String name) {
      configIgnoreMatchName(this.dec, name);
      return this;
    }

    public ChainableGenSetup rootDefault(MatchResult matchResult) {
      configDefault(this.dec, matchResult);
      return this;
    }

    public ChainableGenSetup rootDefaultApply() {
      return this.rootDefault(MatchResult.APPLY);
    }

    public ChainableGenSetup rootDefaultIgnore() {
      return this.rootDefault(MatchResult.IGNORE);
    }
  }

  public void configStereo(Class<? extends IDecorator<?>> dec, IStereoMatcher stereoMatcher) {
    this.decoratorData.getOrCreateMatcherData(dec).getStereoMatchers()
      .add(stereoMatcher);
  }

  public void configTag(Class<? extends IDecorator<?>> dec, ITagMatcher tagMatcher) {
    this.decoratorData.getOrCreateMatcherData(dec).getTagMatchers()
      .add(tagMatcher);
  }

  public void configCLI(Class<? extends IDecorator<?>> dec, ICLIMatcher cliMatcher) {
    this.decoratorData.getOrCreateMatcherData(dec).getCLIMatchers()
      .add(cliMatcher);
  }

  public void configDefault(Class<? extends IDecorator<?>> dec, MatchResult def) {
    this.decoratorData.getOrCreateMatcherData(dec).setGlobalDefault(def);
  }

  public void configApplyMatchName(Class<? extends IDecorator<?>> dec, String name) {
    this.configStereo(dec, IStereoMatcher.applyName(name));
    this.configTag(dec, ITagMatcher.applyName(name));
    this.configCLI(dec, ICLIMatcher.applyName(name));
  }

  public void configIgnoreMatchName(Class<? extends IDecorator<?>> dec, String name) {
    this.configStereo(dec, IStereoMatcher.ignoreName(name));
    this.configTag(dec, ITagMatcher.ignoreName(name));
    this.configCLI(dec, ICLIMatcher.ignoreName(name));
  }

  public void withCLIConfig(List<String> options) {
    var pattern = Pattern.compile("([a-zA-Z0-9_.]+):([a-zA-Z0-9_.]+)(=[a-zA-Z0-9_.]+)?");
    List<String[]> r = new ArrayList<>();
    for (String o : options) {
      var m = pattern.matcher(o);
      if (m.matches()) {
        r.add(new String[]{m.group(1), m.group(2), m.group(3)});
        System.err.println("with " + o);
      } else {
        Log.error("CLI Option " + o + " failed to setup");
      }
    }
    this.cliConfig = r.toArray(new String[0][3]);
  }

  List<DecoratorPhase> createPhases() {
    // Perform some topological sorting: Adapted Kahn's algorithm
    Map<IDecorator<?>, Integer> inDegrees = new HashMap<>();
    Map<IDecorator<?>, List<IDecorator<?>>> graph = new HashMap<>();

    // 1: Initialize DAG nodes
    for (IDecorator<?> node : this.decorators) {
      inDegrees.put(node, 0);
      graph.putIfAbsent(node, new ArrayList<>());
    }
    // Initialize edges (in the reverse order)
    for (IDecorator<?> node : this.decorators) {
      for (Class<? extends IDecorator<?>> depOn : node.getMustRunAfter()) {
        for (IDecorator<?> depNodeCandidate : this.decorators) {
          if (depNodeCandidate.getClass() == depOn) {
            graph.get(depNodeCandidate).add(node);
            inDegrees.put(node, inDegrees.getOrDefault(node, 0) + 1);
          }
        }
      }
    }
    // Process nodes with zero dependencies
    Queue<IDecorator<?>> queue = new LinkedList<>();
    for (IDecorator<?> node : inDegrees.keySet()) {
      if (inDegrees.get(node) == 0) {
        queue.offer(node);
      }
    }

    List<DecoratorPhase> phases = new ArrayList<>();

    while (!queue.isEmpty()) {
      DecoratorPhase phase = new DecoratorPhase();
      int size = queue.size();
      for (int i = 0; i < size; i++) {
        IDecorator<?> node = queue.poll();
        phase.decorators.add(node);
        for (IDecorator<?> n : graph.get(node)) {
          inDegrees.put(n, inDegrees.getOrDefault(n, 0) - 1);
          if (inDegrees.get(n) == 0) {
            queue.offer(n);
          }
        }
      }
      phases.add(phase);
    }

    return phases;
  }


  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit root, Map<FieldSymbol, CDRoleSymbol> fieldToRoles, Optional<GlobalExtensionManagement> glexOpt) {
    // Start by ordering the phases
    List<DecoratorPhase> phases = createPhases();

    // Then create the target CD
    CopyCreator creator = new CopyCreator();
    var created = creator.createFrom(root);

    // Create the parent-child tree relationship
    decoratorData.setupParents(created, cliConfig);
    decoratorData.fieldToRoles = fieldToRoles;

    // Some safeguard: "hash" the original AST (by pretty printing it)
    // We will then re-hash it after every phase to check if a phase has modified it
    String initialAsString = CD4CodeMill.prettyPrint(root, true);
    for (DecoratorPhase phase : phases) {
      final CD4CodeTraverser traverser = CD4CodeMill.inheritanceTraverser();
      // Add all decorators of this phase to a (new) inheritance traverser
      phase.decorators.forEach(d -> d.addToTraverser(traverser));
      // initialize the decorators
      phase.decorators.forEach(d -> d.init(decoratorData, glexOpt));
      // and traverse the (original) CD
      root.accept(traverser);
      // Post-checkup: Check that the pretty printed original CD has not changed
      String afterAsString = CD4CodeMill.prettyPrint(root, true);
      if (!initialAsString.equals(afterAsString)) {
        Log.error("0xTODO: A Decorator of phase " + phase.decorators + " has modified the original CD instead of the decorated CD");
      }
    }

    return created.getDecorated();
  }

  /**
   * The decoration occurs in phases.
   * During each phase the original AST is traversed and decorators
   * get the chance to decorate the target CD
   */
  static class DecoratorPhase {
    final List<IDecorator<?>> decorators = new ArrayList<>();
  }

}
