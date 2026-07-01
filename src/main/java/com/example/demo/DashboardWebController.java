package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class DashboardWebController {

    private final RegistroRepository registroRepository;

    public DashboardWebController(RegistroRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {
        long total = registroRepository.count();
        List<Registro> todos = registroRepository.findAll();
        List<Registro> ultimosCinco = todos.stream()
                .skip(Math.max(0, todos.size() - 5))
                .toList();

        model.addAttribute("total", total);
        model.addAttribute("ultimos", ultimosCinco);

        return "dashboard";
    }
}
