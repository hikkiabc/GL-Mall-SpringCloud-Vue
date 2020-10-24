package com.glmall.member;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class MemberApplicationTests {
class P{
    String name;

    public P(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "P{" +
                "name='" + name + '\'' +
                '}';
    }
}
    @Test
    void contextLoads() {
        P a=new P("a");
        P b=new P("b");

        Stream.of(a, b).peek(u -> u.setName("bcc"))
                .forEach(System.out::println);
    }
    @Test
    void test1(){
        Stream.of("one", "two", "three","four").filter(e -> e.length() > 3)
                .peek(e -> System.out.println("Filtered value: " + e))
                .map(String::toUpperCase)
                .peek(e -> System.out.println("Mapped value: " + e))
                .collect(Collectors.toList());
    }


}
