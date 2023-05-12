package com.example.javafxjdbcmvc.model.services;

import com.example.javafxjdbcmvc.model.dao.DaoFactory;
import com.example.javafxjdbcmvc.model.dao.SellerDao;
import com.example.javafxjdbcmvc.model.entities.Seller;

import java.util.List;

public class SellerService {
    private SellerDao sellerDao = DaoFactory.createSellerDao();
    public List<Seller> findAll(){
        return sellerDao.findAll();
    }

    public void saveOrUpdate(Seller obj){
        if (obj.getId() == null){
            sellerDao.insert(obj);
        }
        else {
            sellerDao.update(obj);
        }
    }

    public void delete(Seller obj){
        sellerDao.deleteById(obj.getId());
    }
}
