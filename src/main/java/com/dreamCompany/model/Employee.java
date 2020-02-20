package com.dreamCompany.model;

import java.util.Collections;
import java.util.Set;

import org.javers.core.metamodel.annotation.Entity;

import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Wither;

@Data
@Builder
@AllArgsConstructor
@Entity // notice javers metadata
public class Employee {

    public Employee(final String name, final String position, final int salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

//    public Employee(final String name, final String position, final int salary, final Set<Address> addresses) {
//        this.name = name;
//        this.position = position;
//        this.salary = salary;
//        this.addresses = addresses;
//    }

    @org.javers.core.metamodel.annotation.Id // notice javers metadata
    private String name;
    private String position;

    private int salary;

    @Wither
    private Set<Address> addresses = Sets.newHashSet();

    public void addAddresses(final Address ... addresses) {
        Collections.addAll(this.addresses, addresses);
    }

}
