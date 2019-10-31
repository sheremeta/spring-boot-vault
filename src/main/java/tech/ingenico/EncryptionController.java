package tech.ingenico;

import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/encryption")
public class EncryptionController {

    private final VaultOperations vaultOperations;

    public EncryptionController(VaultOperations vaultOperations) {
        this.vaultOperations = vaultOperations;
    }

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String value) {
        return vaultOperations.opsForTransit().encrypt("customer", Plaintext.of(value)).getCiphertext();
    }

    @GetMapping("/decrypt")
    public String decrypt(@RequestParam String value) {
        return vaultOperations.opsForTransit().decrypt("customer", Ciphertext.of(value)).asString();
    }
}
