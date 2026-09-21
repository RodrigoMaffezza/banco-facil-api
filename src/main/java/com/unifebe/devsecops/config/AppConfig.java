package com.unifebe.devsecops.config;

/**
 * Segredos de runtime (senha de banco, chaves de API) NAO devem ficar no
 * codigo-fonte. Aqui sao lidos de variaveis de ambiente injetadas no
 * processo em execucao; em producao, essas variaveis viriam de um cofre de
 * segredos (ex.: HashiCorp Vault, AWS Secrets Manager, Azure Key Vault) e
 * NAO do GITHUB_TOKEN, que e um segredo de CI/build, nao de runtime/aplicacao.
 */
public class AppConfig {

    public static String dbPassword() {
        return System.getenv("DB_PASSWORD");
    }

    public static String awsAccessKeyId() {
        return System.getenv("AWS_ACCESS_KEY_ID");
    }

    public static String awsSecretAccessKey() {
        return System.getenv("AWS_SECRET_ACCESS_KEY");
    }

    public static String paymentGatewayApiKey() {
        return System.getenv("PAYMENT_GATEWAY_API_KEY");
    }

}
