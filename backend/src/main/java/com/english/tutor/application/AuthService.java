package com.english.tutor.application;

import com.english.tutor.domain.User;
import com.english.tutor.domain.UserRepository;
import com.english.tutor.infrastructure.security.PasswordHasher;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordHasher passwordHasher;

    public boolean register(String email, String password, String englishLevel) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false; // E-mail já cadastrado no sistema
        }

        User user = new User(null, email, password, englishLevel);
        if (!user.isValid()) {
            throw new IllegalArgumentException("Dados de usuário inválidos para cadastro");
        }

        // Hashear a senha antes de salvar (Segurança e Criptografia)
        String hashedPassword = passwordHasher.hash(password);
        user.setPassword(hashedPassword);

        userRepository.save(user);
        return true;
    }

    public Optional<String> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Optional.empty(); // Usuário não encontrado
        }

        User user = userOpt.get();
        if (!passwordHasher.verify(password, user.getPassword())) {
            return Optional.empty(); // Senha incorreta
        }

        // Gerar token JWT utilizando SmallRye JWT assinado com a nossa chave privada
        String token = Jwt.issuer("https://tutor.english.com/issuer")
                .upn(user.getEmail())
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("englishLevel", user.getEnglishLevel())
                .groups("USER")
                .expiresIn(86400) // Token válido por 24 horas
                .sign();

        return Optional.of(token);
    }
}
