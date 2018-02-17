package com.connexta.testbed;

import java.util.ArrayList;
import java.util.List;
import org.geotools.filter.LiteralExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

public class PredicateSubstitutor implements Function {
  private static final String PLACEHOLDER = "{{replacement_data}}";

  static final FunctionName NAME = new FunctionNameImpl("replaceme", String.class);

  public String getName() {
    return NAME.getName();
  }

  public FunctionName getFunctionName() {
    return NAME;
  }

  public List<Expression> getParameters() {
    return new ArrayList<>();
  }

  public Literal getFallbackValue() {
    return new LiteralExpressionImpl(PLACEHOLDER);
  }

  public Object evaluate(Object object) {
    return new LiteralExpressionImpl(PLACEHOLDER);
  }

  @SuppressWarnings("unchecked")
  public <T> T evaluate(Object object, Class<T> context) {
    if (context.isInstance(PLACEHOLDER)) {
      return (T) PLACEHOLDER;
    }
    LiteralExpressionImpl literal = new LiteralExpressionImpl(PLACEHOLDER);
    if (context.isInstance(literal)) {
      return (T) literal;
    }
    return null;
  }

  public Object accept(ExpressionVisitor visitor, Object extraData) {
    return visitor.visit(this, extraData);
  }
}
