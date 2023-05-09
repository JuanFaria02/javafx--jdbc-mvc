package com.example.javafxjdbcmvc.model.services;

import com.example.javafxjdbcmvc.model.entities.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {
    public List<Department> findAll(){
        List<Department> list = new ArrayList<>();
        list.add(new Department(1, "Vendas"));
        list.add(new Department(2, "Livros"));
        return list;
    }
}
