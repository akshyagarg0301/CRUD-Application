package com.example.democrud.Repository;

import com.example.democrud.CacheStore;
import com.example.democrud.entity.Category;
import com.example.democrud.entity.CategoryList;
import com.example.democrud.entity.Newspaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Repository
public class DaoForCategory {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public static final String HASH_KEY="Category";
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CacheStore<Category> categoryCacheStore;



    class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category category = new Category();

            category.setCategoryID(rs.getInt("CategoryID"));
            category.setCategoryName(rs.getString("Newsppr_Category"));

            return category;
        }
    }

        public CategoryList findAll()
        {
            System.out.println("getting from DB");
            return new CategoryList(jdbcTemplate.query("select * from CATEGORY", new CategoryRowMapper()));
        }

        public Category findByID(int CategoryID)
        {

            String category_id=String.valueOf(CategoryID);
            Category category=categoryCacheStore.get(category_id);
            if(category!=null)
            {

                System.out.println("Getting from Guava");
                return category;
            }
            else{
                if(redisTemplate.opsForHash().get(HASH_KEY,category_id)==null)
                {
                    System.out.println("getting from db");
                    Category category1=(Category) jdbcTemplate.queryForObject("select * from CATEGORY where CategoryID=?",new Object[]{CategoryID}, new CategoryRowMapper());
                    redisTemplate.opsForHash().put(HASH_KEY,category_id,category1);
                    redisTemplate.expire(HASH_KEY,100, TimeUnit.SECONDS);
                    categoryCacheStore.add(category_id,category1);
                    return category1;
                }
                else
                {
                    if(categoryCacheStore.get(category_id)==null)
                    {
                        System.out.println("getting from redis");
                        Category category1=(Category) redisTemplate.opsForHash().get(HASH_KEY,category_id);

                        categoryCacheStore.add(category_id,category1);

                        return category1;
                    }
                    else
                    {
                        return categoryCacheStore.get(category_id);
                    }

                }
            }

        }

        public String deletebyID( int CategoryID)
        {
            String  category_id=String.valueOf(CategoryID);
            int a=jdbcTemplate.update("delete from CATEGORY where CategoryID=? ",CategoryID);

            if(a==1)
            {

                if(redisTemplate.opsForHash().get(HASH_KEY,category_id)!=null) {

                    redisTemplate.opsForHash().delete(HASH_KEY,category_id);
                    if(categoryCacheStore.get(category_id)!=null)
                    {
                        categoryCacheStore.delete(category_id);
                        return "Deleted from DB,Redis and Guava";
                    }
                    else
                    {
                        return "Deleted from DB and redis";
                    }
                }
                else{
                    return "Deleted from DB only";
                }
            }
            else
            {
                return "Operation not successful";
            }


        }

        public String insert(Category category)
        {
            if(jdbcTemplate.update("insert into CATEGORY VALUES(?,?)",category.getCategoryID(),category.getCategoryName())>0)
                return "Inserted Successfully in DB";
            else
                return "Operation Not Successful";
        }

        public String update(Category category, int categoryID) {
            String  category_id=String.valueOf(categoryID);
            if(redisTemplate.opsForHash().get(HASH_KEY,category_id)!=null)
            {

                if(categoryCacheStore.get(category_id)!=null)
                {
                    jdbcTemplate.update("update CATEGORY set Newsppr_Category=? where CategoryID=?",category.getCategoryName(),categoryID);
                    Category category1 = (Category) jdbcTemplate.queryForObject("select * from CATEGORY where CategoryID=?",new Object[]{categoryID}, new CategoryRowMapper());
                    redisTemplate.opsForHash().put(HASH_KEY, category_id, category1);
                    categoryCacheStore.add(category_id,category1);
                    return "Updated in DB,Redis and Guava";
                }
                else
                {
                    redisTemplate.opsForHash().delete(HASH_KEY,category_id);
                    jdbcTemplate.update("update CATEGORY set Newsppr_Category=? where CategoryID=?",category.getCategoryName(),categoryID);
                    Category category1 = (Category) jdbcTemplate.queryForObject("select * from CATEGORY where CategoryID=?",new Object[]{categoryID}, new CategoryRowMapper());
                    redisTemplate.opsForHash().put(HASH_KEY, category_id, category1);
                    return "Updated in DB and Redis";
                }
            }
            else
            {
                jdbcTemplate.update("update CATEGORY set Newsppr_Category=? where CategoryID=?",category.getCategoryName(),categoryID);
                return "Updated in DB";
            }



        }
}
