<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.JavaDoc")}
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("MethodContent:Annotations")}
<#assign isAbstract = ast.getModifier().isAbstract()>
<@compress single_line=true>
  ${cdPrinter.printSimpleModifier(ast.getModifier())}
  <#if ast.isPresentTypeParameters()>${cdPrinter.printTypeParameters(ast.getTypeParameters())}</#if>
  ${cdPrinter.printType(ast.getMCReturnType())}
  ${ast.getName()}
  (${cdPrinter.printCDParametersDecl(ast.getCDParameterList())})
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if>
</@compress>
<#if isAbstract><#lt>;<#else> {
    ${tc.include("cd2java.EmptyBody")}
}
</#if>
