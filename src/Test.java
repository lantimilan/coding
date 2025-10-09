import java.util.*;

public class Test {

    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }

        Queue<String> queue = new LinkedList<>();
        queue.offer("a");
        queue.offer("b");
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        //list.sort(Comparator.comparingInt(a -> a));
        list.sort(null);
        System.out.println(list);
    }

    class Record implements Comparable<Record> {
        String name;
        int age;
        public Record(String name, int age) { this.name = name; this.age = age; }

        public int compareTo(Record o) {
            return this.age - o.age;
        }
    }
}
