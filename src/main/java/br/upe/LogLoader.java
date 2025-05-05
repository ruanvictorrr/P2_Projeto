package br.upe;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class LogLoader {

    // Carrega arquivo do mesmo diretório do projeto
    public List<String> carregarViaRelativo(String nomeArquivo) throws IOException {
        Path caminho = Paths.get(nomeArquivo);
        return Files.readAllLines(caminho);
    }

    
    public List<String> carregarViaCaminho(String caminhoCompleto) throws IOException {
        Path caminho = Paths.get(caminhoCompleto);
        return Files.readAllLines(caminho);
    }

    // Carrega arquivo da pasta resources 
    public List<String> carregarViaResources(String nomeArquivo) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(nomeArquivo);
        if (input == null) {
            throw new FileNotFoundException("Arquivo não encontrado em resources: " + nomeArquivo);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.lines().collect(Collectors.toList());
        }
    }
}
