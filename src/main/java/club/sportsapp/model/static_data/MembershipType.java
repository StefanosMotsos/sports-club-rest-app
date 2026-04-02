package club.sportsapp.model.static_data;

import club.sportsapp.model.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "membership_type")
public class MembershipType{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "membershipType", fetch = FetchType.LAZY)
    private Set<Member> members;

    public Set<Member> getAllMembers() {
        return Collections.unmodifiableSet(members);
    }

    public void addMember(Member member) {
        members.add(member);
        member.setMembershipType(this);
    }

    public void removeMember(Member member) {
        if (members == null) return;
        members.remove(member);
        member.setMembershipType(null);
    }
}
