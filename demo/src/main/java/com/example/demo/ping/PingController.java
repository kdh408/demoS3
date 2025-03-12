package com.example.demo.ping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Controller
public class PingController {

    @GetMapping("/ping")
    public String pingTest(@RequestParam(name = "url", required = false) String url, Model model) {
        if (url != null && !url.isEmpty()) {

            url = url.replaceAll("^(https?://)", "").split("/")[0];

            try {
                //linux : ping -c 4
                ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "4", url);
                Process process = processBuilder.start();

                //window:MS949  linux:UTF_8
                //Charset encoding = isWindows ? Charset.forName("MS949") : StandardCharsets.UTF_8;
                //Charset encoding = Charset.forName("MS949");
                Charset encoding = StandardCharsets.UTF_8;

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),encoding));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                reader.close();
                model.addAttribute("result", output.toString());
            } catch (Exception e) {
                model.addAttribute("result", "Ping failed: " + e.getMessage());
            }
        }
        return "pingtest";
    }
}

