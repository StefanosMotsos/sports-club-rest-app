package club.sportsapp.service;

import club.sportsapp.dto.JobStatusDTO;

public interface IMemberReportingService {
    void generateReport(String jobId);
    JobStatusDTO getJobId(String jobId);
}
