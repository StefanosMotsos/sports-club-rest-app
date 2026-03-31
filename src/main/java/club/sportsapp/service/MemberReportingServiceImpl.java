package club.sportsapp.service;

import club.sportsapp.dto.JobStatusDTO;
import club.sportsapp.dto.MemberStatusReportView;
import club.sportsapp.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
@RequiredArgsConstructor
public class MemberReportingServiceImpl implements IMemberReportingService{
    private final Map<String, JobStatusDTO> jobStatusMap = new ConcurrentHashMap<>();
    private final MemberRepository memberRepository;


    @Async
    @Override
    @Transactional(readOnly = true)
    public void generateReport(String jobId) {
        jobStatusMap.put(jobId, JobStatusDTO.withoutData(jobId, "IN_PROGRESS"));

        try {
            List<MemberStatusReportView> report = memberRepository.findAllMembersReport();

            jobStatusMap.put(jobId, new JobStatusDTO(jobId , "COMPLETED" ,report));
            log.info("Report generated for jobId={}, records={}", jobId, report.size());
        } catch (Exception e) {
            jobStatusMap.put(jobId, JobStatusDTO.withoutData(jobId, "FAILED"));
            log.error("Failed to generate report with jobId={}", jobId);
            throw new RuntimeException(e);
        }
    }

    @Override
    public JobStatusDTO getJobId(String jobId) {
        return jobStatusMap.get(jobId);
    }
}
