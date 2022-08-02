package com.example.democrud.Repository;
import com.example.democrud.CacheStore;
import com.example.democrud.entity.Content;
import com.example.democrud.entity.ContentList;
import com.example.democrud.entity.Newspaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@RestController
@Repository
public class DaoForContent {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CacheStore<Content>contentCacheStore;

    public static final String HASH_KEY="Content";
    class ContentRowMapper implements RowMapper<Content>{
        @Override
        public Content mapRow(ResultSet rs, int rowNum) throws SQLException {
            Content content=new Content();

            content.setNewspprID(rs.getInt("NewspprID"));
            content.setCategoryID(rs.getInt("CategoryID"));
            content.setArticleID(rs.getInt("ArticleID"));
            content.setArticle(rs.getString("Article"));
            return content;
        }


    }
    public ContentList findAll()
    {
        return new ContentList(jdbcTemplate.query("select * from CONTENT", new DaoForContent.ContentRowMapper()));

    }

    public Content findByID(int ArticleID)
    {
        String  article_id=String.valueOf(ArticleID);
        Content content=contentCacheStore.get(article_id);
        if(content!=null)
        {

            System.out.println("Getting from Guava");
            return content;
        }
        else {
            if (redisTemplate.opsForHash().get(HASH_KEY, article_id) == null) {
                System.out.println("getting from db");
                Content content1 = (Content) jdbcTemplate.queryForObject("select * from CONTENT where ArticleID=?", new Object[]{ArticleID}, new DaoForContent.ContentRowMapper());
                redisTemplate.opsForHash().put(HASH_KEY, article_id, content1);
                redisTemplate.expire(HASH_KEY, 100, TimeUnit.SECONDS);
                contentCacheStore.add(article_id, content1);
                return content1;
            } else {
                if (contentCacheStore.get(article_id) == null) {
                    System.out.println("getting from redis");
                    Content content1 = (Content) redisTemplate.opsForHash().get(HASH_KEY, article_id);

                    contentCacheStore.add(article_id, content1);

                    return content1;
                } else {
                    return contentCacheStore.get(article_id);
                }

            }
        }

    }

    public String deletebyID( int ArticleID)
    {
        String article_id=String.valueOf(ArticleID);
        int a=jdbcTemplate.update("delete from CONTENT where ArticleID=? ",ArticleID);
        if(a==1)
        {

            if(redisTemplate.opsForHash().get(HASH_KEY,article_id)!=null) {

                redisTemplate.opsForHash().delete(HASH_KEY, article_id);
                if(contentCacheStore.get(article_id)!=null)
                {
                    contentCacheStore.delete(article_id);
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


    public String insert(Content content){
        if(jdbcTemplate.update("insert into CONTENT VALUES(?,?,?,?)",content.getArticleID(),content.getNewspprID(),content.getCategoryID(),content.getArticle())>0)
            return "Inserted Successfully in DB";
        else
        {
            return "Operation Not Successful";
        }
    }


    public String update(Content content,int ArticleId)
    {
        String  article_id=String.valueOf(ArticleId);
        if(redisTemplate.opsForHash().get(HASH_KEY,ArticleId)!=null)
        {

            if(contentCacheStore.get(article_id)!=null)
            {
                jdbcTemplate.update("update CONTENT set NewspprID=?,CategoryID=?,Article=? where ArticleID=? ",content.getNewspprID(),content.getCategoryID(),content.getArticle(),ArticleId);
                Content content1 = (Content) jdbcTemplate.queryForObject("select * from CONTENT where ArticleID=?", new Object[]{ArticleId}, new DaoForContent.ContentRowMapper());
                redisTemplate.opsForHash().put(HASH_KEY, article_id, content1);
                contentCacheStore.add(article_id,content1);
                return "Updated in DB,Redis and Guava";
            }
            else
            {
                redisTemplate.opsForHash().delete(HASH_KEY,article_id);
                jdbcTemplate.update("update CONTENT set NewspprID=?,CategoryID=?,Article=? where ArticleID=? ",content.getNewspprID(),content.getCategoryID(),content.getArticle(),ArticleId);
                Content content1 = (Content) jdbcTemplate.queryForObject("select * from CONTENT where ArticleID=?", new Object[]{ArticleId}, new DaoForContent.ContentRowMapper());
                redisTemplate.opsForHash().put(HASH_KEY,article_id, content1);
                return "Updated in DB and Redis";
            }
        }
        else
        {
            jdbcTemplate.update("update CONTENT set NewspprID=?,CategoryID=?,Article=? where ArticleID=? ",content.getNewspprID(),content.getCategoryID(),content.getArticle(),ArticleId);
            return "Updated in DB";
        }


    }
}
