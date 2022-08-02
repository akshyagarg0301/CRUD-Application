package com.example.democrud.Repository;
// this class talks with database

import com.example.democrud.CacheStore;
import com.example.democrud.entity.Newspaper;
import com.example.democrud.entity.NewspaperList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


@Repository

public class DaoForNewsPaper {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public static final String HASH_KEY="Newspaper";
    @Autowired
    RedisTemplate redisTemplate;



    @Autowired
    CacheStore<Newspaper> newspaperCacheStore;

    class NewsRowMapper implements RowMapper<Newspaper> {
        @Override
        public Newspaper mapRow(ResultSet rs, int rowNum) throws SQLException {
            Newspaper newspaper = new Newspaper();
            newspaper.setDate(rs.getDate("Published_Date"));
            newspaper.setNewspprID(rs.getInt("NewspprID"));
            newspaper.setCategoryID(rs.getInt("CategoryID"));
            newspaper.setName(rs.getString("Name"));
            newspaper.setLanguage(rs.getString("Language"));
            return newspaper;
        }


    }





    public NewspaperList findAll() {


//        if(redisTemplate.hasKey(HASH_KEY)==false)
//        {
            NewspaperList newspaperList=new NewspaperList(jdbcTemplate.query("select * from NEWSPAPER", new NewsRowMapper()));

            System.out.println("getting from db");
            //redisTemplate.opsForHash().put(HASH_KEY,"map",newspaperList.toString());
            return newspaperList;
//        }
//        //System.out.println("found from Redis");
//        NewspaperList newspaperList= new NewspaperList(redisTemplate.opsForHash().values(HASH_KEY));
//        return newspaperList;



    }

    public Newspaper findByID(int newsID, int categID) {

        String  newsID_categID=String.valueOf(newsID)+"_"+String.valueOf(categID);
        Newspaper newspaper=newspaperCacheStore.get(newsID_categID);
        if(newspaper!=null)
        {

            System.out.println("Getting from Guava");
            return newspaper;
        }
        else{
            if(redisTemplate.opsForHash().get(HASH_KEY,newsID_categID)==null)
            {
                System.out.println("getting from db");
                Newspaper newspaper1=(Newspaper)jdbcTemplate.queryForObject("select * from NEWSPAPER where CategoryID=? and NewspprID=?", new Object[]{categID, newsID}, new NewsRowMapper());
                //redisTemplate.opsForHash().put(HASH_KEY,newsID_categID,newspaper1);
                redisTemplate.opsForValue().set(newsID_categID,newspaper1);
                redisTemplate.expire(newsID_categID,20  , TimeUnit.SECONDS);
                newspaperCacheStore.add(newsID_categID,newspaper1);
                return newspaper1;
            }
            else
            {
                if(newspaperCacheStore.get(newsID_categID)==null)
                {
                    System.out.println("getting from redis");
                    Newspaper newspaper1=(Newspaper) redisTemplate.opsForHash().get(HASH_KEY,newsID_categID);

                    newspaperCacheStore.add(newsID_categID,newspaper1);

                    return newspaper1;
                }
                else
                {
                    return newspaperCacheStore.get(newsID_categID);
                }

            }
        }
    }

    public String deletebyID(int newsID, int categID) {

        String  newsID_categID=String.valueOf(newsID)+"_"+String.valueOf(categID);
        int a=jdbcTemplate.update("delete from NEWSPAPER where NewspprID=? and CategoryID=?", newsID, categID);
        if(a==1)
        {

            if(redisTemplate.opsForHash().get(HASH_KEY,newsID_categID)!=null) {

                redisTemplate.opsForHash().delete(HASH_KEY, newsID_categID);
                if(newspaperCacheStore.get(newsID_categID)!=null)
                {
                    newspaperCacheStore.delete(newsID_categID);
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


    public String insert(Newspaper newspaper) {

        if (jdbcTemplate.update("INSERT INTO NEWSPAPER VALUES(?,?,?,?,?)", newspaper.getName(), newspaper.getDate(), newspaper.getLanguage(), newspaper.getCategoryID(), newspaper.getNewspprID())>0)
            return "Inserted Successfully in DB";
        else
            return "Operation Not Successful";
    }



    public String update(Newspaper newspaper, int newsID, int categID) {
        String  newsID_categID=String.valueOf(newsID)+"_"+String.valueOf(categID);
        if(redisTemplate.opsForHash().get(HASH_KEY,newsID_categID)!=null)
        {

            if(newspaperCacheStore.get(newsID_categID)!=null)
            {
                jdbcTemplate.update("update NEWSPAPER set Name=?,Published_Date=?,Language=? where NewspprID=? and CategoryID=?", newspaper.getName(), newspaper.getDate(), newspaper.getLanguage(), newsID, categID);
                Newspaper newspaper1 = (Newspaper) jdbcTemplate.queryForObject("select * from NEWSPAPER where CategoryID=? and NewspprID=?", new Object[]{categID, newsID}, new NewsRowMapper());
                redisTemplate.opsForHash().put(HASH_KEY, newsID_categID, newspaper1);
                newspaperCacheStore.add(newsID_categID,newspaper1);
                return "Updated in DB,Redis and Guava";
            }
            else
            {
                redisTemplate.opsForHash().delete(HASH_KEY,newsID_categID);
                jdbcTemplate.update("update NEWSPAPER set Name=?,Published_Date=?,Language=? where NewspprID=? and CategoryID=?", newspaper.getName(), newspaper.getDate(), newspaper.getLanguage(), newsID, categID);
                Newspaper newspaper1 = (Newspaper) jdbcTemplate.queryForObject("select * from NEWSPAPER where CategoryID=? and NewspprID=?", new Object[]{categID, newsID}, new NewsRowMapper());
                redisTemplate.opsForHash().put(HASH_KEY, newsID_categID, newspaper1);
                return "Updated in DB and Redis";
            }
        }
        else
        {
            jdbcTemplate.update("update NEWSPAPER set Name=?,Published_Date=?,Language=? where NewspprID=? and CategoryID=?", newspaper.getName(), newspaper.getDate(), newspaper.getLanguage(),newsID,categID);
            return "Updated in DB";
        }


    }
}
