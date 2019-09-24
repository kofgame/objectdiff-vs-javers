package com.dreamCompany.javers;

import com.dreamCompany.model.Address;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Andrey on 24.09.2019.
 */
public class BasicValueChangeExample {

    @Test
    public void shouldCaptureValueChange() {
        //given
        Javers javers = JaversBuilder.javers().build();

        Address address1 = new Address("New York", "5th Avenue");
        Address address2 = new Address("New York","6th Avenue");

        //compare
        Diff diff = javers.compare(address1, address2);

        //there should be one change of type {@link ValueChange}
        ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

        assertThat(diff.getChanges()).hasSize(1);
        assertThat(change.getAffectedGlobalId().value())
                .isEqualTo("com.dreamCompany.model.Address/");

        assertThat(change.getPropertyName()).isEqualTo("streetName");
        assertThat(change.getLeft()).isEqualTo("5th Avenue");
        assertThat(change.getRight()).isEqualTo("6th Avenue");

        System.out.println(diff);
    }

}
