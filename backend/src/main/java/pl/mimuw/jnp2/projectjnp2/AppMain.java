package pl.mimuw.jnp2.projectjnp2;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Component
public class AppMain extends RouteBuilder {

    String email;
    String password;

    @Override
    public void configure() throws Exception {

        from("file:magic_folder?recursive=true")
                .setHeader("From", constant("file@reporter.com"))
                .setHeader("Content-Type", constant("text/plain"))
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        File file = exchange.getIn().getBody(File.class);
                        Scanner scanner = new Scanner(file);
                        String body = scanner.useDelimiter("\\A").next();
                        scanner.close();

                        String fileName = file.getName();
                        StringBuilder extension = new StringBuilder();
                        int dot = fileName.indexOf(".");

                        if (dot != -1) {
                            for (int i = dot; i < fileName.length(); i++)
                                extension.append(fileName.charAt(i));
                        }

                        String thanks = "Thanks for using 'Magic Folder'!\n";
                        if (extension.toString().equals(".py"))
                            body = body + "\n#" + thanks;
                        else if (extension.toString().equals(".cpp") || extension.toString().equals(".cc") || extension.toString().equals(".java"))
                            body = body + "\n//" + thanks;
                        else if (extension.toString().equals(".asm"))
                            body = body + "\n;" + thanks;
                        else if (extension.toString().equals(".curr")) {
                            exchange.getIn().setHeader("Subject", "Your exchange rates have arrived!");
                            exchange.getIn().setBody(body);
                            return;
                        }
                        else if (extension.toString().equals("")){

                            StringBuilder toWhom = new StringBuilder();
                            StringBuilder subject = new StringBuilder();

                            int i;

                            for (i = 0; i < body.length(); i++) {
                                char currChar = body.charAt(i);
                                toWhom.append(currChar);
                                if (currChar == '\n')
                                    break;
                            }


                            for (++i; i < body.length(); i++) {
                                char currChar = body.charAt(i);
                                subject.append(currChar);
                                if (currChar == '\n')
                                    break;
                            }

                            if (!toWhom.toString().equals("") && !subject.toString().equals("")) {
                                StringBuilder actualBody = new StringBuilder();
                                for ( ; i < body.length(); i++)
                                    actualBody.append(body.charAt(i));

                                actualBody.append("\n//").append(thanks);

                                exchange.getIn().setHeader("Subject", subject.toString());
                                exchange.getIn().setHeader("to", toWhom.toString());
                                exchange.getIn().setBody(actualBody.toString());

                                return;
                            }
                        }

                        exchange.getIn().setHeader("Subject", String.format(("Your newest file: '%s' has arrived!"), fileName));
                        exchange.getIn().setBody(body);
                    }
                })
                .to(String.format("smtps://smtp.gmail.com?username=%s&password=%s&delete=false&unseen=true&delay=60000", email, password))
                .to("direct://zipper");

        from("direct:zipper")
                .to("file:visible")
                .marshal().zipFile().to("file:zip_out");

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/showFile")
                .get("/{fileName}")
                .to("direct:showFile");

        from("direct:showFile")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        String fileName = exchange.getIn().getHeader("fileName", String.class);
                        String body;
                        try {
                            File file = new File("/home/neo/IdeaProjects/project-jnp2/visible/" + fileName);
                            Scanner scanner = new Scanner(file);
                            body = scanner.useDelimiter("\\A").next();
                            scanner.close();
                        } catch (Exception e) {
                            body = "File not found!";
                        }
                        exchange.setProperty(Exchange.CHARSET_NAME, "UTF-8");
                        exchange.setProperty(Exchange.CONTENT_TYPE, "plain/text");
                        exchange.getIn().setBody(body);
                    }
                });
    }
}

