import de.monticore.cd4codebasis._ast.ASTCDMethod
import de.monticore.cd4codebasis._ast.ASTCDMethodBuilder
import de.monticore.cd4codebasis._ast.ASTCDParameter
import de.monticore.cdbasis._ast.ASTCDClass
import de.monticore.cdbasis._ast.ASTCDCompilationUnit
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface
import de.monticore.generating.templateengine.GlobalExtensionManagement
import de.monticore.generating.templateengine.StringHookPoint
import de.monticore.generating.templateengine.TemplateHookPoint
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedTypeBuilder
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgumentBuilder
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType
import de.monticore.types.mccollectiontypes._ast.ASTMCListTypeBuilder
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument

/**
 * This script replaces generated resolveMany methods to also look in related scopes
 */

// glex, astGrammar, decoratedCD
GlobalExtensionManagement glex = args[0] as GlobalExtensionManagement
ASTCDCompilationUnit decoratedCD = args[2] as ASTCDCompilationUnit

// TODO: Replace template is for some reason not transitive
// glex.replaceTemplate("_symboltable.iscope.Filter", new StringHookPoint(...))
// is never invoked, b/c TC#getTemplateForwardings only ever looks for one template
for (ASTCDInterface cl : decoratedCD.getCDDefinition().getCDInterfacesList()) {
  if (cl.getName().endsWith("Scope") && !cl.getName().endsWith("ArtifactScope")) {
    for (ASTCDMethod m : cl.getCDMethodList()) {
      if (m.getMCReturnType().isPresentMCType() && m.getMCReturnType().getMCType() instanceof ASTMCGenericType) {
        ASTMCTypeArgument symbolType = ((ASTMCGenericType) m.getMCReturnType().getMCType()).getMCTypeArgumentList().get(0);
        List<String> nameList = ((ASTMCBasicTypeArgument) symbolType).getMCQualifiedType().getNameList();
        String last = nameList.get(nameList.size() - 1);
        String actualName = last.substring(0, last.length() - 6);

        String potResolveDownMethodName = "resolve" + actualName + "Many"

        if (m.getName().equals(potResolveDownMethodName) && m.getCDParameterList().size() == 4 && !cl.getName().endsWith("GlobalScope")) {
          //System.out.println("replacing " + potResolveDownMethodName)

          glex.replaceTemplate("cd2java.EmptyBody", m, new TemplateHookPoint("cdlang.ResolveMany4IScope",
              actualName, symbolType.printType()))
        }
      }
    }
  }
}
