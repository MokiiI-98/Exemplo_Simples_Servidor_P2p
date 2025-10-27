package org.example;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Peer {

    private static final String SERVIDOR_DIRETORIO = "localhost";
    private static final int PORTA_DIRETORIO = 5000;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== PEER P2P Centralizado ===");
        System.out.println("1 - Compartilhar arquivo (Peer Servidor)");
        System.out.println("2 - Baixar arquivo (Peer Cliente)");
        System.out.println("3 - Listar arquivos registrados");
        System.out.print("Escolha: ");
        int opcao = sc.nextInt();
        sc.nextLine();

        switch (opcao) {
            case 1 -> {
                System.out.print("Nome do arquivo para compartilhar: ");
                String arquivo = sc.nextLine();
                System.out.print("Porta do servidor local (ex: 6001): ");
                int porta = sc.nextInt();
                iniciarCompartilhamento(arquivo, porta);
            }
            case 2 -> {
                System.out.print("Nome do arquivo para baixar: ");
                String nome = sc.nextLine();
                baixarArquivo(nome);
            }
            case 3 -> listarArquivos();
            default -> System.out.println("❌ Opção inválida!");
        }

        sc.close();
    }

    // === PEER SERVIDOR ===
    private static void iniciarCompartilhamento(String arquivo, int porta) throws Exception {
        String ipLocal = InetAddress.getLocalHost().getHostAddress();

        // Registro no servidor de diretórios
        try (Socket s = new Socket(SERVIDOR_DIRETORIO, PORTA_DIRETORIO);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            out.println("REGISTER " + arquivo + " " + ipLocal + ":" + porta);
        }

        System.out.println("📡 Arquivo '" + arquivo + "' registrado no diretório.");
        System.out.println("📍 IP: " + ipLocal + " | Porta: " + porta);

        // Envio de notificação por e-mail
        String dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        String corpoEmail = "Arquivo: " + arquivo +
                "\nIP: " + ipLocal +
                "\nPorta: " + porta +
                "\nData/Hora: " + dataHora;

        try {
            EmailUtil.enviar("lima.luan@ufms.br", "Compartilhamento iniciado", corpoEmail);
            System.out.println("📧 E-mail de notificação enviado!");
        } catch (Exception e) {
            System.out.println("⚠️ Falha ao enviar e-mail (simulação): " + e.getMessage());
        }

        // Servidor de envio de arquivo
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("📂 Peer servidor aguardando conexões na porta " + porta + "...");
            while (true) {
                Socket cliente = servidor.accept();
                new Thread(() -> enviarArquivo(cliente, arquivo)).start();
            }
        }
    }

    private static void enviarArquivo(Socket cliente, String arquivo) {
        try (FileInputStream fis = new FileInputStream(arquivo);
             OutputStream outFile = cliente.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytes;
            while ((bytes = fis.read(buffer)) != -1) {
                outFile.write(buffer, 0, bytes);
            }
            System.out.println("📤 Arquivo '" + arquivo + "' enviado para " + cliente.getInetAddress());
        } catch (IOException e) {
            System.err.println("Erro ao enviar arquivo: " + e.getMessage());
        } finally {
            try {
                cliente.close();
            } catch (IOException ignored) {}
        }
    }

    // === PEER CLIENTE ===
    private static void baixarArquivo(String nome) throws Exception {
        String endereco;
        try (
                Socket s = new Socket(SERVIDOR_DIRETORIO, PORTA_DIRETORIO);
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))
        ) {
            out.println("GET " + nome);
            endereco = in.readLine();
        }

        if (endereco == null || endereco.equals("NAO_ENCONTRADO")) {
            System.out.println("❌ Arquivo não encontrado no servidor de diretórios!");
            return;
        }

        String[] dados = endereco.split(":");
        String host = dados[0];
        int porta = Integer.parseInt(dados[1]);

        System.out.println("🔍 Servidor de diretório retornou:");
        System.out.println("   -> IP: " + host + " | Porta: " + porta);
        System.out.println("⏬ Iniciando download do peer...");

        try (
                Socket peer = new Socket(host, porta);
                InputStream input = peer.getInputStream();
                FileOutputStream fos = new FileOutputStream("download_" + nome)
        ) {
            byte[] buffer = new byte[4096];
            int bytes;
            while ((bytes = input.read(buffer)) != -1) {
                fos.write(buffer, 0, bytes);
            }
            System.out.println("✅ Download concluído com sucesso: download_" + nome);
        }
    }

    // === LISTAR ARQUIVOS REGISTRADOS ===
    private static void listarArquivos() throws Exception {
        try (
                Socket s = new Socket(SERVIDOR_DIRETORIO, PORTA_DIRETORIO);
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))
        ) {
            out.println("LIST");
            System.out.println("📜 Arquivos registrados no servidor de diretórios:");
            String linha;
            while ((linha = in.readLine()) != null) {
                System.out.println("  - " + linha);
            }
        }
    }
}
