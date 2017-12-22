package com.vladmeh.hibernate;

import com.vladmeh.hibernate.Entity.Category;
import org.hibernate.Session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @autor mvl on 21.12.2017.
 */
public class App {
    public static void main(String[] args) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List result = session.createQuery("from Category").list();

        for (Category category: (List<Category>) result){
            String parent = (category.getParentCategory() != null)?category.getParentCategory().getName():null;
            System.out.println("Category: " + category.getName() + " -> parent category: " + parent);
        }

        session.getTransaction().commit();
        session.close();

        HibernateUtil.shutdown();
    }


    public static void createCategory(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Category category = new Category("Category3");

        Set<Category> subCategories = new HashSet<Category>();

        subCategories.add(new Category("Sub Category 1", category));
        subCategories.add(new Category("Sub Category 2", category));
        subCategories.add(new Category("Sub Category 3", category));

        category.setCategories(subCategories);

        /*List<Product> products = new ArrayList<Product>();

        products.add(new Product("Product_1"));
        products.add(new Product("Product_2"));
        products.add(new Product("Product_3"));
        products.add(new Product("Product_4"));

        category.setProducts(products);*/

        session.save(category);

        for (Category subCategory: subCategories)
            session.save(subCategory);

        session.getTransaction().commit();
        session.close();

        HibernateUtil.shutdown();
    }
}
