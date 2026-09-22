/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis.trafo;

import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.se_rwth.commons.Joiners;

import java.util.*;

/**
 * Warning: This trafo is useful just-before generation and might create a CD that fails CoCos due
 * to moved qualified types referred to (sub-) packages
 */
public class CDBasisDefaultPackageTrafo implements CDBasisVisitor2 {
  
  // For constructing the fqn of the default package
  protected List<String> artifactPackageParts;
  protected boolean doRedirectTypeReferences;
  
  //
  protected Set<ASTMCQualifiedName> renamedItems = new HashSet<>();
  protected int prefixLength;
  
  /**
   * In case the resulting CD must be "valid" w.r.t. its symbol references,
   * doRedirectTypeReferences may be required (exp. for sub-packages)
   *
   * @param doRedirectTypeReferences whether to redirect moved types
   */
  public CDBasisDefaultPackageTrafo(boolean doRedirectTypeReferences) {
    this.doRedirectTypeReferences = doRedirectTypeReferences;
  }
  
  public CDBasisDefaultPackageTrafo() {
    this(false);
  }
  
  @Override
  public void visit(ASTCDCompilationUnit node) {
    artifactPackageParts = new ArrayList<>();
    
    // set artifact package parts to the default package
    if (node.isPresentMCPackageDeclaration()) {
      artifactPackageParts.addAll(node.getMCPackageDeclaration().getMCQualifiedName()
          .getPartsList());
    }
  }
  
  @Override
  public void visit(ASTCDDefinition node) {
    // add cd name (lower case) to the default package
    artifactPackageParts.add(node.getName());
    
    // create the default package
    ASTMCQualifiedName qualName = CDBasisMill.mCQualifiedNameBuilder().addAllParts(
        artifactPackageParts).build();
    ASTCDPackage defPkg = CDBasisMill.cDPackageBuilder().setMCQualifiedName(qualName).build();
    
    CDBasisTraverser traverser = CDBasisMill.inheritanceTraverser();
    // Collect the FQN of all types moved into a package
    MovedTypeCollector movedTypes = new MovedTypeCollector(artifactPackageParts);
    traverser.add4CDBasis(movedTypes);
    
    // add elements (that are not packages themselves) to the default package
    for (ASTCDElement e : node.getCDElementList()) {
      if (!(e instanceof ASTCDPackage)) {
        defPkg.addCDElement(e);
        e.accept(traverser);
      }
    }
    
    // remove these cd elements from cd definition
    node.removeAllCDElements(defPkg.getCDElementList());
    
    // the remaining direct elements of the cd definition are all packages
    for (ASTCDElement e : node.getCDElementList()) {
      ASTCDPackage pkg = (ASTCDPackage) e;
      // Collect the (original) FQN of all types moved into a package
      movedTypes.packageName.clear();
      e.accept(traverser);
      // extend the package with the prefix of the default package
      pkg.getMCQualifiedName().getPartsList().addAll(0, defPkg.getMCQualifiedName().getPartsList());
    }
    
    // add default package to cd elements of the diagram and
    // explicitly set the link towards the default package
    node.addCDElement(0, defPkg);
    node.setDefaultPackage(defPkg);
    
    // Finally: Redirect all type references
    // TODO: This *might* break with overlapping types?
    // This is required (currently) for the completion of the decorated ST
    if (this.doRedirectTypeReferences) {
      traverser = CDBasisMill.inheritanceTraverser();
      traverser.add4MCBasicTypes(new PrefixAdder(movedTypes.movedTypes, defPkg.getMCQualifiedName()
          .getPartsList(), renamedItems));
      prefixLength = defPkg.getMCQualifiedName().getPartsList().size();
      node.accept(traverser);
    }
    
  }
  
  public void transform(ASTCDCompilationUnit ast) {
    CDBasisTraverser t = CDBasisMill.inheritanceTraverser();
    t.add4CDBasis(this);
    ast.accept(t);
  }
  
  public void undoRename() {
    if (!this.doRedirectTypeReferences)
      throw new IllegalStateException("Defaulttrafo did not redirect type references before");
    for (ASTMCQualifiedName i : renamedItems)
      for (int n = 0; n < prefixLength; n++)
        i.getPartsList().removeFirst();
  }
  
  static class MovedTypeCollector implements CDBasisVisitor2 {
    
    protected Set<String> movedTypes = new HashSet<>();
    protected Stack<String> packageName = new Stack<>();
    
    public MovedTypeCollector(List<String> pack) {
      packageName.addAll(pack);
    }
    
    @Override
    public void visit(ASTCDType node) {
      movedTypes.add(Joiners.DOT.join(Joiners.DOT.join(packageName), node.getName()));
    }
    
    @Override
    public void visit(ASTCDPackage node) {
      packageName.add(node.getName());
    }
    
    @Override
    public void endVisit(ASTCDPackage node) {
      packageName.pop();
    }
    
  }
  
  /**
   * Add the prefix to all MCQualifiedTypes
   */
  protected static class PrefixAdder implements MCBasicTypesVisitor2 {
    
    protected final Set<String> toRename;
    protected final List<String> prefix;
    protected final Set<ASTMCQualifiedName> renamedItems;
    
    public PrefixAdder(Set<String> toRename, List<String> prefix,
        Set<ASTMCQualifiedName> renamedItems) {
      this.toRename = toRename;
      this.prefix = prefix;
      this.renamedItems = renamedItems;
    }
    
    @Override
    public void visit(ASTMCQualifiedType node) {
      String joined = Joiners.DOT.join(node.getNameList());
      if (toRename.contains(joined)) {
        List<String> newParts = new ArrayList<>(node.getMCQualifiedName().getPartsList());
        newParts.addAll(0, prefix);
        node.getMCQualifiedName().setPartsList(newParts);
        renamedItems.add(node.getMCQualifiedName());
      }
    }
    
  }
  
}
