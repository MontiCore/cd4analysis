<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdInterface", "package")}
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${tc.include("cd2java.Package", package)}

${tc.include("cd2java.Imports")}

${tc.include("cd2java.Annotations")}
${cdPrinter.printSimpleModifier(cdInterface.getModifier())} interface ${cdInterface.getName()} <#rt><#lt>
<#if cdInterface.isPresentCDExtendUsage()>extends ${cdPrinter.printObjectTypeList(cdInterface.getCDExtendUsage().getSuperclassList())} </#if> { <#rt><#lt>


<#list cdInterface.getCDAttributeList() as attribute>
    ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list cdInterface.getCDMethodList() as method>
  <#if !method.getModifier().isAbstract()>default </#if>${tc.include("cd2java.Method", method)}
</#list>
}
