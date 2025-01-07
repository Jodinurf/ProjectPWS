package com.TugasProject.WSB2022;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dataKtp")
public class DataKtpController {

    private final DataKtpJpaController ctrl = new DataKtpJpaController();
    private static final Logger logger = LoggerFactory.getLogger(DataKtpController.class);

    // Helper method for mapping DataKtp to DataKtpResponse
    private DataKtpResponse mapToResponse(DataKtp dataKtp) {
        return new DataKtpResponse(
                dataKtp.getNik(),
                dataKtp.getNama(),
                dataKtp.getTglLahir(),
                dataKtp.getAlamat(),
                Base64.getEncoder().encodeToString(dataKtp.getPhoto()));
    }

    // Helper method for validating input fields
    private String validateInput(String nik, String nama, Date tglLahir) {
        if (nik == null || nik.isEmpty() || nik.length() != 16) {
            return "NIK is required and must be 16 characters.";
        }
        if (nama == null || nama.isEmpty()) {
            return "Nama is required.";
        }
        if (tglLahir == null) {
            return "Tanggal Lahir is required.";
        }
        return null;
    }

    // Helper method for validating image file
    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }

    // Get all data
    @GetMapping("/getAllData")
    public ResponseEntity<List<DataKtpResponse>> getAllData() {
        try {
            List<DataKtp> dataList = ctrl.findDataKtpEntities();
            List<DataKtpResponse> responseList = dataList.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            logger.error("Error fetching data: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    // Create data
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createData(
            @RequestParam("nik") String nik,
            @RequestParam("nama") String nama,
            @RequestParam("tglLahir") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tglLahir,
            @RequestParam("alamat") String alamat,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            String validationError = validateInput(nik, nama, tglLahir);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("error", validationError));
            }

            if (photo != null && !photo.isEmpty()) {
                if (photo.getSize() > 5 * 1024 * 1024) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Photo size exceeds 5 MB."));
                }
                if (!isValidImageFile(photo)) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Photo must be a valid image file (JPEG or PNG)."));
                }
            }

            DataKtp dataKtp = new DataKtp();
            dataKtp.setNik(nik);
            dataKtp.setNama(nama);
            dataKtp.setTglLahir(tglLahir);
            dataKtp.setAlamat(alamat);

            if (photo != null) {
                dataKtp.setPhoto(photo.getBytes());
            }

            ctrl.create(dataKtp);
            return ResponseEntity.ok(Map.of("message", "Data successfully created with NIK: " + nik));
        } catch (Exception e) {
            logger.error("Error creating data: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error creating data", "details", e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateData(
            @RequestParam("nik") String nik,
            @RequestParam("nama") String nama,
            @RequestParam("tglLahir") @DateTimeFormat(pattern = "yyyy-MM-dd") Date tglLahir,
            @RequestParam("alamat") String alamat,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        try {
            String validationError = validateInput(nik, nama, tglLahir);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("error", validationError));
            }

            DataKtp dataKtp = ctrl.findDataKtp(nik); 
            if (dataKtp == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Data not found for NIK: " + nik));
            }

            dataKtp.setNama(nama);
            dataKtp.setTglLahir(tglLahir);
            dataKtp.setAlamat(alamat);

            if (photo != null && !photo.isEmpty()) {
                dataKtp.setPhoto(photo.getBytes());
            }

            ctrl.edit(dataKtp);
            return ResponseEntity.ok(Map.of("message", "Data successfully updated with NIK: " + nik));
        } catch (Exception e) {
            logger.error("Error updating data: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error updating data", "details", e.getMessage()));
        }
    }

    // Delete data by NIK
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteData(@RequestParam("nik") String nik) {
        try {
            if (nik == null || nik.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "NIK is required for deleting data."));
            }

            ctrl.destroy(nik);
            return ResponseEntity.ok(Map.of("message", "Data successfully deleted for NIK: " + nik));
        } catch (Exception e) {
            logger.error("Error deleting data: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Error deleting data", "details", e.getMessage()));
        }
    }

    // Search by NIK or Name
    @GetMapping("/search")
    public ResponseEntity<List<DataKtpResponse>> searchData(
            @RequestParam(value = "nik", required = false) String nik,
            @RequestParam(value = "name", required = false) String name) {
        EntityManager em = ctrl.getEntityManager();
        try {
            StringBuilder queryBuilder = new StringBuilder("SELECT d FROM DataKtp d WHERE 1=1");
            if (nik != null && !nik.isEmpty()) {
                queryBuilder.append(" AND d.nik = :nik");
            }
            if (name != null && !name.isEmpty()) {
                queryBuilder.append(" AND d.nama LIKE :name");
            }

            Query query = em.createQuery(queryBuilder.toString(), DataKtp.class);
            if (nik != null && !nik.isEmpty()) {
                query.setParameter("nik", nik);
            }
            if (name != null && !name.isEmpty()) {
                query.setParameter("name", "%" + name + "%");
            }

            List<DataKtp> results = query.getResultList();
            List<DataKtpResponse> responseList = results.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            logger.error("Error searching data: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        } finally {
            em.close();
        }
    }
}
