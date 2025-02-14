package homework;

import java.util.*;

public class CustomerService {

    private final NavigableMap<Customer, String> entries =
            new TreeMap<>((o1, o2) -> (int) (o1.getScores() - o2.getScores()));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> smallest = entries.firstEntry();
        Customer smallestCustomer = smallest.getKey();
        return new AbstractMap.SimpleEntry<>(
                new Customer(smallestCustomer.getId(), smallestCustomer.getName(), smallestCustomer.getScores()),
                smallest.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> next = entries.higherEntry(customer);
        if (next == null) {
            return null;
        }
        Customer nextCustomer = next.getKey();
        return new AbstractMap.SimpleEntry<>(
                new Customer(nextCustomer.getId(), nextCustomer.getName(), nextCustomer.getScores()), next.getValue());
    }

    public void add(Customer customer, String data) {
        entries.put(customer, data);
    }
}
