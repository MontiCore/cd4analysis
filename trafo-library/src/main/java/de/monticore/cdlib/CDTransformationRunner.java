package de.monticore.cdlib;

import com.google.common.collect.Maps;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.tf.runtime.ODRule;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CDTransformationRunner {
  
  private final ASTCDCompilationUnit ast;
  
  public CDTransformationRunner(ASTCDCompilationUnit ast) {
    this.ast = ast;
  }
  
  public void transform(String trafoName) {
    transform(trafoName, Collections.emptyMap());
  }
  
  public void transform(String trafoName, Map<String, Object> params) {
    CDTransformationLibType type = CDTransformationLibType.valueOf(trafoName);
    transform(type, params);
  }
  
  public void transform(CDTransformationLibType type, Map<String, Object> params) {
    Map<String, CDTransformationParameter<?>> wrappedParams = wrapParameterValues(params);
    _transform(type, wrappedParams);
  }
  
  private void _transform(CDTransformationLibType type,
      Map<String, CDTransformationParameter<?>> params) {
    try {
      type.apply(this.ast, params);
    }
    catch (IOException ex) {
      Log.error("0x4A522: Transformation failed: " + type, ex);
    }
  }
  
  private static Map<String, CDTransformationParameter<?>> wrapParameterValues(
      Map<String, Object> params) {
    return Maps.transformValues(params, CDTransformationParameter::fromObject);
  }
  
  public void genericTransform(ODRule trafo) {
    genericTransform(trafo, Collections.emptyMap());
  }
  
  public void genericTransform(ODRule trafo, Map<String, Object> params) {
    Map<String, CDTransformationParameter<?>> wrappedParams = wrapParameterValues(params);
    _genericTransform(trafo, wrappedParams);
  }
  
  private void _genericTransform(ODRule trafo, Map<String, CDTransformationParameter<?>> params) {
    applyParameters(trafo, params);
    if (trafo.doPatternMatching()) {
      trafo.doReplacement();
    }
  }
  
  private void applyParameters(ODRule trafo, Map<String, CDTransformationParameter<?>> params) {
    for (Map.Entry<String, CDTransformationParameter<?>> param : params.entrySet()) {
      if (param.getValue() instanceof List) {
        Log.warn("Ignoring list parameter: " + param.getKey() + " in generic transformation: "
            + trafo.getClass().getSimpleName());
        continue;
      }
      try {
        Method valueSetter =
            trafo.getClass().getDeclaredMethod("set_$" + param.getKey(), String.class);
        String value = param.getValue().asString();
        valueSetter.invoke(trafo, value);
      }
      catch (NoSuchMethodException e) {
        Log.warn("Ignoring unknown parameter: " + param.getKey() + " in generic transformation: "
            + trafo.getClass().getSimpleName());
      }
      catch (InvocationTargetException | IllegalAccessException e) {
        Log.error(
            "0x4A523: Could not set parameter: " + param.getKey() + " in generic transformation: "
                + trafo.getClass().getSimpleName(), e);
      }
    }
  }
}
