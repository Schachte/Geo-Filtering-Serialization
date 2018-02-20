package com.connexta.xml;

import java.util.Collections;
import java.util.List;
import java.util.logging.Filter;

public class Template {
    String id;
    String title;
    String description;
    FilterNode root;

    public Template() {}
    public Template(String id, String title, String description, FilterNode root) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.root = root;
    }

    static class FilterNode {
        String type;
        Boolean isLeaf;
        List<FilterLeafNode> nodes;

        FilterNode() {}
        FilterNode(String type, List<FilterLeafNode> nodes) {
            this.type = type;
            this.nodes = nodes;
            this.isLeaf = false;
        }
    }

    static class FilterLeafNode extends FilterNode {
        String property;
        String value;
        String type;
        String nodeId;
        Boolean isVisible;
        Boolean isReadOnly;

        FilterLeafNode(String property, String value, String type) {
            super();
            this.property = property;
            this.value = value;
            this.type = type;
            this.isLeaf = true;
        }

        public String getProperty() {
            return property;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }
    }
}


