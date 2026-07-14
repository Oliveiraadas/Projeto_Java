# 📊 Dashboard de Processamento e Auditoria de Dados Corporativos

## 📝 Descrição do Projeto
Este sistema é uma solução robusta desenvolvida em **Spring Boot 3** focada na automação, importação e auditoria de dados financeiros corporativos de folhas de pagamento e horas extraordinárias. 

O software conta com um ecossistema completo que engloba um processo em lote orientado a eventos (*CommandLineRunner*), uma API REST para consumo de dados puros e um painel web gerencial interativo em tempo real.

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas
* **Linguagem:** Java 21 (LTS)
* **Framework Core:** Spring Boot 3.4.1
* **Persistência & ORM:** Spring Data JPA / Hibernate
* **Motor de Templates:** Thymeleaf (HTML5 / CSS3 dinâmico)
* **Banco de Dados:** PostgreSQL (Produção) / H2 Database (Testes)
* **Manipulação de Dados:** OpenCSV 4.2
* **Segurança de Variáveis:** Java Dotenv 5.2.2
* **Gerenciador de Dependências:** Maven

---

## ⚙️ Diferenciais Técnicos e Arquitetura

### 1. Detecção Dinâmica de Delimitadores (OpenCSV)
O sistema possui um algoritmo inteligente integrado na camada de serviço (`RegistroService`) que lê e analisa estatisticamente a primeira linha do arquivo `.csv`. Ele identifica de forma dinâmica se o separador utilizado é uma vírgula (`,`) ou ponto e vírgula (`;`), evitando falhas de importação geradas por divergências regionais de softwares como Excel.

### 2. Consistência Transacional Corporativa (`@Transactional`)
Toda a operação de carga e limpeza de dados históricos duplicados é protegida com gerenciamento de transações declarativas do Spring. Caso ocorra qualquer inconsistência de dados ou erro crítico de parsing no meio do arquivo, o framework garante o *rollback* imediato da operação, blindando a integridade da base.

### 3. Padrão MVC e DTO (*Data Transfer Object*)
A estrutura do código foi desenhada com clara separação de responsabilidades (Clean Architecture):
* **Camada Controller:** Gerencia rotas web convencionais (`/dashboard`) e endpoints de API Rest (`/api/dashboard`).
* **Camada Service:** Centraliza as regras de parsing e manipulação lógica de arquivos e streams de dados.
* **Camada Repository:** Realiza as operações otimizadas no banco de dados PostgreSQL.
* **DashboardDTO:** Transfere dados estruturados sem expor diretamente entidades sensíveis de persistência para as requisições da API.

---

## 🗂️ Estrutura do Banco de Dados
A entidade fundamental `Registro` mapeia registros mapeados dinamicamente com as colunas extraídas do arquivo fonte:
* `id`: Identificador único auto-incremental (`GenerationType.IDENTITY`).
* `coluna1`: Identificação / Código base.
* `coluna2`: Detalhamento textual de lançamentos.
* `coluna3`: Métricas quantitativas / Financeiras.

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
* Java 21 instalado.
* Maven instalado e configurado no PATH.
* Instância do PostgreSQL rodando localmente.

### 1. Configurando o Banco de Dados
Crie um banco de dados vazio no seu PostgreSQL com o nome:
```sql
CREATE DATABASE meu_projeto_java;
```

### 2. Configurando as Variáveis de Ambiente
Na raiz do projeto, crie ou configure o arquivo `.env` (ou ajuste o seu arquivo `application.properties`) mapeando os caminhos corretos da sua máquina:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/meu_projeto_java
spring.datasource.username=seu_usuario_postgres
spring.datasource.password=sua_senha_postgres

caminho_do_arquivo=C:\\temp\\24. Pagamento de Horas Extraordinárias (Referência_ Ano de 2025).csv
```

### 3. Compilando e Rodando
Abra o terminal na pasta raiz do projeto e execute os comandos:
```bash
# Compilar o projeto
mvn clean install

# Executar a aplicação Spring Boot
mvn spring-boot:run
```

---

## 🎯 Endpoints Disponíveis
Após a inicialização do sistema, você poderá testar o ecossistema através dos seguintes caminhos locais no seu navegador:

* **Painel Web (Interface Gráfica com Thymeleaf):** `http://localhost:8080/dashboard`
* **Dados Puros da API (JSON / DTO):** `http://localhost:8080/api/dashboard`
