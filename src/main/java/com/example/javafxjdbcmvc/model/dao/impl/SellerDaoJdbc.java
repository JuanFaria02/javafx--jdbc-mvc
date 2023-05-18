package com.example.javafxjdbcmvc.model.dao.impl;

import com.example.javafxjdbcmvc.db.DB;
import com.example.javafxjdbcmvc.db.exceptions.DbException;
import com.example.javafxjdbcmvc.model.dao.SellerDao;
import com.example.javafxjdbcmvc.model.entities.Department;
import com.example.javafxjdbcmvc.model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDaoJdbc implements SellerDao {
    private Connection connection;
    public SellerDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Seller seller) {
        PreparedStatement st = null;

        try{
            st = connection.prepareStatement("INSERT INTO seller\n" +
                    "(Name, Email, BirthDate, BaseSalary, DepartmentId)\n" +
                    "VALUES\n" +
                    "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            st.setString(1, seller.getName());
            st.setString(2, seller.getEmail());
            st.setDate(3, Date.valueOf(seller.getBirthDate()));
            st.setDouble(4, seller.getBaseSalary());
            st.setInt(5, seller.getDepartment().getId());

            int result = st.executeUpdate();
            if (testExecuteQuery(result)) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    seller.setId(rs.getInt(1));

                }
                DB.closeResultSet(rs);

            }
            else {
                throw new DbException("Error. Insert didn't conclude");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }

    private boolean testExecuteQuery(Integer result) {
        if (result == 0){
            return false;
        }
        return true;
    }

    @Override
    public void update(Seller seller) {
        PreparedStatement st = null;

        try{
            st = connection.prepareStatement("UPDATE seller\n" +
                    "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?\n" +
                    "WHERE Id = ?");

            st.setString(1, seller.getName());
            st.setString(2, seller.getEmail());
            st.setDate(3, Date.valueOf(seller.getBirthDate()));
            st.setDouble(4, seller.getBaseSalary());
            st.setInt(5, seller.getDepartment().getId());
            st.setInt(6, seller.getId());
            st.executeUpdate();
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
            st = connection.prepareStatement("DELETE FROM seller\n" + "WHERE Id = ?");

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
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT seller.*,department.Name as DepName\n" +
                    "FROM seller INNER JOIN department\n" +
                    "ON seller.DepartmentId = department.Id\n" +
                    "WHERE seller.Id = ?");
            st.setInt(1, id);

            rs = st.executeQuery();

            if (rs.next()) {
                return new Seller(rs.getInt("Id"), rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getDate("BirthDate").toLocalDate(),
                        rs.getDouble("BaseSalary"),
                        new Department(rs.getInt("DepartmentId"), rs.getString("DepName"))
                );
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
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT seller.*,department.Name as DepName\n" +
                    "FROM seller INNER JOIN department\n" +
                    "ON seller.DepartmentId = department.Id\n" +
                    "ORDER BY Name");


            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = new Department(rs.getInt("DepartmentId"), rs.getString("DepName"));
                    map.put(rs.getInt("Id"), dep);
                }

                list.add(new Seller(rs.getInt("Id"), rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getDate("BirthDate").toLocalDate(),
                        rs.getDouble("BaseSalary"),
                        dep
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }


    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement("SELECT seller.*,department.Name as DepName\n" +
                    "FROM seller INNER JOIN department\n" +
                    "ON seller.DepartmentId = department.Id\n" +
                    "WHERE DepartmentId = ?\n" +
                    "ORDER BY Name");
            st.setInt(1, department.getId());

            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId"));
                if (dep == null) {
                    dep = new Department(rs.getInt("DepartmentId"), rs.getString("DepName"));
                    map.put(rs.getInt("Id"), dep);
                }

                list.add(new Seller(rs.getInt("Id"), rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getDate("BirthDate").toLocalDate(),
                        rs.getDouble("BaseSalary"),
                        dep
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }

}
