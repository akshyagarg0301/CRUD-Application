package com.example.democrud.service;

import com.example.democrud.Consumer;
import com.example.democrud.entity.Newspaper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Service
public class kafkaConsumerService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @PostConstruct
    public void init()
    {
        new KafkaConsumer(jdbcTemplate).start();
    }

}

class KafkaConsumer extends Thread
{

    JdbcTemplate jdbcTemplate;
    public KafkaConsumer(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate=jdbcTemplate;
    }

    Logger logger= LoggerFactory.getLogger(Consumer.class);
    @Override
    public void run()
    {

        String groupID = "030";
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        //create consumer
        org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(properties);
        //subscribe consumer to our topic
        consumer.subscribe(Arrays.asList("Newspaper"));
        logger.info("Kafkaconsumer started");
        //poll for new data
        while (true) {
            long start=System.currentTimeMillis();
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(60000));
            //System.out.println(System.currentTimeMillis()-start);
            for (ConsumerRecord<String, String> record : records) {
                logger.info("Key: " + record.key() + "Value " + record.value());
                logger.info("partition: " + record.partition() + ", Offset: " + record.offset());
                //System.out.println(record.value().getClass().getSimpleName());
                ObjectMapper mapper = new ObjectMapper();
                Newspaper newspaper = null;
                try {
                    newspaper = mapper.readValue(record.value(), Newspaper.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //System.out.println(newspaper.getNewspprID());


                try {

                    jdbcTemplate.update("INSERT INTO NEWSPAPER VALUES(?,?,?,?,?)", newspaper.getName(), newspaper.getDate(), newspaper.getLanguage(), newspaper.getCategoryID(), newspaper.getNewspprID());
                    System.out.println("Inserted in DB");
                } catch (Exception e) {
                    System.out.println("error occurred");
                }

            }



        }
    }}