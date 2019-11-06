public class TestClass implements TestInterface {

    @Override
    public void testMethod() {
        System.out.println("class method");
    }

    @Override
    public void testMethod2() {
        System.out.println("class method2");
    }

    public static void main(String[] args) {
        TestInterface testInterface = new TestClass();
        testInterface.testMethod();
        testInterface.testMethod2();
    }
}