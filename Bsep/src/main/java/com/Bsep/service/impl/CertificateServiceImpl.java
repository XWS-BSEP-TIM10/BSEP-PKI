package com.bsep.service.impl;

import com.bsep.certificate.CertificateGenerator;
import com.bsep.dto.CertificateDto;
import com.bsep.dto.CertificateRevocationStatusDTO;
import com.bsep.dto.NewCertificateDto;
import com.bsep.dto.RevokeCertificateDTO;
import com.bsep.exception.CertificateInvalidException;
import com.bsep.mapper.CertificateMapper;
import com.bsep.model.CertificateData;
import com.bsep.model.CertificatePurposeType;
import com.bsep.model.CertificateStatus;
import com.bsep.model.CertificateType;
import com.bsep.model.IssuerData;
import com.bsep.model.SubjectData;
import com.bsep.model.User;
import com.bsep.repository.CertificateDataRepository;
import com.bsep.repository.KeyStoreRepository;
import com.bsep.service.CerificateService;
import com.bsep.service.UserService;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class CertificateServiceImpl implements CerificateService {

    private final UserService userService;

    private final CertificateDataRepository certificateDataRepository;

    private final KeyStoreRepository keyStoreRepository;

    private final CertificateMapper certificateMapper;

    private static final String BEGIN_CERT = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERT = "-----END CERTIFICATE-----";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
    private static final String CERTIFICATE_DIRECTORY = "data" + File.separator + "certificates";
    


    public CertificateServiceImpl(UserService userService, CertificateDataRepository certificateDataRepository, KeyStoreRepository keyStoreRepository, CertificateMapper certificateMapper) {
        this.userService = userService;
        this.certificateDataRepository = certificateDataRepository;
        this.keyStoreRepository = keyStoreRepository;
        this.certificateMapper = certificateMapper;
    }


    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(CERTIFICATE_DIRECTORY));
    }

    @Override
    public CertificateData createCertificate(NewCertificateDto newCertificateDto) throws CertificateInvalidException, UnrecoverableKeyException, CertificateEncodingException, KeyStoreException, NoSuchAlgorithmException, ParseException, IOException {
        KeyPair keyPairSubject = generateKeyPair();
        if (keyPairSubject == null)
            return null;
        SubjectData subjectData = generateSubjectData(newCertificateDto, keyPairSubject.getPublic());
        if (subjectData == null)
            return null;
        String issuerSerialNumber = "";
        if (newCertificateDto.getCertificateType() == CertificateType.ROOT)
            issuerSerialNumber = subjectData.getSerialNumber();
        else
            issuerSerialNumber = newCertificateDto.getIssuerCertificateSerialNumber();
        IssuerData issuerData = getIssuerData(newCertificateDto, keyPairSubject, subjectData);

        CertificateData certificateData = new CertificateData(subjectData.getSerialNumber(),
                getRDNValueFromSubjectData(subjectData, BCStyle.CN),
                getRDNValueFromSubjectData(subjectData, BCStyle.O),
                getRDNValueFromSubjectData(subjectData, BCStyle.OU),
                getRDNValueFromSubjectData(subjectData, BCStyle.C),
                issuerSerialNumber,
                new Date(),
                iso8601Formater.parse(newCertificateDto.getEndDate()),
                CertificateStatus.VALID,
                newCertificateDto.getCertificateType(),
                getCertificatePurposeBasedOnType(newCertificateDto.getCertificateType()));

        if(newCertificateDto.getCertificateType() != CertificateType.ROOT) {
            CertificateData issuerCertificateData = certificateDataRepository.findBySerialNumber(issuerSerialNumber);
            if (certificateData.getEndDate().after(issuerCertificateData.getEndDate())) {
                return null;
            }
        }
        if(certificateData.getEndDate().before(new Date())){
            return null;
        }

        CertificateData savedCertificateData = certificateDataRepository.save(certificateData);

        X509Certificate x509certificate = new CertificateGenerator().generateCertificate(subjectData, issuerData, newCertificateDto);

        keyStoreRepository.saveCertificate(keyPairSubject.getPrivate(), x509certificate.getSerialNumber().toString(16), newCertificateDto.getCertificateType(), x509certificate);
        
        createCertificateFile(savedCertificateData.getId());
        return savedCertificateData;
    }

    @Override
    public List<CertificateDto> getAll(Boolean isCa) {
        List<CertificateDto> certificateDtos = new ArrayList<>();
        Set<CertificateData> certificates = certificateDataRepository.findAll(isCa);
        for (CertificateData certificate : certificates) {
            isCertificateValid(certificate);
            CertificateData issuerCertificate = certificateDataRepository.findBySerialNumber(certificate.getIssuerSerialNumber());
            certificateDtos.add(certificateMapper.toDTO(certificate, issuerCertificate.getSubjectUsername()));
        }
        return certificateDtos;
    }

    @Override
    public Resource getCertificateResource(Long id) {
        try {
        	Optional<CertificateData> certificateDataTemp = certificateDataRepository.findById(id);
        	if(!certificateDataTemp.isPresent()) return null;
        	CertificateData certificateData = certificateDataTemp.get();
            File filePath = getCertificatePath(certificateData.getSerialNumber());
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toPath().toUri());
            if (resource.exists()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            
            return null;
        }
    }

    @Override
    public List<CertificateDto> getByUsername(String username) {
        List<CertificateDto> certificateDtos = new ArrayList<>();
        List<CertificateData> certificates = certificateDataRepository.findBySubjectUsername(username);
        for (CertificateData certificate : certificates) {
            isCertificateValid(certificate);
            CertificateData issuerCertificate = certificateDataRepository.findBySerialNumber(certificate.getIssuerSerialNumber());
            certificateDtos.add(certificateMapper.toDTO(certificate, issuerCertificate.getSubjectUsername()));
        }
        return certificateDtos;
    }

    @Override
    public void revoke(String serialNumber, RevokeCertificateDTO revokeCertificateDTO) {
        CertificateData certificateData = certificateDataRepository.findBySerialNumber(serialNumber);
        certificateData.setCertificateStatus(CertificateStatus.REVOKED);
        certificateData.setRevocationReason(revokeCertificateDTO.getReason());
        certificateDataRepository.save(certificateData);
        for (CertificateData cert : certificateDataRepository.findByIssuerSerialNumber(certificateData.getSerialNumber())) {
            if (cert.getCertificateType() == CertificateType.ROOT)
                continue;
            revoke(cert.getSerialNumber(),revokeCertificateDTO);
        }
    }

    @Override
    public CertificateRevocationStatusDTO isRevoked(String serialNumber) {
        CertificateData certificateData = certificateDataRepository.findBySerialNumber(serialNumber);
        return new CertificateRevocationStatusDTO(certificateData.getCertificateStatus() == CertificateStatus.REVOKED, certificateData.getRevocationReason());
        
    }

    @Override
    public boolean checkIsValid(String serialNumber) {
        CertificateData certificateData = certificateDataRepository.findBySerialNumber(serialNumber);
        return isCertificateValid(certificateData);
    }

    private boolean isCertificateValid(CertificateData certificate) {
        if (certificate.getCertificateStatus() == CertificateStatus.REVOKED)
            return false;
        verifySignature(certificate);
        checkExpiration(certificate);
        return certificate.getCertificateStatus() == CertificateStatus.VALID;
    }

    private void verifySignature(CertificateData certificateData) {
        Certificate certificate = keyStoreRepository.readCertificate(certificateData.getCertificateType(), certificateData.getSerialNumber());
        Certificate[] certificateChain = getCertificateChain(certificateData, certificate);
        try {
            if (certificateChain.length < 2)
                certificateChain[0].verify(certificateChain[0].getPublicKey());

            for (int i = 0; i < certificateChain.length - 1; i++) {
                certificateChain[i].verify(certificateChain[i + 1].getPublicKey());
            }
        } catch (SignatureException | CertificateException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException e) {
            certificateData.setCertificateStatus(CertificateStatus.INVALID);
        }
    }

    private void checkExpiration(CertificateData certificateData) {
        if (certificateData.getEndDate().before(new Date()))
            certificateData.setCertificateStatus(CertificateStatus.EXPIRED);
    }

    private File getCertificatePath(String serialNumber) {
        return new File(CERTIFICATE_DIRECTORY + File.separator + serialNumber + ".cer");
    }

    private void createCertificateFile(Long id) throws CertificateInvalidException, CertificateEncodingException, IOException {
        Base64.Encoder encoder = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes());
        Optional<CertificateData> certificateDataTemp = certificateDataRepository.findById(id);
    	if(!certificateDataTemp.isPresent()) return;
    	CertificateData certificateData = certificateDataTemp.get();
        if (!isCertificateValid(certificateData)) {
            throw new CertificateInvalidException();
        }
        StringBuilder certificates = new StringBuilder();
        Certificate firstCert = keyStoreRepository.readCertificate(certificateData.getCertificateType(), certificateData.getSerialNumber());
        Certificate[] certs = getCertificateChain(certificateData, firstCert);
        for (int i = 0; i < certs.length - 1; i++) {
            Certificate cert = certs[i];
            byte[] bytes = cert.getEncoded();
            String certificate = BEGIN_CERT + LINE_SEPARATOR + new String(encoder.encode(bytes)) + LINE_SEPARATOR
                    + END_CERT + LINE_SEPARATOR;
            certificates.append(certificate);
        }
        Certificate cert = certs[certs.length - 1];
        byte[] bytes = cert.getEncoded();
        String certificate = BEGIN_CERT + LINE_SEPARATOR + new String(encoder.encode(bytes)) + LINE_SEPARATOR
                + END_CERT;
        certificates.append(certificate);
        writeBytesToFile("data" + File.separator + "certificates" + File.separator + certificateData.getSerialNumber() + ".cer",
                certificates.toString().getBytes());
    }

    private void writeBytesToFile(String fileOutput, byte[] bytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileOutput)) {
            fos.write(bytes);
        }
    }

    private CertificatePurposeType getCertificatePurposeBasedOnType(CertificateType certificateType) {
        if (certificateType.equals(CertificateType.ROOT)) return CertificatePurposeType.SERVICE;
        if (certificateType.equals(CertificateType.INTERMEDIATE)) return CertificatePurposeType.SUBSYSTEM;
        return CertificatePurposeType.USER;
    }

    private SubjectData generateSubjectData(NewCertificateDto newCertificateDto, PublicKey publicKey) {
        try {
            Date startDate = new Date();
            Date endDate = iso8601Formater.parse(newCertificateDto.getEndDate());

            String sn = generateSerialNumber();
            User user = userService.findById(newCertificateDto.getSubjectUID());
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, user.getUsername());
            builder.addRDN(BCStyle.SURNAME, user.getFirstName());
            builder.addRDN(BCStyle.GIVENNAME, user.getLastName());
            builder.addRDN(BCStyle.O, newCertificateDto.getOrganization());
            builder.addRDN(BCStyle.OU, newCertificateDto.getOrganizationalUnitName());
            builder.addRDN(BCStyle.C, newCertificateDto.getCountryCode());
            builder.addRDN(BCStyle.E, user.getUsername());
            builder.addRDN(BCStyle.UID, user.getId().toString());

            return new SubjectData(publicKey, builder.build(), sn, startDate, endDate);
        } catch (ParseException e) {
        	return null;
        }
    }

    private Certificate[] getCertificateChain(CertificateData certificateData, Certificate currentCertificate) {
        if (certificateData.getCertificateType() == CertificateType.ROOT)
            return new Certificate[]{currentCertificate};

        List<Certificate> certificates = new ArrayList<>();
        certificates.add(currentCertificate);
        CertificateData issuerCertificateData = certificateDataRepository.findBySerialNumber(certificateData.getIssuerSerialNumber());
        Certificate issuerCertificate = keyStoreRepository.readCertificate(issuerCertificateData.getCertificateType(), issuerCertificateData.getSerialNumber());
        certificates.add(issuerCertificate);
        while (!issuerCertificateData.getSerialNumber().equals(issuerCertificateData.getIssuerSerialNumber())) {
            issuerCertificateData = certificateDataRepository.findBySerialNumber(issuerCertificateData.getIssuerSerialNumber());
            issuerCertificate = keyStoreRepository.readCertificate(issuerCertificateData.getCertificateType(), issuerCertificateData.getSerialNumber());
            certificates.add(issuerCertificate);
        }
        return certificates.toArray(new Certificate[0]);
    }


    public static String generateSerialNumber() {
        String uuid;
        do
            uuid = UUID.randomUUID().toString().replace("-", ""); //uuid starting with 0 creates problems with bigint parsing
        while (uuid.startsWith("0"));
        return uuid;
    }


    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException|NoSuchProviderException e) {
        	return null;
        } 
    }

    private IssuerData getIssuerData(NewCertificateDto newCertificateDto, KeyPair keyPairSubject, SubjectData subjectData) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateEncodingException {

        if (newCertificateDto.getCertificateType() == CertificateType.ROOT) {
            return new IssuerData(keyPairSubject.getPrivate(), subjectData.getX500name(), keyPairSubject.getPublic());
        }
        CertificateData issuerCertificateData = certificateDataRepository.findBySerialNumber(newCertificateDto.getIssuerCertificateSerialNumber());
        Certificate issuerCertificate = keyStoreRepository.readCertificate(issuerCertificateData.getCertificateType(), issuerCertificateData.getSerialNumber());
        PrivateKey issuerKey = keyStoreRepository.getPrivateKeyForKeyStore((issuerCertificateData.getSerialNumber()), issuerCertificateData.getCertificateType());
        X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) issuerCertificate).getSubject();
        return new IssuerData(issuerKey, issuerName, issuerCertificate.getPublicKey());
    }

    private String getRDNValueFromSubjectData(SubjectData subjectData, ASN1ObjectIdentifier asn1ObjectIdentifier) {
        RDN rdn = subjectData.getX500name().getRDNs(asn1ObjectIdentifier)[0];
        return IETFUtils.valueToString(rdn.getFirst().getValue());
    }


}
