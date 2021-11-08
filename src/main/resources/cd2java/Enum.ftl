<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdEnum")}
/* (c) https://github.com/MontiCore/monticore */
<#assign cdPrinter = glex.getGlobalVar("cdPrinter")>

${tc.include("cd2java.Package")}

${tc.include("cd2java.Annotations")}
public enum ${cdEnum.getName()}
<#if cdEnum.isPresentCDInterfaceUsage()>implements ${cdPrinter.printObjectTypeList(cdEnum.getCDInterfaceUsage().getInterfaceList())} </#if>{

<#list cdEnum.getCDEnumConstantList() as constants>
  ${tc.include("cd2java.EmptyConstants", constants)}<#if !constants?is_last>,</#if>
</#list>
;

<#list cdEnum.getCDAttributeList() as attribute>
  ${tc.include("cd2java.Attribute", attribute)}
</#list>

<#list cdEnum.getCDConstructorList() as constructor>
  ${tc.include("cd2java.Constructor", constructor)}
</#list>

<#list cdEnum.getCDMethodList() as method>
  ${tc.include("cd2java.Method", method)}
</#list>
}
