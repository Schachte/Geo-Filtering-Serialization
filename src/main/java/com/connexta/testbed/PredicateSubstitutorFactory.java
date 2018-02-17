package com.connexta.testbed;

import java.util.Collections;
import java.util.List;
import org.geotools.feature.NameImpl;
import org.geotools.filter.FunctionFactory;
import org.opengis.feature.type.Name;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

public class PredicateSubstitutorFactory implements FunctionFactory {
  public List<FunctionName> getFunctionNames() {
    return Collections.singletonList(PredicateSubstitutor.NAME);
  }

  public Function function(String name, List<Expression> args, Literal fallback) {
    return function(new NameImpl(name), args, fallback);
  }

  public Function function(Name name, List<Expression> args, Literal fallback) {
    if (PredicateSubstitutor.NAME.getFunctionName().equals(name)) {
      return new PredicateSubstitutor();
    }
    return null;
  }
}
