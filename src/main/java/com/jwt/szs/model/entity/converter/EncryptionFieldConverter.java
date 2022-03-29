package com.jwt.szs.model.entity.converter;

import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import io.netty.handler.codec.DecoderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Converter
@Slf4j
public class EncryptionFieldConverter implements AttributeConverter<String, String> {

    @Value("${encryption.key}")
    private String encryptionKey;

    private static final String encryptAlgorithm = "AES";
    private Key key;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        key = new SecretKeySpec(encryptionKey.getBytes(), encryptAlgorithm);

        if (isNullOrEmpty(attribute)) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(encryptAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(Hex.encode(cipher.doFinal(attribute.getBytes()))).toUpperCase();
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new CustomRuntimeException(ErrorCode.ENCRYPTION_FAIL);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        key = new SecretKeySpec(encryptionKey.getBytes(), encryptAlgorithm);

        if (isNullOrEmpty(dbData)) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(encryptAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Hex.decode(dbData)));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | DecoderException e) {
            throw new CustomRuntimeException(ErrorCode.DECRYPTION_FAIL);
        }
    }

    private Boolean isNullOrEmpty(String content) {
        return content == null || content.isEmpty();
    }
}
