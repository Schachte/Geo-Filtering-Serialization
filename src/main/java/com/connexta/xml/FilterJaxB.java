package com.connexta.xml;


import net.opengis.fes20.impl.LogicOpsTypeImpl;
import net.opengis.filter.v_2_0.*;
import org.opengis.filter.BinaryLogicOperator;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.connexta.xml.Template.FilterNode;
import com.connexta.xml.Template.FilterLeafNode;

public class FilterJaxB {
    public FilterJaxB() {}

    public static void main(String[] args) throws Exception {

        ObjectFactory factory = new ObjectFactory();
        JAXBContext context = JAXBContext.newInstance(FilterType.class.getPackage()
        .getName()
        + ":"
        + FilterType.class.getPackage().getName());

        List<FilterLeafNode> nodeList = new ArrayList<>();
        FilterLeafNode leafNode = new FilterLeafNode("id", "exampleid", "=");
        FilterLeafNode leafNode2 = new FilterLeafNode("attrName1", "5", ">=");
        FilterLeafNode leafNode3 = new FilterLeafNode("attrName2", "20", "<=");
        nodeList.add(leafNode);
        nodeList.add(leafNode2);
        nodeList.add(leafNode3);

        Template.FilterNode fN = new Template.FilterNode("Or", nodeList);
        Template newTemplate = new Template("100", "Template 1", "This is a template", fN);

        print(String.format("Template id is %s", newTemplate.id));
        print(String.format("Template title is %s", newTemplate.title));
        print(String.format("Template description is %s", newTemplate.description));

        // Lets find the root node
        String rootNodeType = fN.type;

        BinaryLogicOpType _blop = new BinaryLogicOpType();
        JAXBElement<BinaryLogicOpType> root = constructRootElement(_blop, factory, rootNodeType);

        /**
         * LETS CREATE THE FIRST CHILD ELEMENT
         */
        BinaryComparisonOpType attrName1GTEFive = new BinaryComparisonOpType();
        _blop.getOps().add(factory.createPropertyIsEqualTo(attrName1GTEFive));

        JAXBElement<String> attr1Property =
                factory.createValueReference(fN.nodes.get(0).getProperty());
        LiteralType attr1ValueLiteral = new LiteralType();
        attr1ValueLiteral.getContent().add(fN.nodes.get(0).getValue());

        JAXBElement<LiteralType> attr1Value = factory.createLiteral(attr1ValueLiteral);

        attrName1GTEFive.getExpression().add(attr1Property);
        attrName1GTEFive.getExpression().add(attr1Value);

        /**
         *
         * LETS CREATE THE SECOND CHILD ELEMENT
         */
        BinaryComparisonOpType attrName2LTETwenty = new BinaryComparisonOpType();
        _blop.getOps().add(factory.createPropertyIsLessThanOrEqualTo(attrName2LTETwenty));

        JAXBElement<String> attr2Property =
                factory.createValueReference(fN.nodes.get(1).getProperty());
        LiteralType attr2ValueLiteral = new LiteralType();
        attr2ValueLiteral.getContent().add(fN.nodes.get(1).getValue());

        JAXBElement<LiteralType> attr2Value = factory.createLiteral(attr2ValueLiteral);

        attrName2LTETwenty.getExpression().add(attr2Property);
        attrName2LTETwenty.getExpression().add(attr2Value);


        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(root, System.out);

        // Unmarshal sample XML to understand object structure of JaxB filter 2.0 XML POJO
        FilterJaxB filterer = new FilterJaxB();
        filterer.unmarshalMe(context);
    }

    public static JAXBElement<BinaryLogicOpType> constructRootElement(BinaryLogicOpType ele, ObjectFactory f, String rootType) throws Exception {

        switch (rootType) {
            case "Or":
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
        JAXBElement<FilterType> filter = (JAXBElement<FilterType>) unmarshal.unmarshal(new File("/Users/schachte/Desktop/GeoToolsFiltering/src/main/resources/Filter01.xml"));

        filter.getValue().getLogicOps();

        JAXBElement<? extends LogicOpsType> list = filter.getValue().getLogicOps();
        LogicOpsType data = list.getValue();
    }
}
