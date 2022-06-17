package com.bsep.certificate;

import com.bsep.dto.NewCertificateDto;
import com.bsep.model.CertificateType;
import com.bsep.model.IssuerData;
import com.bsep.model.SubjectData;
import com.bsep.service.LoggerService;
import com.bsep.service.impl.LoggerServiceImpl;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateGenerator {
	private final LoggerService loggerService= new LoggerServiceImpl(this.getClass());
	
    public CertificateGenerator() {
    	/*
    	 Constructor 
    	 */
    }

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, NewCertificateDto newCertificateDto) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber(), 16),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());
            //Generise se sertifikat

            addExtensions(newCertificateDto, certGen);

            JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();
            AuthorityKeyIdentifier authorityKeyIdentifier = extensionUtils
                    .createAuthorityKeyIdentifier(issuerData.getPublicKey());
            certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.35"), false, authorityKeyIdentifier);

            SubjectKeyIdentifier subjectKeyIdentifier = extensionUtils
                    .createSubjectKeyIdentifier(subjectData.getPublicKey());
            certGen.addExtension(new ASN1ObjectIdentifier("2.5.29.14"), false, subjectKeyIdentifier);

            X509CertificateHolder certHolder = certGen.build(contentSigner);


            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);
        } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException | NoSuchAlgorithmException | CertIOException e) {
        	return null;
        }
    }

    private void addExtensions(NewCertificateDto newCertificateDto, X509v3CertificateBuilder certGen) {
        if (newCertificateDto.getCertificateType() == CertificateType.END_ENTITY) {
            try {
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));
            } catch (CertIOException e) {
               return; 
            }
        } else {
            try {
                certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            } catch (CertIOException e) {
            	return; 
            }
        }
        int usage = 0;
        for (String s : newCertificateDto.getKeyUsages()) {
            int sInt = Integer.parseInt(s);
            usage |= sInt;
        }

        try {
            if (newCertificateDto.getCertificateType() != CertificateType.END_ENTITY) {
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(usage | KeyUsage.keyCertSign));
            } else {
                certGen.addExtension(Extension.keyUsage, true, new KeyUsage(usage));
            }
            
            ASN1ObjectIdentifier idKp = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.3");
            KeyPurposeId[] purposes = new KeyPurposeId[newCertificateDto.getExtendedKeyUsages().size()];
            for(int i=0;i<purposes.length;i++) {
            	purposes[i] = KeyPurposeId.getInstance(idKp.branch(newCertificateDto.getExtendedKeyUsages().get(i)));
            }
            certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(purposes));

        } catch (CertIOException e) {
        	loggerService.errorLog(e);
        }

    }
}
