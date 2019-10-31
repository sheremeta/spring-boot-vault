package tech.ingenico;

import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApplicationTests {

    private static final String TOKEN_VAULT = "sUlnKb4t35NBNwiZ4762mEAm";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @ClassRule
    public static VaultContainer vault = new VaultContainer().withVaultToken(TOKEN_VAULT);

    @BeforeClass
    public static void setUp() throws Exception {
        setupVault();
    }

    @Test
    public void encryptDecrypt() {
        String secret = RandomString.make(10);

        String encrypted = this.restTemplate.getForObject(String.format("http://localhost:%d/encryption/encrypt?value={value}", port), String.class, secret);
        String decrypted = this.restTemplate.getForObject(String.format("http://localhost:%d/encryption/decrypt?value={value}", port), String.class, encrypted);

        assertThat(decrypted).isEqualTo(secret);
    }

    private static void setupVault() throws java.io.IOException, InterruptedException {
        System.setProperty("VAULT_HOST", vault.getContainerIpAddress());
        System.setProperty("VAULT_PORT", String.valueOf(vault.getFirstMappedPort()));
        System.setProperty("VAULT_TOKEN", "sUlnKb4t35NBNwiZ4762mEAm");

        vault.execInContainer("/bin/sh", "-c", "vault secrets enable transit");
        vault.execInContainer("/bin/sh", "-c", "vault write -f transit/keys/customer");
    }
}
