<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("package")}
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${tc.include("cd2java.Package", package)}

${tc.include("cd2java.Imports")}

${tc.include("cd2java.Annotations")}
${cdPrinter.printSimpleModifier(ast.getModifier())} interface ${ast.getName()} <#rt><#lt>
<#if ast.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(ast.getCDExtendUsage().getSuperclassList())} </#if> { <#rt><#lt>


<#list ast.getCDAttributeList() as attribute>
    ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list ast.getCDMethodList() as method>
  <#if !method.getModifier().isAbstract()>default </#if>${tc.include("cd2java.Method", method)}
</#list>
}
