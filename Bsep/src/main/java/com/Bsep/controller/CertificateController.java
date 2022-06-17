package com.bsep.controller;

import com.bsep.dto.CertificateDto;
import com.bsep.dto.CertificateRevocationStatusDTO;
import com.bsep.dto.NewCertificateDto;
import com.bsep.dto.RevokeCertificateDTO;
import com.bsep.model.CertificateData;
import com.bsep.service.LoggerService;
import com.bsep.service.UserService;
import com.bsep.service.impl.CertificateServiceImpl;
import com.bsep.service.impl.LoggerServiceImpl;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/certificate")
public class CertificateController {

    private final LoggerService loggerService;
    private final CertificateServiceImpl certificateService;
    private final UserService userService;

    public CertificateController(CertificateServiceImpl certificateService, UserService userService) {
        this.certificateService = certificateService;
        this.userService = userService;
        this.loggerService = new LoggerServiceImpl(this.getClass());
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('CREATE_CERTIFICATE_PERMISSION')")
    public ResponseEntity<CertificateData> createCertificate(@RequestBody NewCertificateDto newCertificateDto) throws Exception {
        CertificateData newCertificate = certificateService.createCertificate(newCertificateDto);
        if(newCertificate == null) {
            loggerService.certificateCreatingFailed(SecurityContextHolder.getContext().getAuthentication().getName(), userService.findById(newCertificateDto.getSubjectUID()).getUsername());
            return ResponseEntity.badRequest().build();
        }
        loggerService.certificateCreated(SecurityContextHolder.getContext().getAuthentication().getName(), userService.findById(newCertificateDto.getSubjectUID()).getUsername());
        return ResponseEntity.ok(newCertificate);
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('GET_CERTIFICATES_PERMISSION')")
    public ResponseEntity<List<CertificateDto>> getUserCertificates(@RequestParam(required = false) Boolean isCa, Authentication authentication) {

       List<CertificateDto> certificates;

       certificates = certificateService.getByUsername(authentication.getName());
       loggerService.userCertificates(authentication.getName());

        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('GET_ALL_CERTIFICATES_PERMISSION')")
    public ResponseEntity<List<CertificateDto>> getAllCertificates(@RequestParam(required = false) Boolean isCa, Authentication authentication) {
        List<CertificateDto> certificates;

        certificates = certificateService.getAll(isCa);
        loggerService.allCertificates(authentication.getName());

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
            loggerService.certificateDownloadSuccess(SecurityContextHolder.getContext().getAuthentication().getName(), id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            loggerService.certificateDownloadFailed(SecurityContextHolder.getContext().getAuthentication().getName(), id);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{serial}/revoke")
    @PreAuthorize("hasAuthority('REVOKE_CERTIFICATE_PERMISSION')")
    public ResponseEntity<HttpStatus> revoke(@PathVariable String serial, @RequestBody RevokeCertificateDTO dto) {
        certificateService.revoke(serial, dto);
        loggerService.certificateRevokingSuccess(SecurityContextHolder.getContext().getAuthentication().getName(), serial);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{serial}/status")
    @PreAuthorize("hasAuthority('CHECK_CERTIFICATE_PERMISSION')")
    public ResponseEntity<CertificateRevocationStatusDTO> isRevoked(@PathVariable String serial) {
        loggerService.checkIfCertificateIsRevoked(SecurityContextHolder.getContext().getAuthentication().getName(), serial);
        return ResponseEntity.ok(certificateService.isRevoked(serial));
    }

    @GetMapping(value = "/{serial}/valid")
    @PreAuthorize("hasAuthority('CHECK_CERTIFICATE_PERMISSION')")
    public ResponseEntity<Boolean> isValid(@PathVariable String serial) {
        boolean isValid = certificateService.checkIsValid(serial);
        loggerService.checkIfCertificateIsValid(SecurityContextHolder.getContext().getAuthentication().getName(), serial);
        return ResponseEntity.ok(isValid);
    }
}
