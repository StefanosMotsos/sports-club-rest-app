package club.sportsapp.api;

import club.sportsapp.dto.JobStatusDTO;
import club.sportsapp.service.IMemberReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class MemberReportingRestController {

    private final IMemberReportingService memberReportingService;

    @PostMapping
    public ResponseEntity<Map<String, String>> startReport() {

        String jobId = UUID.randomUUID().toString();
        memberReportingService.generateReport(jobId);
        return ResponseEntity.accepted().body(Map.of("jobId", jobId));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobStatusDTO> getReport(@PathVariable String jobId) {
        JobStatusDTO status = memberReportingService.getJobId(jobId);

        if (status == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(status);
    }
}
