package com.botengine.automatizacion.service;

import com.botengine.framework.utils.ConfigReader;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class CredencialCryptoService {

    private static final String VARIABLE_CLAVE =
            "BOTENGINE_CREDENTIAL_KEY";

    private static final int IV_LENGTH =
            12;

    private static final int TAG_LENGTH =
            128;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public String cifrar(
            String valor) {

        if (valor == null
                || valor.isBlank()) {

            throw new IllegalArgumentException(
                    "El valor a cifrar no puede estar vacío."
            );
        }

        try {

            byte[] iv =
                    new byte[IV_LENGTH];

            secureRandom.nextBytes(
                    iv
            );

            Cipher cipher =
                    Cipher.getInstance(
                            "AES/GCM/NoPadding"
                    );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    obtenerClave(),
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    )
            );

            byte[] cifrado =
                    cipher.doFinal(
                            valor.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            byte[] resultado =
                    new byte[
                            iv.length
                            + cifrado.length
                    ];

            System.arraycopy(
                    iv,
                    0,
                    resultado,
                    0,
                    iv.length
            );

            System.arraycopy(
                    cifrado,
                    0,
                    resultado,
                    iv.length,
                    cifrado.length
            );

            return Base64
                    .getEncoder()
                    .encodeToString(
                            resultado
                    );

        } catch (GeneralSecurityException exception) {

            throw new IllegalStateException(
                    "No fue posible cifrar la credencial.",
                    exception
            );
        }
    }

    public String descifrar(
            String valorCifrado) {

        if (valorCifrado == null
                || valorCifrado.isBlank()) {

            throw new IllegalArgumentException(
                    "La credencial cifrada está vacía."
            );
        }

        try {

            byte[] contenido =
                    Base64
                            .getDecoder()
                            .decode(
                                    valorCifrado
                            );

            if (contenido.length
                    <= IV_LENGTH) {

                throw new IllegalArgumentException(
                        "La credencial cifrada no es válida."
                );
            }

            byte[] iv =
                    Arrays.copyOfRange(
                            contenido,
                            0,
                            IV_LENGTH
                    );

            byte[] cifrado =
                    Arrays.copyOfRange(
                            contenido,
                            IV_LENGTH,
                            contenido.length
                    );

            Cipher cipher =
                    Cipher.getInstance(
                            "AES/GCM/NoPadding"
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    obtenerClave(),
                    new GCMParameterSpec(
                            TAG_LENGTH,
                            iv
                    )
            );

            byte[] original =
                    cipher.doFinal(
                            cifrado
                    );

            return new String(
                    original,
                    StandardCharsets.UTF_8
            );

        } catch (
                GeneralSecurityException
                | IllegalArgumentException exception) {

            throw new IllegalStateException(
                    "No fue posible descifrar la credencial.",
                    exception
            );
        }
    }

    private SecretKeySpec obtenerClave() {

        String valor =
                ConfigReader.getEnv(
                        VARIABLE_CLAVE
                );

        byte[] clave;

        try {

            clave =
                    Base64
                            .getDecoder()
                            .decode(
                                    valor
                            );

        } catch (IllegalArgumentException exception) {

            throw new IllegalStateException(
                    "BOTENGINE_CREDENTIAL_KEY no contiene Base64 válido."
            );
        }

        if (clave.length != 16
                && clave.length != 24
                && clave.length != 32) {

            throw new IllegalStateException(
                    "BOTENGINE_CREDENTIAL_KEY debe representar una clave AES de 16, 24 o 32 bytes."
            );
        }

        return new SecretKeySpec(
                clave,
                "AES"
        );
    }
}