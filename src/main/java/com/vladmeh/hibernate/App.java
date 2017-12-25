package com.vladmeh.hibernate;

import com.vladmeh.hibernate.Entity.Category;
import org.hibernate.Session;

import java.util.List;

/**
 * @autor mvl on 21.12.2017.
 */
public class App {
    public static void main(String[] args) {

        List result = listCategory();

        for (Category category: (List<Category>) result){
            String parent = (category.getParentCategory() != null)
                    ? String.valueOf(category.getParentCategory().getId()):
                    null;
            System.out.println("Category: " + category.getName() + " -> parent category: " + parent);
        }
    }

    private static List listCategory(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List result = session.createQuery("from Category").list();

        session.getTransaction().commit();
        session.close();
        HibernateUtil.shutdown();

        return result;
    }
}
