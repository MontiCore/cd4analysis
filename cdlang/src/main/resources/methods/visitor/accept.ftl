<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astcdClass","staticErrorCode")}
<#assign plainName = astcdClass.getName()?remove_ending("TOP")>
<#assign errorCode = staticErrorCode + cdGenService.getGeneratedErrorCode(astcdClass.getName())>
  visitor.handle((${plainName}) this);
