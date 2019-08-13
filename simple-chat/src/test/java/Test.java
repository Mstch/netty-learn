import java.util.Optional;

/**
 * @author zhangweichen
 * @date 2019-08-11 17:15
 */
public class Test {
    public static void main(String[] args) {
        Father f = new Son();
        System.out.println((Son)f);
    }
}

class Father{

}
class Son extends Father{

}