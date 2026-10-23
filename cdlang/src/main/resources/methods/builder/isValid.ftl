<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes","staticErrorCode")}

<#list attributes as attribute>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(attribute.getName()+attribute.getMCType().printType())>
<#assign attributeType = TypeCheck3.symTypeFromAST(attribute.getMCType())>

<#-- Check if the attribute is not a list, set or optional as they have isAbsent methods-->
<#if (!(MCCollectionSymTypeRelations.isList(attributeType) ||
        MCCollectionSymTypeRelations.isSet(attributeType) ||
        MCCollectionSymTypeRelations.isOptional(attributeType)))>

  <#-- as primitive types cannot be check for == null we need to ignore them -->
  <#if (!attributeType.isPrimitive())>
if (this.${attribute.getName()} == null) {
  Log.error("${errorCode} ${attribute.getName()} of type ${attribute.printType()} must not be null");
  return false;
}
  </#if>
</#if>
</#list>

return true;
