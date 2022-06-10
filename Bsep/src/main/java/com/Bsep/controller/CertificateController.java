package com.Bsep.controller;

import com.Bsep.dto.CertificateDto;
import com.Bsep.dto.CertificateRevocationStatusDTO;
import com.Bsep.dto.NewCertificateDto;
import com.Bsep.dto.RevokeCertificateDTO;
import com.Bsep.model.CertificateData;
import com.Bsep.service.impl.CertificateServiceImpl;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/certificate")
public class CertificateController {

    private final CertificateServiceImpl certificateService;

    public CertificateController(CertificateServiceImpl certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('CREATE_CERTIFICATE_PERMISSION')")
    public ResponseEntity<CertificateData> createCertificate(@RequestBody NewCertificateDto newCertificateDto) throws Exception {
        CertificateData newCertificate = certificateService.createCertificate(newCertificateDto);
        if(newCertificate == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(newCertificate);
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('GET_CERTIFICATES_PERMISSION')")
    public ResponseEntity<List<CertificateDto>> getAllCertificates(@RequestParam(required = false) Boolean isCa, Authentication authentication) {
        List<GrantedAuthority> roles= (List<GrantedAuthority>) authentication.getAuthorities();
        List<CertificateDto> certificates;
        if(roles.get(0).getAuthority().equals("ROLE_USER"))
            certificates = certificateService.getByUsername(authentication.getName());
         else
            certificates = certificateService.getAll(isCa);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping(value = "/{id}/download")
    @PreAuthorize("hasAuthority('DOWNLOAD_CERTIFICATE_PERMISSION')")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        try {
            Resource resource = certificateService.getCertificateResource(id);
            String contentType = "application/octet-stream";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"");
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{serial}/revoke")
    @PreAuthorize("hasAuthority('REVOKE_CERTIFICATE_PERMISSION')")
    public ResponseEntity<HttpStatus> revoke(@PathVariable String serial, @RequestBody RevokeCertificateDTO dto) {
        certificateService.revoke(serial, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{serial}/status")
    @PreAuthorize("hasAuthority('CHECK_CERTIFICATE_PERMISSION')")
    public ResponseEntity<CertificateRevocationStatusDTO> isRevoked(@PathVariable String serial) {
        return ResponseEntity.ok(certificateService.isRevoked(serial));
    }

    @GetMapping(value = "/{serial}/valid")
    @PreAuthorize("hasAuthority('CHECK_CERTIFICATE_PERMISSION')")
    public ResponseEntity<Boolean> isValid(@PathVariable String serial) {
        boolean isValid = certificateService.checkIsValid(serial);
        return ResponseEntity.ok(isValid);
    }
}
