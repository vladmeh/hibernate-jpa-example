package com.vladmeh.hibernate;

import com.vladmeh.hibernate.Entity.Category;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @autor mvl on 22.12.2017.
 */
public class AppTest extends TestCase {
    private SessionFactory sessionFactory;
    @Override
    protected void setUp() throws Exception {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }

    @SuppressWarnings({ "unchecked" })
    public void testGetCategory(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List result = session.createQuery("from Category").list();

        for (Category category: (List<Category>) result){
            String parent = (category.getParentCategory() != null)?category.getParentCategory().getName():null;
            System.out.println("Category: " + category.getName() + " -> parent category: " + parent);
        }

        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings({ "unchecked" })
    public void testCriteriaQuery(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Category> query = builder.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);

        query.select(root).where(builder.equal(root.get("name"), "Category"));
        Query<Category> q = session.createQuery(query);

        try {
            Category  category = q.getSingleResult();
            System.out.println(category.getId());
        } catch (NoResultException e) {
            System.out.println("No entity found for query");
        }

        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings({ "unchecked" })
    public void testFindNullParentCategory(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List result = session.createQuery("from Category where parentCategory is null ").list();

        for (Category category: (List<Category>) result){
            if (!category.getName().startsWith("Root")){
                category.setName("Root Category");
                //session.save(category);
            }
            System.out.println(category.getName());
        }

        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings({ "unchecked" })
    public void testFindCategoryAndUpdate(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();


        Category category = session.byId(Category.class).getReference(1L);
        System.out.println(category.getName());

        if (category.getName().equals("Category")){
            category.setName("Root Category");
            //session.save(category);
        }

        session.getTransaction().commit();
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();

        List result = session.createQuery("from Category where parentCategory is null ").list();

        for (Category subCategory: (List<Category>) result){
            if (subCategory.getName().startsWith("Sub")){
                subCategory.setParentCategory(category);
                //session.save(subCategory);
                System.out.println(subCategory.getName() + " -> " + subCategory.getParentCategory().getName());
            }
        }

        session.getTransaction().commit();
        session.close();


    }

    @SuppressWarnings({ "unchecked" })
    public void testAddSubCategories(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Category category = new Category("Root Category");

        Set<Category> subCategories = new HashSet<Category>();

        subCategories.add(new Category("Sub Category 1", category));
        subCategories.add(new Category("Sub Category 2", category));
        subCategories.add(new Category("Sub Category 3", category));

        category.setCategories(subCategories);

        session.save(category);

        for (Category subCategory: subCategories)
            session.save(subCategory);

        session.getTransaction().commit();
        session.close();

        session = sessionFactory.openSession();
        session.beginTransaction();

        List result = session.createQuery("from Category").list();

        for (Category cat: (List<Category>) result){
            String parent = (cat.getParentCategory() != null)?cat.getParentCategory().getName():null;
            System.out.println("Category: " + cat.getName() + " -> parent category: " + parent);
        }

        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings({ "unchecked" })
    public void testDeleteCategory(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            Category category = session.byId(Category.class).getReference(2L);
            session.delete(category);
            session.getTransaction().commit();
        } catch (EntityNotFoundException e) {
            System.out.println("No entity found for query");
            session.getTransaction().rollback();
        }

        session.close();
    }

    public void testUpdateCategory(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Category> categoryList = session.createQuery("from Category where parentCategory is null").list();

        int n = 0;
        for (Category category: (List<Category>) categoryList){
            if (category.getName().startsWith("Root")){
                category.setName("Root Category " + String.valueOf(++n));
            }
            System.out.println(category.getName());
        }

        session.getTransaction().commit();
        session.close();
    }
}
