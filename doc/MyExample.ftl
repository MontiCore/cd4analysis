<#-- (c) https://github.com/MontiCore/monticore -->
<#assign trafoRunner = tc.instantiate("de.monticore.cdlib.CDTransformationRunner", [ast])>

${trafoRunner.transform("ENCAPSULATE_ATTRIBUTES")}
