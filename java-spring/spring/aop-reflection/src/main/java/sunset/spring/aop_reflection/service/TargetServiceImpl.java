package sunset.spring.aop_reflection.service;

import sunset.spring.aop_reflection.infra.annotation.MyMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class TargetServiceImpl implements TargetService {

    @MyMethod
    @Override
    public int hello(String greeting, List<String> names) {
        Assert.notNull(names, "names must be not null.");
        names.forEach(name -> {
            System.out.println(greeting + ": " + name);
        });
        return names.size();
    }
}
