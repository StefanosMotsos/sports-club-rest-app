package club.sportsapp.repository;

import club.sportsapp.dto.MemberStatusReportView;
import club.sportsapp.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, Long>,
        JpaSpecificationExecutor<Member> {

    Optional<Member> findByUuid(UUID uuid);
    Optional<Member> findByVat(String vat);
    Optional<Member> findByPersonalInfo_MembershipId(String membershipId);

    Optional<Member> findByUuidAndDeletedFalse(UUID uuid);
    Optional<Member> findByVatAndDeletedFalse(String vat);

    @EntityGraph(attributePaths = {"personalInfo", "sport"})
    Page<Member> findAllByDeletedFalse(Pageable pageable);

    boolean existsByUuidAndUser_Uuid(UUID memberUuid, UUID userUuid);

    @Query(value = """
    SELECT
        s.name AS athlima,
        m.firstname AS onoma,
        m.lastname AS eponymo,
        pi.membership_Id AS membership,
        m.vat AS afm,
            CASE WHEN m.deleted = 1 THEN 'ΔΙΕΓΡΑΜΜΕΝΟΣ' ELSE 'ΕΝΕΡΓΟΣ' END AS katastasi,
            CASE
                WHEN m.created_at > '2025-01-01' THEN 'NEW MEMBER'
                WHEN m.created_at > '2023-01-01' THEN 'ACTIVE'
                WHEN m.created_at > '2020-01-01' THEN 'LOYAL'
                ELSE 'VETERAN'
            END AS epipedo
    FROM members m
    JOIN personal_information pi ON m.personal_info_id = pi.id
    JOIN sports s ON m.sport_id = s.id
    WHERE m.deleted = 0
    ORDER BY m.deleted DESC, s.name
    """, nativeQuery = true)
    List<MemberStatusReportView> findAllMembersReport();
}
