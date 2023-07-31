package dev.fernandohenning.springcoreproject.repository;

import dev.fernandohenning.springcoreproject.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.isExpired = false or t.isRevoked = false)\s
            """)
    List<Token> findAllValidTokenByUser(Long id);

    @Query("""
            SELECT t FROM Token t
            WHERE t.token = :token
             """)
    Optional<Token> findByToken(String token);
}
