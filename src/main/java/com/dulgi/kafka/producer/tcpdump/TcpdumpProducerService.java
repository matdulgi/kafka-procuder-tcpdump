package com.dulgi.kafka.producer.tcpdump;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

@Component
public class TcpdumpProducerService implements ApplicationRunner {
    final static Queue<String> msgQueue = new LinkedList<>();

    private final String topic;
    private final ProducerFactory<String, String> producerFactory;
    private final KafkaTemplate<String, String> template;
    private final String nic;
    Logger logger = LoggerFactory.getLogger(TcpdumpProducerService.class);

    public TcpdumpProducerService( ProducerFactory<String, String> producerFactory, KafkaTemplate<String, String> template
            , @Value("${app.kafka.producer.topic}") String topic
            , @Value("${app.tcpdump.nic}") String nic ) {
        this.producerFactory = producerFactory;
        this.template = template;
        this.topic = topic;
        this.nic = nic;
    }

    @Override
    public void run(ApplicationArguments args) {
        produce( runTcpdump() );

    }

    InputStream runTcpdump(){
        Process tcpdump = null;
        try {
            System.out.print("root password : ");
            String password = new String(System.console().readPassword());
            logger.info("NIC : " + nic);
            String[] cmd = {"/bin/bash", "-c", "echo " + password + " | sudo -S tmpdump -i " + nic};
//            String[] cmd = {"/bin/bash", "-c", "echo <password> | sudo -S tcpdump -i " + nic};
            tcpdump = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tcpdump.getInputStream();
    }

    private void produce(InputStream inputStream) {
        if (inputStream != null) {
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")) ) {
                while (true) {
                    String msg = reader.readLine();
                    if (!msg.equals("")){
                        this.template.send(new ProducerRecord(this.topic, "tcpdump", msg));
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
