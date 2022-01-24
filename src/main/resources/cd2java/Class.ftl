<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdClass", "package")}
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>
/* (c) https://github.com/MontiCore/monticore */

${defineHookPoint("ClassContent:addComment")}

${tc.include("cd2java.Package", package)}

${tc.include("cd2java.Imports")}
${cdPrinter.printImportList(cd4c.getImportList(cdClass))}

<#-- Imports hook -->
${defineHookPoint("ClassContent:Imports")}

${tc.include("cd2java.Annotations")}

<#-- Annotations hook -->
${defineHookPoint("ClassContent:Annotations")}

${cdPrinter.printSimpleModifier(cdClass.getModifier())} class ${cdClass.getName()} <#rt><#lt>
<#if cdClass.isPresentCDExtendUsage()>extends ${cdPrinter.printType(cdClass.getCDExtendUsage().getSuperclass(0))} </#if> <#rt><#lt>
<#if cdClass.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(cdClass.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#-- Elements HOOK -->
${defineHookPoint("ClassContent:Elements")}

<#list cdClass.getCDAttributeList() as attribute>
    ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list cdClass.getCDConstructorList() as constructor>
    ${tc.include("cd2java.Constructor", constructor)}
</#list>

<#list cdClass.getCDMethodList() as method>
    ${tc.include("cd2java.Method", method)}
</#list>

}


