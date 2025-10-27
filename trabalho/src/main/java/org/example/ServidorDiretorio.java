package org.example;

import java.io.*;
import java.net.*;
import java.util.*;


public class ServidorDiretorio {

    
    private static final Map<String, String> arquivos = new HashMap<>();

    public static void main(String[] args) {
        final int porta = 5000;
        System.out.println("🌐 Servidor de Diretórios iniciado na porta " + porta + "...");
        System.out.println("🗂️  Aguardando registros e consultas de peers...\n");

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> tratarConexao(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Erro no servidor de diretórios: " + e.getMessage());
        }
    }

    private static void tratarConexao(Socket socket) {
        String clienteInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        System.out.println("🔗 Nova conexão recebida de " + clienteInfo);

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String comando = in.readLine();
            if (comando == null) return;

            System.out.println("📩 Comando recebido: " + comando);

            if (comando.startsWith("REGISTER")) {
                registrarArquivo(comando, out);
            } else if (comando.equals("LIST")) {
                listarArquivos(out);
            } else if (comando.startsWith("GET")) {
                localizarArquivo(comando, out);
            } else {
                out.println("❌ Comando desconhecido!");
                System.out.println("⚠️  Comando inválido recebido de " + clienteInfo + ": " + comando);
            }

        } catch (IOException e) {
            System.err.println("💥 Erro ao tratar conexão com " + clienteInfo + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("🔒 Conexão encerrada com " + clienteInfo + "\n");
            } catch (IOException ignored) {}
        }
    }

    // === REGISTRAR ARQUIVO ===
    private static void registrarArquivo(String comando, PrintWriter out) {
        String[] dados = comando.split(" ");
        if (dados.length != 3) {
            out.println("ERRO: formato incorreto. Use: REGISTER <arquivo> <endereco>");
            return;
        }

        String nomeArquivo = dados[1];
        String enderecoPeer = dados[2];
        arquivos.put(nomeArquivo, enderecoPeer);

        out.println("✅ Arquivo registrado com sucesso: " + nomeArquivo);
        System.out.println("📁 Novo registro: '" + nomeArquivo + "' → " + enderecoPeer);
    }

    // === LISTAR ARQUIVOS ===
    private static void listarArquivos(PrintWriter out) {
        if (arquivos.isEmpty()) {
            out.println("⚠️  Nenhum arquivo registrado no momento.");
            System.out.println("📂 Pedido de listagem — Nenhum arquivo disponível.");
            return;
        }

        out.println("📜 Lista de arquivos disponíveis:");
        for (Map.Entry<String, String> entry : arquivos.entrySet()) {
            out.println("  • " + entry.getKey() + " → " + entry.getValue());
        }

        System.out.println("📋 Lista enviada a um peer.");
    }

    // === LOCALIZAR ARQUIVO ===
    private static void localizarArquivo(String comando, PrintWriter out) {
        String[] dados = comando.split(" ");
        if (dados.length != 2) {
            out.println("ERRO: formato incorreto. Use: GET <arquivo>");
            return;
        }

        String nomeArquivo = dados[1];
        String endereco = arquivos.get(nomeArquivo);

        if (endereco != null) {
            out.println(endereco);
            System.out.println("🔎 Consulta: '" + nomeArquivo + "' localizado em " + endereco);
        } else {
            out.println("NAO_ENCONTRADO");
            System.out.println("❌ Consulta: '" + nomeArquivo + "' não encontrado no diretório.");
        }
    }
}
