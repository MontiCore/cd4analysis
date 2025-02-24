<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes","staticErrorCode")}

<#list attributes as attribute>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(attribute.getName()+attribute.getMCType().printType()) + " " + attribute.getName() + " of type " + attribute.getMCType().printType() + " must not be null">
<#assign MCCollectionSymTypeRelations = glex.getGlobalVar("mcCollectionSymTypeRelations")>

<#-- Check if the attribute is not a collection or optional as they have isAbsent methods-->
<#if (!(MCCollectionSymTypeRelations.isList(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isSet(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isOptional(attribute.getSymbol().getType())))>

<#-- Primitive types no way to get them better yet -->
<#-- PLEASE FIX THIS ASAP -->
<#if (MCCollectionSymTypeRelations.isBoolean(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isByte(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isShort(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isInt(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isLong(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isFloat(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isDouble(attribute.getSymbol().getType()) ||
     MCCollectionSymTypeRelations.isChar(attribute.getSymbol().getType()))>

if (this.${attribute.getName()} == null) {
  Log.error("${errorCode}");
  return false;
}
</#if>
</#if>
</#list>

return true;
