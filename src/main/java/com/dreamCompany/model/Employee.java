package com.dreamCompany.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class Employee {

    private String name;
    private String position;

    private int salary;

    private Set<Address> addresses;

}
