package com.example.javafxjdbcmvc.model.dao;

import com.example.javafxjdbcmvc.model.entities.Department;
import com.example.javafxjdbcmvc.model.entities.Seller;

import java.util.List;

public interface SellerDao {
    void insert(Seller seller);
    void update(Seller seller);
    void deleteById(Integer id);
    Seller findById(Integer id);
    List<Seller> findAll();
    List<Seller> findByDepartment(Department department);
}
