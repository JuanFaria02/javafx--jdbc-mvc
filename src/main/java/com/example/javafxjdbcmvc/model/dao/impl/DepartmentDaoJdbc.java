package com.example.javafxjdbcmvc.model.dao.impl;

import com.example.javafxjdbcmvc.db.DB;
import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.model.dao.DepartmentDao;
import com.example.javafxjdbcmvc.model.entities.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJdbc implements DepartmentDao {
    private Connection connection;

    public DepartmentDaoJdbc(Connection connection){
        this.connection = connection;
    }


    @Override
    public void insert(Department department) {
        PreparedStatement st = null;
        try{
            st = connection.prepareStatement("INSERT INTO department " +
                    "(Name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, department.getName());
            int result = st.executeUpdate();
            if (testExecuteQuery(result)) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    department.setId(rs.getInt(1));

                }
                DB.closeResultSet(rs);
            }
            else {
                throw new DbException("Error. Insert didn't conclude");
            }
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }

    private boolean testExecuteQuery(int result){
        if (result == 0){
            return false;
        }
        return true;
    }
    @Override
    public void update(Department department) {
        PreparedStatement st = null;

        try {
            st = connection.prepareStatement("UPDATE department " +
                    "SET Name = ? " +
                    "WHERE Id = ?");
            st.setString(1, department.getName());
            st.setInt(2, department.getId());
            int result = st.executeUpdate();
            if (!testExecuteQuery(result)) {
                throw new DbException("Error. Id doesn't exist");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;

        try{
            st = connection.prepareStatement("DELETE FROM department " +
                    "WHERE Id = ?");

            st.setInt(1, id);
            int result = st.executeUpdate();
            if (!testExecuteQuery(result)){
                throw new DbException("Id doesn't exist");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT department.* "+
                    "FROM department " +
                    "WHERE department.Id = ?");
            st.setInt(1, id);

            rs = st.executeQuery();

            if (rs.next()) {
                return new Department(rs.getInt("Id"), rs.getString("Name"));
            }
            return null;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }


    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = connection.prepareStatement("SELECT * FROM DEPARTMENT");
            rs = st.executeQuery();

            List<Department> departmentList = new ArrayList<>();
            while (rs.next()) {
                departmentList.add(new Department(rs.getInt("Id"), rs.getString("Name")));
            }
            return departmentList;
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

}
