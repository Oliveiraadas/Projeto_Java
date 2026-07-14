package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
public class DashboardWebController {

    private final RegistroRepository registroRepository;
    private final RegistroService registroService;

    public DashboardWebController(RegistroRepository registroRepository, RegistroService registroService) {
        this.registroRepository = registroRepository;
        this.registroService = registroService;
    }

    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) {
        long total = registroRepository.count();
        List<Registro> todos = registroRepository.findAll();

        model.addAttribute("total", total);
        model.addAttribute("ultimos", todos);

        return "dashboard";
    }

    @PostMapping("/dashboard/upload")
    public String importarArquivoUsuario(@RequestParam("file") MultipartFile file) {
        registroService.processarArquivoUsuario(file);
        return "redirect:/dashboard";
    }
}
