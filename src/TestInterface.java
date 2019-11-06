import java.util.List;

public interface TestInterface{

    default void testMethod(){
        System.out.println("default method");
    }

    void testMethod2();



}