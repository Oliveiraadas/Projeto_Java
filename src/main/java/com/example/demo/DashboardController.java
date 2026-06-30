package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RegistroRepository registroRepository;


    public DashboardController(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @GetMapping
    public DashboardDTO buscarMetricas() {

        long total = registroRepository.count();

        List<Registro> todos = registroRepository.findAll();
        List<Registro> ultimoscinco = todos.stream()
                .skip(Math.max(0, todos.size() - 5))
                .toList();

        return new DashboardDTO(total, ultimoscinco);
    }
}
