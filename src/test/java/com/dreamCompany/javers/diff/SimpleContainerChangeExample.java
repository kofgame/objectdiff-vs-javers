package com.dreamCompany.javers.diff;

import static org.assertj.core.api.Assertions.assertThat;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.junit.Test;

import com.dreamCompany.model.Address;
import com.dreamCompany.model.Employee;
import com.google.common.collect.Sets;

/**
 * Created by Andrey on 24.09.2019.
 */
public class SimpleContainerChangeExample {

    @Test
    public void shouldCaptureNewAndRemovedChanges() {
        // given
        Javers javers = JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .build();

        Address address1 = new Address("New York", "5th Avenue");
        Address address2 = new Address("New York","6th Avenue");
        Address address3 = new Address("New York","7th Avenue");

        Employee employee1 = new Employee("employee1", "accountant", 5000, Sets.newHashSet(address1));
        Employee employee2 = new Employee("employee2", "accountant", 5000, Sets.newHashSet(address2, address3));

        // compare
        Diff diff = javers.compare(employee1, employee2);

        assertThat(diff.getChanges()).hasSize(5);
        assertThat(diff.getChanges())
            .hasOnlyElementsOfTypes(
                NewObject.class,        // 2 items - address2 & address3
                ObjectRemoved.class
            );
        System.out.println("<--- Changes: \n" + diff.getChanges() + " \n --->");
    }


    @Test
    public void shouldCaptureContainerChange() {
        // given
        Javers javers = JaversBuilder.javers()
            .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
            .build();

        Address address1 = new Address("New Orlean", "5th Avenue");
        Address address2 = new Address("New York","6th Avenue");
        Address address3 = new Address("New Jersey","7th Avenue");

        // compare
        Diff diff = javers.compareCollections(
            Sets.newHashSet(address1),
            Sets.newHashSet(address2, address3),
            Address.class);

        // there should be 2 changes of NewObject type & 1 change of CollectionChange type
        CollectionChange containerChange = diff.getChangesByType(CollectionChange.class).get(0);

        assertThat(diff.getChanges()).hasSize(4);
        assertThat(diff.getChanges())
            .hasOnlyElementsOfTypes(
                NewObject.class,        // 2 items - address2 & address3
                ObjectRemoved.class,    // 1 item - address1
                SetChange.class         // 'set' collection change
            );

        System.out.println("<--- Changes: \n" + diff.getChanges() + " \n --->");
        assertThat(containerChange.getChanges().size()).isEqualTo(3);
        assertThat(containerChange.getAddedValues().size()).isEqualTo(2);
        assertThat(containerChange.getRemovedValues().size()).isEqualTo(1);
    }

}
