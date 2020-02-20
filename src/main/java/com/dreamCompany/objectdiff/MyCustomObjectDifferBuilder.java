package com.dreamCompany.objectdiff;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import org.apache.commons.lang3.StringUtils;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class MyCustomObjectDifferBuilder {

    public static ObjectDifferBuilder createStringObjectDifferBuilder() {
        ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
        builder.comparison()
            .ofType(String.class)
            .toUse((node, type, working, base) -> {
                if (equalsIgnoreCase(StringUtils.normalizeSpace((String) working), StringUtils.normalizeSpace((String) base))) {
                    node.setState(DiffNode.State.UNTOUCHED);
                } else {
                    node.setState(DiffNode.State.CHANGED);
                }
            })
            .and().filtering()
            .returnNodesWithState(DiffNode.State.ADDED)
            .returnNodesWithState(DiffNode.State.CHANGED)
            .returnNodesWithState(DiffNode.State.REMOVED)
            .returnNodesWithState(DiffNode.State.UNTOUCHED);

        return builder;
    }

}
