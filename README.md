# Módulo de Segurança - OAuth2 com Spring

Este módulo de segurança implementa autenticação e autorização utilizando OAuth2 com Spring Security. 
Ele se integra com um banco de dados PostgreSQL para armazenar e gerenciar os detalhes dos clientes registrados. 
Foi criado um client-id padrão mas é importante que cada negócio tenha a sua própria configuração.

**CRIAÇÃO DE UM NOVO CLIENT-ID:**

````curl
curl --location 'http://localhost:8082/register-client-oauth2' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=73A871A00A9AA9B9B70F21073FB2A797; SESSION=MzM5ZjkxZGEtODM4NC00Mjg2LWJkYjUtNjI1MDM5ZjViZGZm' \
--data '{
  "withId": "2",
  "clientId": "douglas-test",
  "clientSecret": "12345",
  "scopes": [
    "mi:teste-read"
  ]
}'
````


**Geração do TOKEK:**

```bash
curl --location 'http://authorization-server.com:8082/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic bW9udHJlYWw6MTIzNDU2' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'scope=mi:external-write'
```

Username: montreal
Password: montreal

-------

## Configuração do Resource Server

Para configurar o Resource Server, que é a aplicação que utilizará o token para autenticar as rotas, 
são necessárias algumas dependências e configurações específicas.

### Dependências Maven

Adicione as seguintes dependências ao seu arquivo `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

Essas dependências fornecem as funcionalidades necessárias para que o aplicativo atue como um servidor de recursos OAuth2.

Configuração no application.properties
No arquivo application.properties, configure o URI do emissor do token:

```java
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://authorization-server.com:8082
```

Isso informa ao Resource Server onde ele pode verificar a validade e a autenticidade dos tokens JWT.
Configuração da Classe SecurityConfig
Além disso, é necessário configurar a classe SecurityConfig. Nesta classe, você pode definir regras de 
segurança específicas para suas rotas, como restrições de acesso baseadas em papéis de usuário ou validações adicionais de token.
Certifique-se de incluir todas as configurações necessárias para garantir a segurança adequada de suas rotas e recursos.
Dentro da classe SecurityConfig, você pode definir métodos para configurar autenticação, autorização e outras políticas 
de segurança conforme necessário para sua aplicação.
Essas configurações garantirão que seu Resource Server esteja configurado corretamente para autenticar e proteger suas 
rotas de acordo com as políticas de segurança definidas.


```java

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/actuator/*",
            "/swagger-ui/**"
    };

    public static final String MATCHER_ACTUATOR = "/actuator";
    public static final String MATCHER_ACTUATOR_HEALTH = "/actuator/health";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authorization ->
                        authorization
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .requestMatchers(MATCHER_ACTUATOR).permitAll()
                                .requestMatchers(MATCHER_ACTUATOR_HEALTH).permitAll()
                                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                                        .decoder(new JWTDecoder())
                        )
                )
                .build();
    }

}

```


## Funcionalidades

- Implementa OAuth2 para autenticação e autorização.
- Armazena e gerencia os detalhes dos clientes registrados em um banco de dados PostgreSQL.
- Utiliza uma chave JKS para assinar os tokens de acesso e atualização.

## OAuth2

OAuth2 é um protocolo de autorização amplamente utilizado para permitir que aplicativos de terceiros acessem os recursos
de um usuário em um serviço protegido. Ele fornece uma maneira segura para conceder acesso limitado aos recursos sem a 
necessidade de compartilhar senhas.

O método OAuth2 funciona delegando a autenticação do usuário para o provedor de identidade e fornecendo aos clientes um 
token de acesso temporário que pode ser usado para acessar recursos protegidos em nome do usuário.

## Banco de Dados PostgreSQL

Este módulo utiliza um banco de dados PostgreSQL para armazenar e gerenciar os detalhes dos clientes registrados. 
Isso inclui informações como ID do cliente, segredos, escopos permitidos, autoridades e muito mais. O banco de dados 
fornece uma maneira eficiente de persistir esses dados e facilita a escalabilidade e o gerenciamento.

## Chave JKS

Uma chave JKS (Java KeyStore) é usada para assinar os tokens de acesso e atualização gerados pelo servidor OAuth2. 
Isso garante que os tokens sejam autênticos e não tenham sido alterados durante a transmissão. A chave JKS é armazenada 
com segurança e usada para verificar a integridade dos tokens durante o processo de autenticação.

````
- Criando
  keytool -genkeypair -alias edpvalidate -keyalg RSA -keypass 123456 -keystore edpvalidate.jks -storepass 123456 -validity 3560

- Validando
  keytool -list -keystore edpvalidate.jks

````

## Configuração

Para configurar e executar este módulo de segurança:

1. Certifique-se de ter um servidor PostgreSQL em execução e atualize as configurações de banco de dados no arquivo `application.properties`.
2. Importe a chave JKS e atualize o caminho e as configurações no arquivo `application.properties`.
3. Compile e execute o aplicativo Spring.

## Dependências

- Java 17
- Spring Boot 3.2.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- JKS (Java KeyStore)


#### Run with docker
````
docker-compose -f docker-compose-dev.yml up
docker-compose -f docker-compose-test.yml up
docker-compose -f docker-compose-prd.yml up

````

#### Diagramas

![Diagram: model](docs/oauth2.png)

## Postman
link: https://api.postman.com/collections/3736685-0e416fb7-2ee4-40eb-8dc2-7565b9d43a64?access_key=PMAT-01HNESFJC0FA82M3RVHNPHAD25


Este README.md fornece instruções detalhadas sobre como configurar o Resource Server usando OAuth2 com Spring Security,
incluindo dependências Maven, configurações no arquivo `application.properties` e configuração da classe `SecurityConfig`.

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).
