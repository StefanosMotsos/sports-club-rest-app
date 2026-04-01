package club.sportsapp.runner;

import club.sportsapp.service.IMemberReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportingResultsRunner implements CommandLineRunner {
    
    private final IMemberReportingService memberReportingService;

    @Override
    public void run(String... args) throws Exception {
        String jobId = UUID.randomUUID().toString();
        
        if (args.length > 0 && args[0].equals("--generate-report")) {
            log.info("Starting job with jobId={}", jobId);
            memberReportingService.generateReport(jobId);
        } else if (args.length > 0 && args[0].equals("--get-status")) {
            log.info("Get job status with job id={}", jobId);
            memberReportingService.getJobId(jobId);
        }
    }
}
