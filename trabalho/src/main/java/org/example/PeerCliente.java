package org.example;// PeerCliente.java
import java.io.*;
import java.net.*;

public class PeerCliente {
    public static void main(String[] args) throws Exception {
        // Consulta ao servidor de diretórios
        Socket s = new Socket("localhost", 6000);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        out.println("GET teste.txt");
        String endereco = in.readLine();
        s.close();

        if (endereco.equals("NAO_ENCONTRADO")) {
            System.out.println("Arquivo não encontrado no servidor de diretórios!");
            return;
        }

        // Conexão direta com o peer servidor
        String[] dados = endereco.split(":");
        String host = dados[0];
        int porta = Integer.parseInt(dados[1]);

        Socket peer = new Socket(host, porta);
        InputStream input = peer.getInputStream();
        FileOutputStream fos = new FileOutputStream("download_" + System.currentTimeMillis() + ".txt");

        byte[] buffer = new byte[4096];
        int bytes;
        while ((bytes = input.read(buffer)) != -1) {
            fos.write(buffer, 0, bytes);
        }

        fos.close();
        peer.close();
        System.out.println("Download concluído!");
    }
}
