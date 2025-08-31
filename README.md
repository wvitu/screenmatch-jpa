# Screenmatch JPA

Projeto baseado na formação **Java | Spring Boot | JPA** da Alura.  
O objetivo é praticar **persistência de dados**, **consultas com Spring Data JPA** e boas práticas de organização em um projeto Java moderno.

> **Pontos de aprendizado**: modelagem de entidades, relacionamentos, repositórios Spring Data, consultas derivadas/JPQL, migrations, validações e camadas de serviço/controlador.

---

## 🧰 Stack

- **Java 17+**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **Banco**: H2 (dev) ou Postgres/MySQL (prod)  
- **Maven** (wrapper incluído)
- **Lombok** (facilita getters/setters/constructors)

---

## 📦 Estrutura (resumo)

```
src/
 └── main/
     ├── java/
     │   └── br/com/alura/screenmatch/
     │       ├── ScreenmatchApplication.java
     │       ├── domain/ (entidades + repositories)
     │       ├── service/ (regras de negócio)
     │       └── controller/ (REST endpoints)
     └── resources/
         ├── application.properties (ou .yml)
         └── data.sql / schema.sql (opcional)
```

> Ajuste os pacotes acima conforme seu código.

---

## ⚙️ Configuração

### 1) Variáveis de ambiente (opcional)
Se consumir APIs externas, crie o arquivo `.env` ou defina variáveis do sistema:
```
OMDB_API_KEY=coloque_sua_chave_aqui
```

### 2) Banco de dados
Exemplo com **H2** (desenvolvimento):
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:h2:mem:screenmatchdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2
```

Exemplo com **PostgreSQL** (produção/dev local):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/screenmatch
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## ▶️ Como rodar

Com Java 17+ instalado:

```bash
# 1. Clonar
git clone https://github.com/wvitu/screenmatch-jpa
cd screenmatch-jpa

# 2. Rodar com Maven Wrapper
./mvnw clean spring-boot:run
# (no Windows) mvnw.cmd clean spring-boot:run
```

- App em: `http://localhost:8080`
- H2 Console (se habilitado): `http://localhost:8080/h2` (JDBC URL deve bater com o application.properties)

---

## 🔗 Endpoints (exemplos – ajuste conforme sua API)

| Método | Caminho                         | Descrição                              |
|-------:|---------------------------------|----------------------------------------|
| GET    | `/series`                       | Lista séries                           |
| GET    | `/series/{id}`                  | Detalhe de série                       |
| GET    | `/series/top5`                  | Top 5 por avaliação                    |
| GET    | `/series/{id}/temporadas`       | Temporadas da série                    |
| POST   | `/series`                       | Cadastra nova série                    |
| GET    | `/episodios?ano=YYYY`           | Busca episódios por ano                |

**Exemplo de resposta (JSON):**
```json
{
  "id": 1,
  "titulo": "Breaking Bad",
  "genero": "Drama",
  "nota": 9.7,
  "totalTemporadas": 5
}
```

---

## 🧪 Testes

- Testes de repositório com **@DataJpaTest**
- Testes de serviço/controlador com **SpringBootTest / MockMvc**

Para executar:
```bash
./mvnw test
```

---

## 🧱 Boas práticas aplicadas

- Camadas bem definidas (Controller → Service → Repository)
- DTOs para entrada/saída (evitar expor entidades)
- Validações com `jakarta.validation`
- Tratamento de erros com `@ControllerAdvice` (Problem Details)
- Consultas Spring Data (derived queries/`@Query` quando necessário)

---

## 🗺️ Roadmap (próximos passos)

- [ ] Documentar com **SpringDoc OpenAPI** (Swagger UI)
- [ ] Adicionar **Bean Validation** nos DTOs
- [ ] Popular base com **Flyway** (migrations)
- [ ] Paginação e filtros avançados (ex.: gênero, nota mínima, ano)
- [ ] Cache para endpoints de leitura frequente
- [ ] Pipeline CI (GitHub Actions) e profile `prod`

---

## 🛠️ Comandos úteis

```bash
# Build do projeto
./mvnw clean package

# Rodar com perfil específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 📸 Demos (opcional)

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Postman**: importe a coleção em `/docs/postman_collection.json` (se você criar)

---

## 🤝 Contribuição

1. Crie uma branch: `feat/minha-melhoria`
2. Commit semântico: `feat: adiciona filtro por gênero`
3. Abra um PR explicando motivação e testes

---

## 👤 Autor

**Wanderson Vitor**  
LinkedIn: https://www.linkedin.com/in/wvitor  
GitHub: https://github.com/wvitu

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja `LICENSE` para detalhes.
