package com.example.democrud.controllers;

import com.example.democrud.entity.Newspaper;
import com.example.democrud.entity.NewspaperList;
import com.example.democrud.service.NewspaperService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
public class NewspaperController {
    @Autowired
    NewspaperService newspaperService;
    @Autowired
    JdbcTemplate jdbcTemplate;


    Logger logger= LoggerFactory.getLogger(NewspaperController.class);

    @GetMapping("/")
    public String Home()
    {
        return "Home Page";
    }
    @GetMapping("/newspaper/getAll")
    public NewspaperList getAll()
    {
        return newspaperService.getAll();
    }

    @GetMapping("/newspaper/getByID/{newspprID}/{categoryID}")
    public Newspaper getByID(@PathVariable int newspprID, @PathVariable int categoryID)
    {


        Newspaper newspaperFromService=newspaperService.getByID(newspprID,categoryID);
        return newspaperFromService;
    }
    @DeleteMapping("/newspaper/delete/{newsID}/{categID}")

    public String delete(@PathVariable int newsID, @PathVariable int categID)
    {
        return newspaperService.delete(newsID,categID);
    }

    @PostMapping("/newspaper/insert")
    public String insert (@RequestBody Newspaper newspaper)
    {
        try {
            return newspaperService.insert(newspaper);

        }catch (Exception e)
        {
            e.printStackTrace();
            return "fat gaya";
        }

    }


//    @KafkaListener(topics = "Newspaper" ,groupId="Newspaper")
//    public void listener1(String newspapers) throws JsonProcessingException {
//
//            ObjectMapper mapper = new ObjectMapper();
//            Newspaper newspaper = mapper.readValue(newspapers, Newspaper.class);
//            System.out.println(newspaper.getName());
//            //logger.info("Key: "+newspaper.get(i).getName());
//            try{
//                jdbcTemplate.update("INSERT INTO NEWSPAPER VALUES(?,?,?,?,?)", newspaper.getName(), newspaper.getDate(), newspaper.getLanguage(), newspaper.getCategoryID(), newspaper.getNewspprID());
//                System.out.println("Successfully inserted into DB");
//            }
//            catch (Exception e)
//            {
//                System.out.println("error occurred");
//            }
//
//
//    }





    @PutMapping("/newspaper/update/{newsID}/{categID}")
    public String update(@RequestBody Newspaper newspaper, @PathVariable int newsID,@PathVariable int categID)
    {
       return  newspaperService.update(newspaper,newsID,categID);
    }

}
