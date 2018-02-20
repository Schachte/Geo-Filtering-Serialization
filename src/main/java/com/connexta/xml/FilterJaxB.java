package com.connexta.xml;

import com.connexta.xml.Template.FilterLeafNode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
import net.opengis.filter.v_2_0.*;

public class FilterJaxB {
  public FilterJaxB() {}

  public static void main(String[] args) throws Exception {

    ObjectFactory factory = new ObjectFactory();
    JAXBContext context =
        JAXBContext.newInstance(
            FilterType.class.getPackage().getName()
                + ":"
                + FilterType.class.getPackage().getName());

    List<FilterLeafNode> nodeList = new ArrayList<>();
    FilterLeafNode leafNode = new FilterLeafNode("id", "exampleid", "=");
    FilterLeafNode leafNode2 = new FilterLeafNode("attrName1", "5", ">=");
    FilterLeafNode leafNode3 = new FilterLeafNode("attrName2", "20", "<=");
    nodeList.add(leafNode);
    nodeList.add(leafNode2);
    nodeList.add(leafNode3);

    Template.FilterNode fN = new Template.FilterNode("And", nodeList);
    Template newTemplate = new Template("100", "Template 1", "This is a template", fN);

    print(String.format("Template id is %s", newTemplate.id));
    print(String.format("Template title is %s", newTemplate.title));
    print(String.format("Template description is %s", newTemplate.description));

    // Lets find the root node
    String rootNodeType = fN.type;

    BinaryLogicOpType _blop = new BinaryLogicOpType();
    JAXBElement<BinaryLogicOpType> root = constructRootElement(_blop, factory, rootNodeType);

    /** LETS CREATE THE FIRST CHILD ELEMENT */
    BinaryComparisonOpType attrName1GTEFive = new BinaryComparisonOpType();
    _blop.getOps().add(factory.createPropertyIsEqualTo(attrName1GTEFive));

    JAXBElement<String> attr1Property = factory.createValueReference(fN.nodes.get(0).getProperty());
    LiteralType attr1ValueLiteral = new LiteralType();
    attr1ValueLiteral.getContent().add(fN.nodes.get(0).getValue());

    JAXBElement<LiteralType> attr1Value = factory.createLiteral(attr1ValueLiteral);

    attrName1GTEFive.getExpression().add(attr1Property);
    attrName1GTEFive.getExpression().add(attr1Value);

    /** LETS CREATE THE SECOND CHILD ELEMENT */
    BinaryComparisonOpType attrName2LTETwenty = new BinaryComparisonOpType();
    _blop.getOps().add(factory.createPropertyIsLessThanOrEqualTo(attrName2LTETwenty));

    JAXBElement<String> attr2Property = factory.createValueReference(fN.nodes.get(1).getProperty());
    LiteralType attr2ValueLiteral = new LiteralType();
    attr2ValueLiteral.setType(new QName("literalNameTest"));
    attr2ValueLiteral.getContent().add(fN.nodes.get(1).getValue());

    JAXBElement<LiteralType> attr2Value = factory.createLiteral(attr2ValueLiteral);

    attrName2LTETwenty.getExpression().add(attr2Property);
    attrName2LTETwenty.getExpression().add(attr2Value);

    /** LETS CREATE THE THRID CHILD ELEMENT */
    BinaryComparisonOpType functionTest = new BinaryComparisonOpType();
    _blop.getOps().add(factory.createPropertyIsEqualTo(functionTest));

    JAXBElement<String> attr3Property = factory.createValueReference(fN.nodes.get(1).getProperty());
    FunctionType funcTest = new FunctionType();
    funcTest.getExpression().add(attr2Value);
    funcTest.setName("functionName");

    JAXBElement<FunctionType> attr3Func = factory.createFunction(funcTest);

    functionTest.getExpression().add(attr3Property);
    functionTest.getExpression().add(attr3Func);

    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.marshal(root, System.out);

    // Unmarshal sample XML to understand object structure of JaxB filter 2.0 XML POJO
    FilterJaxB filterer = new FilterJaxB();
    filterer.unmarshalMe(context);
  }

  public static JAXBElement<BinaryLogicOpType> constructRootElement(
      BinaryLogicOpType ele, ObjectFactory f, String rootType) throws Exception {

    switch (rootType) {
      case "And":
        return f.createAnd(ele);
      default:
        throw new Exception("Error");
    }
  }

  public static void print(String input) {
    System.out.println(input);
  }

  public void unmarshalMe(JAXBContext context) throws JAXBException {
    Unmarshaller unmarshal = context.createUnmarshaller();
    JAXBElement<FilterType> filter =
        (JAXBElement<FilterType>)
            unmarshal.unmarshal(
                new File(
                    "/Users/schachte/Desktop/GeoToolsFiltering/src/main/resources/Filter01.xml"));

    filter.getValue().getLogicOps();

    JAXBElement<? extends LogicOpsType> list = filter.getValue().getLogicOps();
    LogicOpsType data = list.getValue();
  }
}
