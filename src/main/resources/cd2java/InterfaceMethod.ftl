<#-- (c) https://github.com/MontiCore/monticore -->
${tc.include("cd2java.Annotations")}
<#-- Annotations hook -->
${defineHookPoint("InterfaceMethodContent:Annotations")}
<#assign isDefault = ast.isIsDefault()>
<#if isDefault>default </#if>${cdPrinter.printSimpleModifier(ast.getModifier())} ${cdPrinter.printType(ast.getMCReturnType())} ${ast.getName()} (${cdPrinter.printCDParametersDecl(ast.getCDParameterList())})
<#if ast.isPresentCDThrowsDeclaration()> ${cdPrinter.printThrowsDecl(ast.getCDThrowsDeclaration())}</#if>
<#if isDefault> {
  ${tc.include("cd2java.EmptyBody")}
}
<#else>;
</#if>
