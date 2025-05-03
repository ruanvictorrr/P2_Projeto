package br.upe;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        LogAnalyzer analyzer = new LogAnalyzer();
        Scanner scanner = new Scanner(System.in);

        // Menu de carregamento do log
        boolean carregado = false;
        while (!carregado) {
            System.out.println("\n=== Carregamento do access.log ===");
            System.out.println("1 - Carregar do diretório atual");
            System.out.println("2 - Carregar da pasta resources");
            System.out.println("3 - Informar caminho manualmente");
            System.out.print("Escolha: ");
            int opc = scanner.nextInt();
            scanner.nextLine(); // limpar quebra de linha

            switch (opc) {
                case 1 -> carregado = analyzer.carregarViaRelativo();
                case 2 -> carregado = analyzer.carregarViaResources();
                case 3 -> {
                    System.out.print("Digite o caminho do arquivo: ");
                    String caminho = scanner.nextLine();
                    carregado = analyzer.carregarViaCaminhoInformado(caminho);
                }
                default -> System.out.println("Opção inválida.");
            }

            if (!carregado) {
                System.out.println("Erro ao carregar o log. Tente novamente.");
            }
        }

        // Menu de análise após o carregamento
        int option;
        do {
            System.out.println("\n--- Menu de Análise ---");
            System.out.println("1 - Recursos grandes");
            System.out.println("2 - Não respondidos em Novembro/2021");
            System.out.println("3 - Sistemas operacionais em 2021");
            System.out.println("4 - Média de POSTs em 2021");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            option = scanner.nextInt();

            switch (option) {
                case 1 -> analyzer.gerarRecursosGrandes();
                case 2 -> analyzer.gerarNaoRespondidosNovembro();
                case 3 -> analyzer.gerarResumoSistemasOperacionais();
                case 4 -> analyzer.gerarMediaPOST2021();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida.");
            }            

        } while (option != 0);

        scanner.close();
    }
}