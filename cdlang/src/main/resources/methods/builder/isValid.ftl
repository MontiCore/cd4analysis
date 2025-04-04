<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes","staticErrorCode")}

<#list attributes as attribute>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(attribute.getName()+attribute.getMCType().printType())>
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

<#-- Check if the attribute is not a list, set or optional as they have isAbsent methods-->
<#if (!(MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())))>

<#-- Primitive types no way to get them better yet -->
<#-- PLEASE FIX THIS ASAP -->
<#-- as primitive types cannot be check for == null we need either ignore them or build attributes -->
<#-- to check whether they have been set -->

<#if (!(attribute.getMCType().printType() == "boolean" ||
     attribute.getMCType().printType() == "short" ||
     attribute.getMCType().printType() == "int" ||
     attribute.getMCType().printType() == "long" ||
     attribute.getMCType().printType() == "float" ||
     attribute.getMCType().printType() == "double" ||
     attribute.getMCType().printType() == "byte"))>

if (this.${attribute.getName()} == null) {
  Log.error("${errorCode}");
  return false;
}
</#if>
</#if>
</#list>

return true;
