package com.bsep.service;

import com.bsep.dto.CertificateDto;
import com.bsep.dto.CertificateRevocationStatusDTO;
import com.bsep.dto.NewCertificateDto;
import com.bsep.dto.RevokeCertificateDTO;
import com.bsep.exception.CertificateInvalidException;
import com.bsep.model.CertificateData;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.text.ParseException;
import java.util.List;

public interface CerificateService {

    CertificateData createCertificate(NewCertificateDto newCertificateDto) throws CertificateInvalidException, UnrecoverableKeyException, CertificateEncodingException, KeyStoreException, NoSuchAlgorithmException, ParseException, IOException;

    List<CertificateDto> getAll(Boolean isCa);

    Resource getCertificateResource(Long id);

    List<CertificateDto> getByUsername(String name);

    void revoke(String serialNumber, RevokeCertificateDTO revokeCertificateDTO);

    CertificateRevocationStatusDTO isRevoked(String serialNumber);

    boolean checkIsValid(String serialNumber);
}
