package sunset.spring.jpa.repository;

import sunset.spring.jpa.datasource.DatabaseConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseConfig.class})
public abstract class DataTestSupport {

    @Autowired
    protected EntityManagerFactory emf;

    protected <T> T save(T entity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            em.persist(entity);
            em.flush();
            transaction.commit();
            em.clear();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close(); // EntityManager 를 close 해야 커넥션 풀에 반환
        }
        return entity;
    }

    protected <T> Iterable<T> saveAll(Iterable<T> entities) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        transaction.begin();
        try {
            for (T entity : entities) {
                em.persist(entity);
            }
            em.flush();
            transaction.commit();
            em.clear();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            em.close(); // EntityManager 를 close 해야 커넥션 풀에 반환
        }
        return entities;
    }
}
