<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astcdClass","staticErrorCode")}
<#assign plainName = astcdClass.getName()?remove_ending("TOP")>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(astcdClass.getName())>
//TODO remove it not needed
// if (this instanceof ${plainName}) {
  visitor.handle((${plainName}) this);
// } else {
//   throw new UnsupportedOperationException("${errorCode} Only handwritten class ${plainName} is supported for the visitor");
// }
