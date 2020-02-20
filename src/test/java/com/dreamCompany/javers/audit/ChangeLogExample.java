package com.dreamCompany.javers.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.repository.jql.QueryBuilder;
import org.junit.Test;

import com.dreamCompany.model.Address;
import com.dreamCompany.model.Employee;
import com.google.common.collect.Sets;

/**
 * Created by Andrey on 24.09.2019.
 */
public class ChangeLogExample {

    @Test
    public void verifyCaptureChangeLog() {
        Javers javers = JaversBuilder.javers().build();

        // NOTE: for JAVERS to capture audit / changelog entities must be annotated with JAVERS metadata, like Entity, Id etc
        Employee bob = new Employee("Bob", "ProjectManager", 5_000);
        bob.withAddresses(Sets.newHashSet());
        javers.commit("hr.manager", bob);

        // do some changes and commit
        bob.setPosition("Developer");
        bob.setSalary(10_000);
        javers.commit("hr.director", bob);

        bob.addAddresses(
            new Address("New York", "5-th Avenue"),
            new Address("New York", "6-th Avenue")
        );
        javers.commit("hr.manager", bob);

        // capture changes:
        List<Change> changes = javers.findChanges(
            QueryBuilder.byInstanceId("Bob", Employee.class).build());

        assertThat(changes)
            .hasOnlyElementsOfTypes(
                SetChange.class,
                ValueChange.class      // 2 items: position & salary change
            );
        String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());

        // then:
        System.out.println("<-- JAVERS ChangeLog:\n" + changeLog + "--->");
    }

}
