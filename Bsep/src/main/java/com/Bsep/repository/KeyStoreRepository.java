package com.bsep.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Repository;

import com.bsep.keystore.KeyStoreReader;
import com.bsep.keystore.KeyStoreWriter;
import com.bsep.model.CertificateType;
import com.bsep.service.LoggerService;
import com.bsep.service.impl.LoggerServiceImpl;

@Repository
public class KeyStoreRepository {
    private KeyStore keyStoreRoot;
    private KeyStore keyStoreIntermediate;
    private KeyStore keyStoreEndEntity;


    private static final String keyStoresDirectory = "data" + File.separator + "keystores";

    private static final String KS_ROOT_PATH = keyStoresDirectory + File.separator + "root.jks";
    private static final String KS_INTERMEDIATE_PATH = keyStoresDirectory + File.separator + "intermediate.jks";
    private static final String KS_END_ENTITY_PATH = keyStoresDirectory + File.separator + "endEntity.jks";


    private static final String PASSWORD = "password";

    private KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
    private KeyStoreReader keyStoreReader = new KeyStoreReader();
    
    private final LoggerService loggerService= new LoggerServiceImpl(this.getClass());


    public KeyStoreRepository() throws IOException {
        Files.createDirectories(Paths.get(keyStoresDirectory));
        Security.addProvider(new BouncyCastleProvider());
        try {
            keyStoreRoot = KeyStore.getInstance("JKS");
            keyStoreIntermediate = KeyStore.getInstance("JKS");
            keyStoreEndEntity = KeyStore.getInstance("JKS");

            initKeyStore(KS_ROOT_PATH, keyStoreRoot);
            initKeyStore(KS_INTERMEDIATE_PATH, keyStoreIntermediate);
            initKeyStore(KS_END_ENTITY_PATH, keyStoreEndEntity);

        } catch (KeyStoreException|CertificateException|IOException|NoSuchAlgorithmException e) {
        	loggerService.errorLog(e);
        } 

    }

    private void initKeyStore(String keyStorePath, KeyStore keyStore) throws IOException, NoSuchAlgorithmException, CertificateException {
        File rootKeyStoreFile = new File(keyStorePath);
        if (!rootKeyStoreFile.exists()) {
            keyStore.load(null, PASSWORD.toCharArray());
            keyStoreWriter.saveKeyStore(keyStorePath, PASSWORD.toCharArray(), keyStore);
        } else {
            keyStoreWriter.loadKeyStore(keyStorePath, PASSWORD.toCharArray(), keyStore);
        }
    }

    public void saveCertificate(PrivateKey privateKey, String serialNumber, CertificateType type, Certificate certificate) {
        if (type == CertificateType.ROOT) {
            keyStoreWriter.write(serialNumber, privateKey, PASSWORD.toCharArray(), new Certificate[] {certificate}, keyStoreRoot);
            keyStoreWriter.saveKeyStore(KS_ROOT_PATH, PASSWORD.toCharArray(), keyStoreRoot);
        } else if (type == CertificateType.INTERMEDIATE) {
            keyStoreWriter.write(serialNumber, privateKey, PASSWORD.toCharArray(), new Certificate[] {certificate}, keyStoreIntermediate);
            keyStoreWriter.saveKeyStore(KS_INTERMEDIATE_PATH, PASSWORD.toCharArray(), keyStoreIntermediate);
        } else {
            keyStoreWriter.write(serialNumber, privateKey, PASSWORD.toCharArray(), new Certificate[] {certificate}, keyStoreEndEntity);
            keyStoreWriter.saveKeyStore(KS_END_ENTITY_PATH, PASSWORD.toCharArray(), keyStoreEndEntity);
        }
    }

    public PrivateKey getPrivateKeyForKeyStore(String issuerAlias, CertificateType certificateType)
            throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {

        if (certificateType.equals(CertificateType.ROOT)) {
            return (PrivateKey) keyStoreRoot.getKey(issuerAlias, PASSWORD.toCharArray());
        } else {
            return (PrivateKey) keyStoreIntermediate.getKey(issuerAlias, PASSWORD.toCharArray());
        }

    }

    public Certificate readCertificate(CertificateType certificateType, String alias) {
        String keyStoreFile = "";
        if (certificateType == CertificateType.ROOT) {
            keyStoreFile = KS_ROOT_PATH;
        } else if (certificateType == CertificateType.INTERMEDIATE) {
            keyStoreFile = KS_INTERMEDIATE_PATH;
        } else {
            keyStoreFile = KS_END_ENTITY_PATH;
        }
        return keyStoreReader.readCertificate(keyStoreFile, PASSWORD, alias);
    }

}
