package front.jnp2.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import front.jnp2.models.exchangeModel;
import front.jnp2.models.outputModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Controller
public class searchController {

    @RequestMapping(value="/")
    public ModelAndView main() {
        ModelAndView modelView = new ModelAndView();

        System.out.println("here here");
        modelView.setViewName("showOptions");

        return modelView;
    }

    @RequestMapping(value="/mail")
    public ModelAndView giveMailForm() {
        ModelAndView modelView = new ModelAndView();

        System.out.println("here here");
        modelView.setViewName("mail/writeMail");

        return modelView;
    }

    @RequestMapping(value="/backup")
    public ModelAndView sendFile() {
        ModelAndView modelView = new ModelAndView();

        modelView.setViewName("mail/sendFile");

        return modelView;
    }

    @RequestMapping(value="/fileSent")
    public ModelAndView sendFile(@RequestParam("target") File file) throws IOException {
        ModelAndView modelView = new ModelAndView();

        modelView.setViewName("mail/backupMade");

        Path path = Path.of("/home/neo/Desktop/" + file.getName());

        File newFile = new File("/home/neo/IdeaProjects/JNP2/backend/magic_folder/" + file.getName());

        if (!newFile.createNewFile()) {
            System.out.println("File failed to create.");
        }

        Files.writeString(newFile.toPath(), Files.readString(path));

        return modelView;
    }

    @RequestMapping(value="/mailSent")
    public ModelAndView sendMail(@RequestParam("recipient") String recipient,
                                 @RequestParam("subject") String subject,
                                 @RequestParam("body") String body) throws IOException {

        Date date = new Date();
        long timeNow = date.getTime();

        File file = new File("/home/neo/IdeaProjects/JNP2/backend/magic_folder/" + timeNow);

        ModelAndView modelView = new ModelAndView();

        modelView.setViewName("mail/mailSent");

        if (!file.createNewFile()) {
            System.out.println("File already exists.");
            return modelView;
        }

        FileWriter writer = new FileWriter(file);
        writer.write(recipient + "\n" + subject + "\n" + body);
        writer.close();

        return modelView;
    }

    @RequestMapping(value="/bank")
    public ModelAndView sendMail() {
        ModelAndView modelView = new ModelAndView();

        modelView.setViewName("bank/enterAmount");

        return modelView;
    }

    @RequestMapping(value="/showFile/{NAME}")
    public ModelAndView showFile(@PathVariable("NAME") String fileName) throws IOException {
        ModelAndView modelView = new ModelAndView();

        File file = new File("/home/neo/IdeaProjects/JNP2/backend/visible");

        File[] files = file.listFiles();

        modelView.setViewName("showFile/file");

        assert files != null;
        for (File currentFile: files) {
            if (currentFile.isFile() && currentFile.getName().equals(fileName)) {
                modelView.addObject("fileBody", Files.readString(currentFile.toPath()));
                return modelView;
            }
        }

        modelView.setViewName("error");

        return modelView;
    }

    double roundDown(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(4, RoundingMode.HALF_DOWN)
                .doubleValue();
    }

    @RequestMapping(value="/exchangeSent", method = RequestMethod.POST)
    public ModelAndView showExchanged(@RequestParam("currency") String currencyCode,
                                @RequestParam("amount") String amount)
            throws IOException {

        String urlString = "http://api.nbp.pl/api/exchangerates/rates/a/" +
                currencyCode + "?format=json";

        ModelAndView modelView = new ModelAndView();

        RestTemplate restTemplate = new RestTemplate();
        exchangeModel xchg;

        try {
            xchg = restTemplate.getForObject(urlString, exchangeModel.class);
        }
        catch (RestClientException e) {
            modelView.setViewName("error");
            return modelView;
        }

        assert xchg != null;
        double currencyRate = xchg.getRates().get(0).getMid();
        double toExchange;

        try {
            toExchange = roundDown(Double.parseDouble(amount));
        }
        catch (NumberFormatException e) {
            modelView.setViewName("error");
            return modelView;
        }

        outputModel output = new outputModel();
        output.setName(xchg.getCurrency());
        output.setCode(xchg.getCode());
        output.setRate(xchg.getRates().get(0).getMid());
        output.setOutput(roundDown(toExchange / currencyRate));

        ObjectMapper mapper = new ObjectMapper();

        Date date = new Date();
        long timeNow = date.getTime();

        File file = new File("/home/neo/IdeaProjects/JNP2/backend/magic_folder/" + timeNow + ".curr");

        modelView.setViewName("mail/currencySent");

        if (!file.createNewFile()) {
            System.out.println("File already exists.");
        }

        FileWriter writer = new FileWriter(file);
        writer.write(mapper.writeValueAsString(output));
        writer.close();

        return modelView;
    }
}
