# Trabalho Prático - Arquitetura P2P Centralizada

## Descrição

Este projeto simula um **sistema de compartilhamento de arquivos baseado na arquitetura P2P centralizada**, inspirado no modelo do **Napster**. O sistema é composto por três componentes principais:

1. **Servidor de Diretórios**:  
    Responsável por registrar os peers e os arquivos disponíveis no sistema.

2. **Peer Servidor**:  
    Responsável por compartilhar arquivos e enviar dados para outros peers.

3. **Peer Cliente**:  
    Responsável por buscar e baixar arquivos a partir de outros peers.

A comunicação entre os componentes é realizada utilizando **sockets TCP** para garantir a troca de informações entre os peers e o servidor central. Quando um peer inicia o compartilhamento de um arquivo, ele envia uma notificação por e-mail informando sobre o compartilhamento iniciado, incluindo o endereço IP e a porta.

---

## Componentes do Sistema

### **Servidor de Diretórios**:
- Registra os arquivos compartilhados pelos peers.
- Oferece funcionalidades para listar arquivos registrados e fornecer os endereços dos peers que possuem determinados arquivos.

### **Peer Servidor**:
- Recebe solicitações de compartilhamento de arquivos.
- Registra os arquivos no servidor de diretórios e aguarda conexões para enviar os arquivos.

### **Peer Cliente**:
- Solicita arquivos do servidor de diretórios.
- Faz o download dos arquivos diretamente de outro peer.

---

## Funcionalidades

- **Compartilhar Arquivo**:  
    O Peer Servidor pode compartilhar arquivos com outros peers.
  
- **Baixar Arquivo**:  
    O Peer Cliente pode buscar arquivos no servidor de diretórios e realizar o download diretamente de um peer.
  
- **Listar Arquivos Registrados**:  
    O Peer Cliente pode consultar o servidor de diretórios para listar os arquivos que estão sendo compartilhados.

---

## Como Utilizar

### Requisitos

1. **Java 11+** instalado em sua máquina.
2. **Maven** (opcional, caso queira usar o Maven para gerenciar dependências).
3. **Conta de e-mail configurada** para envio de notificações (simulação de envio via `EmailUtil`).

### Executando o Sistema

1. **Iniciar o Servidor de Diretórios**:
    - Execute a classe `ServidorDiretorio`.
    - O servidor ficará aguardando as conexões dos peers.

    ```bash
    java -cp target/SeuProjeto.jar org.example.ServidorDiretorio
    ```

2. **Iniciar o Peer Servidor** (para compartilhar um arquivo):
    - Execute a classe `Peer`.
    - Escolha a opção "1 - Compartilhar arquivo (Peer Servidor)".
    - Informe o nome do arquivo a ser compartilhado e a porta para comunicação.

    ```bash
    java -cp target/SeuProjeto.jar org.example.Peer
    ```

3. **Iniciar o Peer Cliente** (para baixar um arquivo):
    - Execute novamente a classe `Peer`.
    - Escolha a opção "2 - Baixar arquivo (Peer Cliente)".
    - Informe o nome do arquivo a ser baixado.

    ```bash
    java -cp target/SeuProjeto.jar org.example.Peer
    ```

trabalho/
│
├── .idea/ # Configurações do IntelliJ IDEA
│ ├── .gitignore # Arquivo de exclusões do Git
│ ├── compiler.xml # Configurações do compilador
│ ├── encodings.xml # Configurações de codificação
│ ├── jarRepositories.xml # Repositórios de JAR
│ ├── misc.xml # Configurações diversas
│ ├── vcs.xml # Controle de versão
│ └── workspace.xml # Configurações do workspace
│
├── .mvn/ # Diretório de configuração do Maven
│
├── src/ # Código-fonte
│ ├── main/
│ │ ├── java/
│ │ │ └── org/example/ # Pacote com as classes principais
│ │ │ ├── EmailUtil.java # Classe utilitária para envio de e-mails
│ │ │ ├── Peer.java # Classe principal (Peer Servidor e Cliente)
│ │ │ ├── PeerCliente.java # Classe de exemplo para o Peer Cliente
│ │ │ └── ServidorDiretorio.java # Classe do Servidor de Diretório
│ │ └── resources/ # Arquivos de recursos
│ │ └── teste.txt # Arquivo de teste compartilhado
│ └── test/ # Diretório de testes (vazio)
│
├── target/ # Saída do Maven (arquivos compilados)
│ ├── download_teste01.txt # Arquivo baixado pelo peer cliente
│
└── pom.xml # Arquivo de configuração do Maven

## Tecnologias Utilizadas

- **Java 11+** para a implementação do sistema.
- **Sockets TCP** para comunicação entre os peers e o servidor.
- **JavaMail** para enviar notificações por e-mail quando o compartilhamento de arquivo é iniciado.

---

## Como Contribuir

1. Faça um fork deste repositório.
2. Crie uma nova branch para suas mudanças.
3. Realize as alterações e faça commit com uma mensagem explicativa.
4. Envie um pull request para revisão.

---

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

### Observações Finais
Esse README fornece um resumo sobre a arquitetura e como executar o sistema. Certifique-se de que todos os pré-requisitos estão atendidos antes de rodar o projeto. Caso encontre algum erro ou tenha sugestões de melhoria, sinta-se à vontade para contribuir.
