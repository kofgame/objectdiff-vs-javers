package com.dreamCompany.objectdiff;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.dreamCompany.model.Address;
import com.dreamCompany.model.Employee;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

/**
 * Created by Andrey on 24.09.2019.
 */
public class ObjectDiffExample {

    private static ObjectDiffer objectDiffer = initObjectDiffer();

    private static final String FIELD_NAME_EXPECTED_AND_ACTUAL_VALUE = "%s -> ExpectedValue [%s] ActualValue [%s]";

    @Test
    public void verifySimplePropertyUpdate() {

        Employee employee1 = Employee.builder()
            .name("employee1")
            .position("accountant")
            .addresses(Sets.newHashSet())
            .build();
        Employee employee2 = Employee.builder()
            .name("employee2")
            .position("accountant")
            .addresses(Sets.newHashSet())
            .build();

        DiffNode diffNode = objectDiffer.compare(employee1, employee2);

        List<String> differences = new ArrayList<>();
        if (diffNode.isChanged()) { // NOTE: hasChanges is much broader
            diffNode.visit((node, visit) -> {
                if (node.isChanged() && !node.isRootNode() && !node.hasChildren()) {
                    Object expectedValue = node.canonicalGet(employee1);
                    Object actualValue = node.canonicalGet(employee2);
                    String message = String.format(
                        FIELD_NAME_EXPECTED_AND_ACTUAL_VALUE, node.getPropertyName(), expectedValue, actualValue);
                    differences.add(message);
                }
            });
        }
        Assertions.assertThat(Iterables.getOnlyElement(differences))
            .isEqualTo("name -> ExpectedValue [employee1] ActualValue [employee2]");
        System.out.println(differences);
    }

    @Test
    public void verifyCollectionUpdate() {
        Address address1 = new Address("New Jersey", "5th Avenue");

        Address address2 = new Address("New York","6th Avenue");
        Address address3 = new Address("New York","7th Avenue");

        Employee employee1 = Employee.builder()
            .position("accountant")
            .addresses(Sets.newHashSet(address1))
            .build();
        Employee employee2 = Employee.builder()
            .position("accountant")
            .addresses(Sets.newHashSet(address2, address3))
            .build();

        DiffNode diffNode = objectDiffer.compare(employee1, employee2);

        List<String> differences = new ArrayList<>();
        if (diffNode.isChanged()) {
            diffNode.visit((node, visit) -> {
                if (node.hasChanges() && !node.isRootNode() && !node.hasChildren()) {
                    Object expectedValue = node.canonicalGet(employee1);
                    Object actualValue = node.canonicalGet(employee2);
                    String message = String.format(
                        FIELD_NAME_EXPECTED_AND_ACTUAL_VALUE, node.getPropertyName(), expectedValue, actualValue);
                    differences.add(message + StringUtils.LF);
                }
            });
        }
        Assertions.assertThat(differences).containsExactlyInAnyOrder(
            "city -> ExpectedValue [New Jersey] ActualValue [null]\n",
            "streetName -> ExpectedValue [5th Avenue] ActualValue [null]\n",
            "city -> ExpectedValue [null] ActualValue [New York]\n",
            "streetName -> ExpectedValue [null] ActualValue [6th Avenue]\n",
            "city -> ExpectedValue [null] ActualValue [New York]\n",
            "streetName -> ExpectedValue [null] ActualValue [7th Avenue]\n"
        );
        System.out.println(differences);
    }

    private static ObjectDiffer initObjectDiffer() {
        ObjectDifferBuilder builder = MyCustomObjectDifferBuilder.createStringObjectDifferBuilder();
        builder.inclusion().exclude(); // NOTE: can exclude some props' comparison here
        return builder.build();
    }

}
