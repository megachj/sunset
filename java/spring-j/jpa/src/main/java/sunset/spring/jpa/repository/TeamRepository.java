package sunset.spring.jpa.repository;

import sunset.spring.jpa.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
