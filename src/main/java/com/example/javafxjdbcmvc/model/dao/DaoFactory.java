package com.example.javafxjdbcmvc.model.dao;

import com.example.javafxjdbcmvc.db.DB;
import com.example.javafxjdbcmvc.model.dao.impl.DepartmentDaoJdbc;
import com.example.javafxjdbcmvc.model.dao.impl.SellerDaoJdbc;

public class DaoFactory {
    public static DepartmentDao createDepartmentDao(){
        return new DepartmentDaoJdbc(DB.getConnection());
    }

    public static SellerDao createSellerDao(){
        return new SellerDaoJdbc(DB.getConnection());
    }
}
