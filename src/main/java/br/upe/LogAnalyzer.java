package br.upe;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class LogAnalyzer {
    private List<String> linhas = new ArrayList<>();

    public boolean carregarViaRelativo() {
        try {
            LogLoader loader = new LogLoader();
            this.linhas = loader.carregarViaRelativo("access.log");
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao carregar arquivo: " + e.getMessage());
            return false;
        }
    }

    public boolean carregarViaResources() {
        try {
            LogLoader loader = new LogLoader();
            this.linhas = loader.carregarViaResources("access.log");
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao carregar arquivo: " + e.getMessage());
            return false;
        }
    }

    public boolean carregarViaCaminhoInformado(String caminhoStr) {
        try {
            LogLoader loader = new LogLoader();
            this.linhas = loader.carregarViaCaminho(caminhoStr);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao carregar arquivo: " + e.getMessage());
            return false;
        }
    }

    public List<String> getLinhas() {
        return linhas;
    }

    // ---------- MÉTODOS DE ANÁLISE ----------

    public void gerarRecursosGrandes() {
        try {
            Files.createDirectories(Paths.get("Análise"));
            Pattern pattern = Pattern.compile("\".*\"\\s(\\d{3})\\s(\\d+|-)");

            List<String> grandes = linhas.stream()
                .filter(linha -> {
                    Matcher m = pattern.matcher(linha);
                    if (m.find()) {
                        String tamanhoStr = m.group(2);
                        try {
                            if (!tamanhoStr.equals("-")) {
                                int tamanho = Integer.parseInt(tamanhoStr);
                                return tamanho > 5000;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                    return false;
                })
                .toList();

            Path saida = Paths.get("Análise/recursosGrandes.txt");
            Files.write(saida, grandes);
            System.out.println("Relatório salvo em: " + saida.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    public void gerarNaoRespondidosNovembro() {
        try {
            Files.createDirectories(Paths.get("Análise"));
    
            List<String> filtrados = linhas.stream()
                .filter(l -> l.contains("/Nov/2021") && l.matches(".*\\s4\\d{2}\\s.*"))
                .toList();
    
            Path saida = Paths.get("Análise/naoRespondidosNovembro.txt");
            Files.write(saida, filtrados);
            System.out.println("Relatório salvo em: " + saida.toAbsolutePath());
    
        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    

    public void gerarResumoSistemasOperacionais() {
        try {
            Files.createDirectories(Paths.get("Análise"));
            Map<String, Long> contagem = new HashMap<>();
    
            for (String linha : linhas) {
                if (!linha.contains("/2021")) continue;
    
                String sistema = identificarSistemaOperacional(linha.toLowerCase());
                if (sistema != null) {
                    contagem.put(sistema, contagem.getOrDefault(sistema, 0L) + 1);
                }
            }
    
            List<String> resultado = contagem.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + ": " + e.getValue())
                .toList();
    
            Path saida = Paths.get("Análise/sistemasOperacionais.txt");
            Files.write(saida, resultado);
            System.out.println("Relatório salvo em: " + saida.toAbsolutePath());
    
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
    
    private String identificarSistemaOperacional(String linha) {
        if (linha.contains("android") || linha.contains("mobile")) return "Mobile";
        if (linha.contains("windows")) return "Windows";
        if (linha.contains("mac os") || linha.contains("macintosh") || linha.contains("darwin")) return "macOS";
        if (linha.contains("ubuntu")) return "Ubuntu";
        if (linha.contains("fedora")) return "Fedora";
        if (linha.contains("x11")) return "Linux (outros)";
        return null;
    }
    

    public void gerarMediaPOST2021() {
        try {
            Files.createDirectories(Paths.get("Análise"));
            Pattern dataPattern = Pattern.compile("\\[(\\d{2})/(\\w{3})/2021");
            Map<String, Integer> meses = new LinkedHashMap<>();

            List<String> nomesMeses = List.of(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            );
            for (String mes : nomesMeses) {
                meses.put(mes, 0);
            }

            for (String linha : linhas) {
                if (linha.contains("POST") && linha.contains("/2021")) {
                    Matcher m = dataPattern.matcher(linha);
                    if (m.find()) {
                        String mes = m.group(2);
                        if (meses.containsKey(mes)) {
                            meses.put(mes, meses.get(mes) + 1);
                        }
                    }
                }
            }

            int total = meses.values().stream().mapToInt(Integer::intValue).sum();
            long mesesComPost = meses.values().stream().filter(v -> v > 0).count();
            double media = mesesComPost > 0 ? (double) total / mesesComPost : 0.0;

            List<String> resultado = new ArrayList<>();
            resultado.add("POSTs por mês em 2021:");
            meses.forEach((mes, count) -> resultado.add(mes + ": " + count));
            resultado.add("");
            resultado.add("Média de POSTs por mês (considerando meses com POSTs): " + String.format("%.2f", media));

            Path saida = Paths.get("Análise/mediaPOST2021.txt");
            Files.write(saida, resultado);
            System.out.println("Relatório salvo em: " + saida.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
