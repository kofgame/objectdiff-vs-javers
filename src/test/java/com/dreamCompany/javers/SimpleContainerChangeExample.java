package com.dreamCompany.javers;

import com.dreamCompany.model.Address;
import com.dreamCompany.model.Employee;
import com.google.common.collect.Sets;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.CollectionChange;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Andrey on 24.09.2019.
 */
public class SimpleContainerChangeExample {

    @Test
    public void shouldCaptureContainerChange() {
        // given
        Javers javers = JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
                .build();

        Address address1 = new Address("New York", "5th Avenue");
        Address address2 = new Address("New York","6th Avenue");
        Address address3 = new Address("New York","7th Avenue");

        Employee employee1 = Employee.builder().name("employee1")
                .position("accountant")
                .addresses(Sets.newHashSet(address1))
                .build();
        Employee employee2 = Employee.builder().name("employee2")
                .position("accountant")
                .addresses(Sets.newHashSet(address2, address3))
                .build();

        // compare
        Diff diff = javers.compare(employee1, employee2);

        //there should be one change of ValueChange type & 3 changes of CollectionChange type
        ValueChange valueChange = diff.getChangesByType(ValueChange.class).get(0);
        CollectionChange containerChange = diff.getChangesByType(CollectionChange.class).get(0);

        assertThat(diff.getChanges()).hasSize(5);
        assertThat(valueChange.getPropertyName()).isEqualTo("name");

        System.out.println("<--- Changes: \n" + diff.getChanges() + " \n --->");
        assertThat(containerChange.getChanges().size()).isEqualTo(3);
        assertThat(containerChange.getAddedValues().size()).isEqualTo(2);
        assertThat(containerChange.getRemovedValues().size()).isEqualTo(1);
    }

}
