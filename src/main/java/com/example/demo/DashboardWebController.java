package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardWebController {

    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DashboardWebController(RegistroRepository registroRepository, RegistroService registroService) {
        this.registroRepository = registroRepository;
        this.registroService = registroService;
    }

    @GetMapping("/dashboard")
    public String exibirDashboard(Model model) throws Exception {
        List<Registro> todos = registroRepository.findAll();
        long total = todos.size();

        model.addAttribute("total", total);
        model.addAttribute("ultimos", todos);


        model.addAttribute("somaColuna3", registroService.somarColuna3(todos));
        model.addAttribute("categoriasDistintas", registroService.contarCategoriasDistintas(todos));


        Map<String, Long> contagem = registroService.contagemPorColuna2(todos);
        model.addAttribute("categoriasJson", objectMapper.writeValueAsString(contagem.keySet()));
        model.addAttribute("valoresJson", objectMapper.writeValueAsString(contagem.values()));

        return "dashboard";
    }

    @PostMapping("/dashboard/upload")
    public String importarArquivoUsuario(@RequestParam("file") MultipartFile file) {
        registroService.processarArquivoUsuario(file);
        return "redirect:/dashboard";
    }
}
