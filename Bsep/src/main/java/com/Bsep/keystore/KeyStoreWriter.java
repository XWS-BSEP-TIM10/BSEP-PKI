package com.bsep.keystore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class KeyStoreWriter {
    //KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
    //Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
    // - Sertifikati koji ukljucuju javni kljuc
    // - Privatni kljucevi
    // - Tajni kljucevi, koji se koriste u simetricnima siframa

    public KeyStoreWriter() {
		/*try {
			keyStore = KeyStore.getInstance("JKS", "SUN");
		} catch (KeyStoreException e) {
			
		} catch (NoSuchProviderException e) {
			
		}*/
    }

    public void loadKeyStore(String fileName, char[] password, KeyStore keyStore) {
        try {
            if (fileName != null) {
                keyStore.load(new FileInputStream(fileName), password);
            } else {
                //Ako je cilj kreirati novi KeyStore poziva se i dalje load, pri cemu je prvi parametar null
                keyStore.load(null, password);
            }
        } catch (NoSuchAlgorithmException e) {
            
        } catch (CertificateException e) {
            
        } catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            
        }
    }

    public void saveKeyStore(String fileName, char[] password, KeyStore keyStore) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (KeyStoreException e) {
            
        } catch (NoSuchAlgorithmException e) {
            
        } catch (CertificateException e) {
            
        } catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            
        }
    }

    public void write(String alias, PrivateKey privateKey, char[] password, Certificate[] certificates, KeyStore keyStore) {
        try {
            keyStore.setKeyEntry(alias, privateKey, password, certificates);
        } catch (KeyStoreException e) {
            
        }
    }

    public void writeNonRoot(String alias, PrivateKey privateKey, char[] password, Certificate[] certificates, KeyStore keyStore) {
        try {
            keyStore.setKeyEntry(alias, privateKey, password, certificates);
        } catch (KeyStoreException e) {
            
        }
    }
}
