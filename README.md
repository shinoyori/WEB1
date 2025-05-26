# Sistema de Apoio a Testes Exploratórios em Video Games
* **Como Rodar**
-  Rode db/MySQL/create.sql (cria banco Sistema) - SGBD MYSQL
    -> user e senha podem ser trocado no arquivo (...\WEB1\src\main\java\br\ufscar\dc\dsw\dao\GenericDAO.java)
   
-  Build (Maven)
  -> Na raiz do projeto: mvn clean package
  -> Gera: target/AA1.war
   
-  Deploy (Tomcat)
  -> Copie target/AA1.war para TOMCAT_HOME/webapps/.
  -> Inicie/Reinicie o Tomcat.
   
-  Teste
  -> http://localhost:8080/AA1/

## Decrição

O projeto foi desenvolvido para a atividade AA-1 da disciplina Desenvolvimento de Software para a Web 1 (UFSCar) e tem o objetivo de auxilia5 testadores de jogos digitais (game testers) na aplicação de Testes Exploratórios (TE), permitindo a criação de projetos, cadastro de estratégias de teste, e gerenciamento de sessões de teste exploratório

## Funcionalidades

**Gerenciamento de Usuários**
- CRUD de administradores e testadores (apenas admins podem gerenciar usuários)
- Controle de autenticação (login, logout e sessão)
- Proteção de rotas e controle de permissões (admin, testador, visitante)

**Gestão de Projetos e Estratégias**
- Cadastro, edição e listagem de projetos com filtros e ordenação
- Cadastro, edição e listagem pública de estratégias (com descrição, exemplos, dicas e imagens)

**Gerenciamento de Sessões de Teste**
- Cadastro de sessões por testadores, vinculadas a projetos e estratégias
- Controle de ciclo de vida das sessões (criado, em execução, finalizado) com registros de data e hora
- Registro de bugs durante as sessões (se implementado)

**Internacionalização**
- Suporte a múltiplos idiomas (Português e Inglês)

## Instruções de Uso

- **Visitante:** pode visualizar estratégias sem necessidade de login.
- **Testador:** pode criar e gerenciar sessões de teste, vinculadas a projetos e estratégias.
- **Administrador:** gerencia usuários, projetos, estratégias e sessões.

## Diagrama de Classes:

Para o desenvolvimento do projeto, um diagrama de classes foi criado como guia para a arquitetura do sistema. Ele contém as principais entidades do sistema, como Usuário, Projeto, Sessão, Estratégia, Imagem e Bug, ilustrando seus atributos e relacionamentos. 

![Diagrama de Classes](https://github.com/user-attachments/assets/9a89ed55-81f2-4ef5-8066-f390b99b0a99)
