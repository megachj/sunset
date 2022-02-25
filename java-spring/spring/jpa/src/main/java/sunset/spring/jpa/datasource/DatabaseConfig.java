package sunset.spring.jpa.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Slf4j
@EntityScan(basePackages = "com.sunset.spring")
@EnableJpaRepositories(basePackages = "com.sunset.spring") // ?
@EnableTransactionManagement // ?
@Configuration
@EnableConfigurationProperties(JpaProperties.class)
public class DatabaseConfig {

    @Primary
    @Bean(name = "baseDataSource")
    public DataSource baseDataSource(@Value("${sunset.datasource.base.driver-class-name}") String driverClassName,
                                     @Value("${sunset.datasource.base.jdbc-url}") String jdbcUrl,
                                     @Value("${sunset.datasource.base.username}") String username,
                                     @Value("${sunset.datasource.base.password}") String password) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("baseDataSource") DataSource dataSource,
                                                                       JpaProperties jpaProperties // spring.jpa.properties 설정 값
    ) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.sunset.spring");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(jpaProperties.getProperties());

        log.info("\n--------------------------------------------------------------------------------------------\n"
                + "jpaPropertyMap: {}\n"
                + "--------------------------------------------------------------------------------------------", emf.getJpaPropertyMap());

        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
        return new JpaTransactionManager(emf.getObject());
    }
}
