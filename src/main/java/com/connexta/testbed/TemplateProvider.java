package com.connexta.testbed;

import java.util.concurrent.TimeUnit;
import javax.xml.namespace.QName;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FunctionFactory;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.v2_0.FESConfiguration;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.geotools.xml.Encoder;
import org.opengis.filter.*;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.VolatileFunction;

public class TemplateProvider {
  private static final String CQL_TEMPLATE = "(attName >= replaceme() AND title LIKE 'greetings')";

  public static void main(String[] args) throws Exception {

    /**
     * When we get the user input, get preprocess all blank to pass in the name of the function This
     * will replace all the blanks with valid XML functions
     */
    Filter filter = CQL.toFilter(CQL_TEMPLATE);

    // Setup a substitution visitor that can traverse through the tree
    FilterVisitor visitor = new SubstitutingVisitor();

    // This will actually traverse through the tree
    Filter subd = (Filter) visitor.visit((And) filter, null);

    Encoder encoder = setupEncoder();

    String xml =
        encoder.encodeAsString(filter, new QName("http://www.opengis.net/fes/2.0", "Filter"));

    /**
     * Print the output results for how the data is persisted and how the CQL gets returned before processing
     */
    filterToXML(xml);
    filterToCQl(subd);
  }

  // This is how the data is going to be persisted following XML standards compliant spec
  private static void filterToXML(String xml) throws InterruptedException {
    print(xml);
    printSeparator();
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

  private static void printSeparator() {
    print(
        "____________________________________________________________________"
            + System.lineSeparator());
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
  }
}
