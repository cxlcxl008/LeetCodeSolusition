public class TestClass implements TestInterface {

    //@Override
    //public void testMethod() {
    //    System.out.println("class method");
    //}

    @Override
    public void testMethod2() {
        System.out.println("class method2");
    }

    public static void main(String[] args) {
        //TestInterface testInterface = new TestClass();
        //testInterface.testMethod();
        //testInterface.testMethod2();
        //String a = "a.b.c.d.e.f.g";
        //System.out.println(a.split(".").length);
        int id = 13776843;
        System.out.println(id - (id / 128) * 128);
    }
}