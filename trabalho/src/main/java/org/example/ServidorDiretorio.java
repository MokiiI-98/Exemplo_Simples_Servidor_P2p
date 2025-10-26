package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorDiretorio {

    // Mapa: nome do arquivo -> endereço do peer (host:porta)
    private static final Map<String, String> arquivos = new HashMap<>();

    public static void main(String[] args) {
        final int porta = 5000; // ✅ Porta fixa para todos os peers
        System.out.println("🌐 Servidor de diretórios iniciado na porta " + porta + "...");

        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> tratarConexao(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor de diretórios: " + e.getMessage());
        }
    }

    private static void tratarConexao(Socket socket) {
        String clienteInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        System.out.println("🔗 Nova conexão de " + clienteInfo);

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String comando = in.readLine();
            if (comando == null) return;

            System.out.println("📩 Comando recebido: " + comando);

            if (comando.startsWith("REGISTER")) {
                String[] dados = comando.split(" ");
                if (dados.length != 3) {
                    out.println("ERRO: formato incorreto. Use: REGISTER <arquivo> <endereco>");
                    return;
                }

                arquivos.put(dados[1], dados[2]);
                out.println("Arquivo registrado com sucesso: " + dados[1]);
                System.out.println("📁 Arquivo '" + dados[1] + "' registrado por " + dados[2]);

            } else if (comando.equals("LIST")) {
                if (arquivos.isEmpty()) {
                    out.println("Nenhum arquivo registrado.");
                } else {
                    for (Map.Entry<String, String> entry : arquivos.entrySet()) {
                        out.println(entry.getKey() + " -> " + entry.getValue());
                    }
                }

            } else if (comando.startsWith("GET")) {
                String[] dados = comando.split(" ");
                if (dados.length != 2) {
                    out.println("ERRO: formato incorreto. Use: GET <arquivo>");
                    return;
                }

                String resposta = arquivos.getOrDefault(dados[1], "NAO_ENCONTRADO");
                out.println(resposta);
                System.out.println("🔎 Consulta: " + dados[1] + " -> " + resposta);

            } else {
                out.println("Comando desconhecido!");
                System.out.println("❌ Comando inválido recebido: " + comando);
            }

        } catch (IOException e) {
            System.err.println("Erro no tratamento da conexão: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("🔒 Conexão encerrada com " + clienteInfo);
            } catch (IOException ignored) {}
        }
    }
}
