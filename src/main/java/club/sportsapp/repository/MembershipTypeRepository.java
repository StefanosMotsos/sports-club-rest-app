package club.sportsapp.repository;

import club.sportsapp.model.static_data.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTypeRepository extends JpaRepository<MembershipType, Long> {
}
