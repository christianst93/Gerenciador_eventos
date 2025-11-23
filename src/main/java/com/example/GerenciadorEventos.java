package com.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class GerenciadorEventos {
    public static void main(String[] args) {
       
        String host = "localhost"; 
        String porta = "5432";
        String nomeBanco = "gerenciador_eventos";
        String usuario = "postgres";
        String senha = "senha";
        
        String url = "jdbc:postgresql://" + host + ":" + porta + "/" + nomeBanco;

        Scanner in = new Scanner(System.in);

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
             Statement stmt = conexao.createStatement()) {           
           
            System.out.println("Conexão estabelecida com sucesso!");

        do {                
                System.out.println("Digite 1 para cadastrar um usuario:");
                System.out.println("Digite 2 para cadastrar um evento:");
                System.out.println("Digite 3 para cadastrar um participante:");
                System.out.println("Digite 4 para cadastrar uma inscrição:");
                System.out.println("Digite 0 para sair:");
                int opcao = in.nextInt();
                in.nextLine();

                switch (opcao) {
                case 0: System.out.println("Conexão encerrada.");              
                        return;
                case 1: String inserirUsuario = "INSERT INTO usuario (nome,email, senha) VALUES (?,?,?) RETURNING id_usuario";
                        int id_usuario;
                        try (PreparedStatement ps = conexao.prepareStatement(inserirUsuario)){
                            System.out.println("Digite o nome:");
                            ps.setString(1, in.nextLine());
                            System.out.println("Digite o email:");
                            ps.setString(2, in.nextLine());
                            System.out.println("Digite a senha:");
                            ps.setString(3, in.nextLine());
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            id_usuario = rs.getInt("id_usuario");
                            System.out.println("Usuário cadastrado com ID: " + id_usuario + "\n");                            
                        } catch (SQLException e){
                            if (e.getMessage().contains("duplicate key value")) {
                                System.out.println("\nO email informado já está cadastrado no sistema, tente utilizar outro email.\n");
                            } else {
                                System.out.println("Ocorreu um erro ao cadastrar o usuário:");
                                System.out.println(e.getMessage());
                            }
                        }               
                    break;
                case 2: String inserirEvento = "INSERT INTO evento (titulo, descricao, data_evento, local, id_usuario, criado_em) VALUES (?,?,?,?,?,?) RETURNING id_evento";
                        int id_evento;
                        try (PreparedStatement ps = conexao.prepareStatement(inserirEvento)){
                            System.out.println("Digite o nome do evento:");
                            ps.setString(1, in.nextLine());
                            System.out.println("Digite a descrição:");
                            ps.setString(2, in.nextLine());
                            System.out.println("Digite a data do evento:");
                            String data = in.nextLine();
                            
                            java.sql.Date dataEvento;
                            try {
                                dataEvento = java.sql.Date.valueOf(data);
                            } catch (IllegalArgumentException ex) {
                                System.out.println("\nA data informada está em formato inválido.");
                                System.out.println("Use o formato correto: (yyyy-mm-dd)\n");
                                break;                            
                            }

                            ps.setDate(3, dataEvento);
                            System.out.println("Digite o local do evento:");
                            ps.setString(4, in.nextLine());
                            System.out.println("Digite o id do usuario:");
                            ps.setInt(5, in.nextInt());
                            in.nextLine();
                            System.out.println("Informe a data e hora da criação do evento:");
                            String dataHora = in.nextLine();
                            ps.setTimestamp(6, java.sql.Timestamp.valueOf(dataHora));
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            id_evento = rs.getInt("id_evento");
                            System.out.println("Evento cadastrado com ID: " + id_evento +"\n");
                        } catch (SQLException e) {
                            String msg = e.getMessage().toLowerCase();
                            if (msg.contains("foreign key constraint") || msg.contains("violates foreign key")) {
                                System.out.println("\nO ID de usuário informado não existe.");
                                System.out.println("Verifique o ID e tente novamente.\n");
                            } else {
                                System.out.println("\nOcorreu um erro ao cadastrar o evento:");
                                System.out.println(e.getMessage());
                            }
                        }
                    break;
                case 3: String inserirParticipante = "INSERT INTO participante (nome, email, telefone, data_cadastro) VALUES (?,?,?,?) RETURNING id_participante";
                        int id_participante;
                        try (PreparedStatement ps = conexao.prepareStatement(inserirParticipante)) {
                            System.out.println("Digite o nome do participante:");
                            ps.setString(1, in.nextLine());
                            System.out.println("Digite o email do participante:");
                            ps.setString(2, in.nextLine());
                            System.out.println("Digite o telefone do participante:");
                            ps.setString(3, in.nextLine());
                            System.out.println("Informe a data e hora do cadastro:");
                            String dataHora = in.nextLine();
                            ps.setTimestamp(4, java.sql.Timestamp.valueOf(dataHora));
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            id_participante = rs.getInt("id_participante");
                            System.out.println("Participante cadastrado com ID: " + id_participante +"\n");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Erro: Formato de data/hora inválido. (yyyy-MM-dd HH:mm:ss)");
                        } catch (SQLException e) {
                            if (e.getMessage().contains("duplicate key value")) {
                                System.out.println("\nO email informado já está cadastrado no sistema, tente utilizar outro email.\n");
                            } else {
                                System.out.println("Erro ao cadastrar participante: " + e.getMessage() + "\n");
                            }
                        }
                        break;
                case 4: String inserirInscricao = "INSERT INTO inscricao (id_participante, id_evento, data_inscricao) VALUES (?,?,?) RETURNING id_inscricao";
                        int id_inscricao;
                        try (PreparedStatement ps = conexao.prepareStatement(inserirInscricao)) {
                            System.out.println("Digite o id do participante:");
                            ps.setInt(1, in.nextInt());
                            System.out.println("Digite o id do evento:");
                            ps.setInt(2, in.nextInt());
                            in.nextLine();
                            System.out.println("Informe a data e hora da inscrição (formato: yyyy-MM-dd HH:mm:ss):");
                            String dataHora = in.nextLine();
                            ps.setTimestamp(3, java.sql.Timestamp.valueOf(dataHora));
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            id_inscricao = rs.getInt("id_inscricao");
                            System.out.println("Inscrição realizada com ID: " + id_inscricao + "\n");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Erro: Formato de data/hora inválido. (yyyy-MM-dd HH:mm:ss)");
                        } catch (SQLException e) {
                            System.out.println("Erro ao realizar inscrição: " + e.getMessage() + "\n");
                        }
                        break;                    
                default: System.out.println("Digite uma opção valida!\n");
                    break;
            } 
        } while (true);        
                
        } catch (SQLException e) {
            System.out.println("Erro de conexao!");
            System.err.println(e.getMessage());
        }
        in.close();
    }

}
