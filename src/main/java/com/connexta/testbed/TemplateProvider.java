package com.connexta.testbed;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FunctionFactory;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.filter.v2_0.FESConfiguration;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.geotools.xml.Encoder;
import org.opengis.filter.*;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.VolatileFunction;

public class TemplateProvider {

  public static void main(String[] args) throws Exception {

    /**
     * { attributeValues: [ { name: "beNumber", default: null }, { name: "cloudCoverage", default:
     * 0.98 } ], predicateTree: "(attName >= replacementFunc() AND title LIKE 'greetings') AND
     * attName2 = replacementFunc()" }
     */

    /**
     * This gets stripped off the incoming JSON blob Anything that contains "replacementFunc()" will
     * get persisted as a valid XML function
     */
    String CQL_TEMPLATE =
        "(attName >= replacementFunc() AND title like 'test') AND attName2 = replacementFunc()";

    /**
     * When we get the user input, preprocess all blank to pass in the name of the function This
     * will replace all the blanks with valid XML functions
     */
    Expression func = ECQL.toExpression("replacementFunc()");
    Filter filter = CQL.toFilter(CQL_TEMPLATE);

    // Setup a substitution visitor that can traverse through the tree
    SubstitutingVisitor visitor = new SubstitutingVisitor();

    // This will actually traverse through the tree
    Filter subd = (Filter) visitor.visit(visitor, filter);

    Encoder encoder = setupEncoder();

    String xml =
        encoder.encodeAsString(filter, new QName("http://www.opengis.net/fes/2.0", "Filter"));

    /**
     * Print the output results for how the data is persisted and how the CQL gets returned before
     * processing
     */
    filterToXML(xml);
    filterToCQl(subd);
    replacePlaceholderParams(CQL.toCQL(subd));
  }

  // This is how the data is going to be persisted following XML standards compliant spec
  private static void filterToXML(String xml) throws InterruptedException {
    print(xml);
    TimeUnit.MILLISECONDS.sleep(1000);
  }

  private static void filterToCQl(Filter subd) {
    print(CQL.toCQL(subd));
  }

  private static Encoder setupEncoder() {
    // Encoding the filter to XML with supporting function filter 2.0 spec
    Encoder encoder = new Encoder(new FESConfiguration());

    encoder.setIndenting(true);

    encoder.setIndentSize(2);

    return encoder;
  }

  private static FunctionFactory getFuncFactory() throws ClassNotFoundException {

    FunctionFactory functionFactory =
        CommonFactoryFinder.getFunctionFactories(null)
            .stream()
            .filter(f -> f.getFunctionNames().contains(PredicateSubstitutor.NAME))
            .findAny()
            .orElseThrow(() -> new ClassNotFoundException("Cannot find the factory"));

    return functionFactory;
  }

  private static void print(String msg) {
    System.out.println(msg);
  }

  private static class SubstitutingVisitor extends DuplicatingFilterVisitor {
    @Override
    public Object visit(Function function, Object data) {
      if (function instanceof VolatileFunction) {
        return super.visit(function, data);
      }
      return CommonFactoryFinder.getFilterFactory2().literal(function.evaluate(data));
    }

    public Object visit(FilterVisitor visitor, Filter filter) throws Exception {
      if (filter instanceof And) {
        return visitor.visit((And) filter, null);
      } else if (filter instanceof Or) {
        return visitor.visit((Or) filter, null);
      } else {
        throw new Exception("Unsupported operation");
      }
    }
  }

  private static void replacePlaceholderParams(String postProcessCQL) {
    ArrayList<String> userInput = new ArrayList<String>();

    userInput.add("5");

    userInput.add("5");

    StringBuilder postUserInput = new StringBuilder();

    print(String.format("The input string is: \n %s", postProcessCQL));

    String[] inputCQLList = postProcessCQL.split(" ");
    for (String token : inputCQLList) {
      if (token.contains("replacement")) {
        postUserInput.append(userInput.get(0) + " ");
        userInput.remove(0);
      } else {
        postUserInput.append(token + " ");
      }
    }

    print(String.format("The output string is: \n %s", postUserInput.toString()));
  }
}
