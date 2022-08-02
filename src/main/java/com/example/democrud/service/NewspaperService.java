package com.example.democrud.service;

import com.example.democrud.entity.Newspaper;
import com.example.democrud.entity.NewspaperList;
import com.example.democrud.Repository.DaoForNewsPaper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@Service
public class NewspaperService  {

    private static final Logger logger =
            LoggerFactory.getLogger(NewspaperService.class);


    @Autowired
    DaoForNewsPaper daoForNewsPaper;

    @Autowired
    private KafkaTemplate<String,Newspaper> kafkaTemplate;

//    @KafkaListener(topics = "Newspaper", groupId = "group_id")
//    public void kafkaListener1(Newspaper newspaper)
//    {
//        System.out.println("Messsage recieved from Consumer");
//        while(true)
//        {
//            ConsumerRecords<String,String> records=consumerFactory.poll(Duration.ofMillis(100));
//
//            for(ConsumerRecord<String,String> record:records)
//            {
//                logger.info("Key: "+record.key()+", Value: "+record.value());
//                logger.info("partition: "+record.partition()+ ", Offset: "+record.offset());
//            }
//        }
//    }





    public NewspaperList getAll()
    {
        return daoForNewsPaper.findAll();
    }



    public Newspaper getByID(int newspprID, int categoryID)
    {
        return daoForNewsPaper.findByID(newspprID,categoryID);
    }


    public String delete(int newsID, int categID)
    {
        return daoForNewsPaper.deletebyID(newsID,categID);
    }


    public String insert ( Newspaper newspaper)
    {

        try {
                kafkaTemplate.send("Newspaper",newspaper);
                kafkaTemplate.flush();
            return "Successfully sent";
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
       // kafkaTemplate.flush();

       // return daoForNewsPaper.insert(newspaper);
    }


    public String update( Newspaper newspaper,  int newsID, int categID)
    {
        return  daoForNewsPaper.update(newspaper,newsID,categID);
    }

}
