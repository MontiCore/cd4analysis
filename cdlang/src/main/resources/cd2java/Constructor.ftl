<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.JavaDoc")}
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("ConstructorContent:Annotations")}
<@compress single_line=true>
${cdPrinter.printSimpleModifier(ast.getModifier())}
  ${ast.getName()}
  <#if ast.isPresentTypeParameters()>${cdPrinter.printTypeParameters(ast.getTypeParameters())}</#if>
  (${cdPrinter.printCDParametersDecl(ast.getCDParameterList())})
  <#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if>
</@compress> {
  ${tc.include("cd2java.EmptyBody")}
}
