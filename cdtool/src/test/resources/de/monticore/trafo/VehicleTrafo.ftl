<#-- (c) https://github.com/MontiCore/monticore -->
<#assign trafoRunner = tc.instantiate("de.monticore.cdlib.CDTransformationRunner", [ast])>

${trafoRunner.transform("PULL_UP_ATTRIBUTES")}
${trafoRunner.transform("ENCAPSULATE_ATTRIBUTES", {"attributes":["brand","model","year"]})}
