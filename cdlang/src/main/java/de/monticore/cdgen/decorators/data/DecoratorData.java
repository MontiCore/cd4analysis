/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdgen.decorators.data;

import com.google.common.collect.Iterables;
import de.monticore.ast.ASTNode;
import de.monticore.cd.codegen.CDGenService;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdgen.MatchResult;
import de.monticore.cdgen.creators.CopyCreator;
import de.monticore.cdgen.decorators.IDecorator;
import de.monticore.cdgen.decorators.matcher.MatcherData;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.TagRepository;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.visitor.IVisitor;

import java.lang.ref.WeakReference;
import java.util.*;

public class DecoratorData {

  public Map<Class<? extends IDecorator<?>>, Object> decoratorDataMap = new HashMap<>();
  public Map<FieldSymbol, CDRoleSymbol> fieldToRoles; // Assoc -> Field
  protected Map<Class<? extends IDecorator<?>>, MatcherData> matchers = new HashMap<>();

  protected String[][] cliConfig;

  protected CDGenService cdGenService = new CDGenService();

  /**
   * We keep a map of child -> parent relations for when we look-up the state of
   */
  protected WeakHashMap<ASTNode, WeakReference<ASTNode>> parents = new WeakHashMap<>();

  /**
   * A Cache (AST, Decorator) -> MatchResult
   */
  protected WeakHashMap<ASTNode, IdentityHashMap<MatcherData, MatchResult>> cache = new WeakHashMap<>();

  protected SimpleSymbolTagger tagger = new SimpleSymbolTagger(this::_getTaggingUnits);
  protected ASTTagUnit internalTagUnit;
  public CopyCreator.Created created;

  public DecoratorData() {
    this.internalTagUnit = TagsMill.tagUnitBuilder().setName("__cd_decorator_internak").build();
  }

  protected Iterable<ASTTagUnit> _getTaggingUnits() {
    // TODO: Limit tags?
    return Iterables.concat(Collections.singleton(internalTagUnit), TagRepository.getLoadedTagUnits());
  }

  public void simpleTag(ISymbol symbol, String name) {
    this.tagger.addTag(symbol, TagsMill.simpleTagBuilder().setName(name).build());
  }

  public MatcherData getOrCreateMatcherData(Class<? extends IDecorator<?>> clazz) {
    return this.matchers.computeIfAbsent(clazz, aClass -> new MatcherData());
  }

  public <D> D getDecoratorData(Class<? extends IDecorator<D>> decorator) {
    return (D) this.decoratorDataMap.get(decorator);
  }

  public void setupParents(CopyCreator.Created created, String[][] cliConfig) {
    parents.clear();
    cache.clear();
    this.cliConfig = cliConfig;
    var t = CD4CodeMill.inheritanceTraverser();
    t.add4IVisitor(new IVisitor() {
      final Stack<ASTNode> nodeStack = new Stack<>();

      @Override
      public void visit(ASTNode node) {
        if (!nodeStack.isEmpty())
          parents.put(node, new WeakReference<>(nodeStack.peek()));
        nodeStack.push(node);
      }

      @Override
      public void endVisit(ASTNode node) {
        nodeStack.pop();
      }
    });
    created.getOriginal().accept(t);
    this.created = created;
  }

  public Optional<ASTNode> getParent(ASTNode p) {
    return this.parents.containsKey(p) ? Optional.ofNullable(parents.get(p).get()) : Optional.empty();
  }

  public boolean shouldDecorate(Class<? extends IDecorator> decorator, ASTNode node) {
    MatcherData matcherData = matchers.get(decorator);
    if (matcherData == null) {
      return false;
    }
    return shouldDecorate(matcherData, node) == MatchResult.APPLY;
  }

  protected MatchResult shouldDecorate(MatcherData matcherData, ASTNode node) {
    return this.cache.computeIfAbsent(node, (astNode) -> new IdentityHashMap<>()).computeIfAbsent(matcherData, (matcherData1) -> shouldDecorateCacheMiss(matcherData1, node));
  }


  protected MatchResult shouldDecorateCacheMiss(MatcherData matcherData, ASTNode node) {
    MatchResult result = MatchResult.DEFAULT;
    if (node instanceof ASTCDClass) {
      result = matchClass((ASTCDClass) node, matcherData);
    } else if (node instanceof ASTCDAttribute) {
      result = matchCDAttribute((ASTCDAttribute) node, matcherData);
    } else if (node instanceof ASTCDDefinition) {
      System.err.println("TODO ASTCDDefinition ");
    } else if (node instanceof ASTCDCompilationUnit) {
      System.err.println("TODO ASTCDCompilationUnit ");
    } else {
      throw new IllegalStateException("Unhandled TODO " + node.getClass().getName());
    }

    if (result != MatchResult.DEFAULT) return result;

    // No decision could be made for this node => check for its parent
    var parent = this.parents.get(node);
    if (parent != null) {
      return shouldDecorate(matcherData, parent.get());
    }

    return matcherData.getGlobalDefault();
  }


  protected MatchResult matchClass(ASTCDClass node, MatcherData matcherData) {
    if (node.getModifier().isPresentStereotype()) {
      for (var s : node.getModifier().getStereotype().getValuesList()) {
        var r = matchStereo(s, matcherData);
        if (r != MatchResult.DEFAULT) return r;
      }
    }

    if (node.isPresentSymbol()) {
      var r = matchCLI(node.getSymbol(), matcherData);
      if (r != MatchResult.DEFAULT) return r;
      r = matchTags(node.getSymbol(), matcherData);
      if (r != MatchResult.DEFAULT) return r;
    }

    // TODO: more
    return MatchResult.DEFAULT;
  }

  protected MatchResult matchCDAttribute(ASTCDAttribute node, MatcherData matcherData) {
    if (node.getModifier().isPresentStereotype()) {
      for (var s : node.getModifier().getStereotype().getValuesList()) {
        var r = matchStereo(s, matcherData);
        if (r != MatchResult.DEFAULT) return r;
      }
    }

    if (node.isPresentSymbol()) {
      var r = matchCLI(node.getSymbol(), matcherData);
      if (r != MatchResult.DEFAULT) return r;
      r = matchTags(node.getSymbol(), matcherData);
      if (r != MatchResult.DEFAULT) return r;
    }

    // TODO: more
    return MatchResult.DEFAULT;
  }

  protected MatchResult matchStereo(ASTStereoValue value, MatcherData matcherData) {
    for (var m : matcherData.getStereoMatchers()) {
      var r = m.match(value);
      if (r != MatchResult.DEFAULT) return r;
    }
    return MatchResult.DEFAULT;
  }

  protected MatchResult matchTags(ISymbol symbol, MatcherData matcherData) {
    var tags = tagger.getTags(symbol);
    for (var m : matcherData.getTagMatchers()) {
      for (var tag : tags) {
        var r = m.match(tag);
        if (r != MatchResult.DEFAULT) return r;
      }
    }
    return MatchResult.DEFAULT;
  }

  protected MatchResult matchCLI(ISymbol symbol, MatcherData matcherData) {
    for (var cliOption : this.cliConfig) {
      if (symbol.getFullName().equals(cliOption[0])) {
        for (var m : matcherData.getCLIMatchers()) {
          var r = m.match(cliOption[1], cliOption[2]);
          if (r != MatchResult.DEFAULT) return r;
        }
      }
    }
    return MatchResult.DEFAULT;
  }

  public <T extends ASTNode> T getAsDecorated(T originalClazz) {
    return (T) created.getOriginalToDecoratedMap().get(originalClazz);
  }
}
